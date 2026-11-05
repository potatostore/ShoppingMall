import { CLIENT_API_URL, safeNextPath } from "@/lib/api";

// 소셜 로그인 공통 흐름 (세 회사 모두 authorization code 방식)
// 1. 버튼 클릭 → 각 회사 로그인 화면으로 이동 (카카오·구글은 공식 SDK, 네이버는 공식 인가 URL)
// 2. 로그인·동의 후 /auth/callback/{provider}?code=...&state=... 로 돌아옴
// 3. state가 이 브라우저에서 시작한 요청과 같은지 확인 (CSRF 방지)
// 4. code를 백엔드 POST /auth/oauth/{provider}로 보내면, 백엔드가 비밀키로 토큰 교환·사용자 조회 후 우리 JWT 쿠키를 발급
// 회사 토큰은 브라우저에 남기지 않는다. code는 수명이 짧고 한 번만 쓸 수 있어 노출돼도 비밀키 없이는 쓸 수 없다

export type SocialProvider = "kakao" | "naver" | "google";

export const SOCIAL_PROVIDERS: { id: SocialProvider; label: string }[] = [
    { id: "kakao", label: "카카오" },
    { id: "naver", label: "네이버" },
    { id: "google", label: "구글" },
];

export function isSocialProvider(value: string): value is SocialProvider {
    return SOCIAL_PROVIDERS.some((provider) => provider.id === value);
}

export function providerLabel(provider: SocialProvider): string {
    return SOCIAL_PROVIDERS.find((item) => item.id === provider)?.label ?? provider;
}

// 각 회사 개발자 콘솔에서 발급받는 공개 키. 비밀키(client secret)는 백엔드에만 둔다
const CLIENT_IDS: Record<SocialProvider, { value: string | undefined; envName: string }> = {
    kakao: { value: process.env.NEXT_PUBLIC_KAKAO_JS_KEY, envName: "NEXT_PUBLIC_KAKAO_JS_KEY" },
    naver: { value: process.env.NEXT_PUBLIC_NAVER_CLIENT_ID, envName: "NEXT_PUBLIC_NAVER_CLIENT_ID" },
    google: { value: process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID, envName: "NEXT_PUBLIC_GOOGLE_CLIENT_ID" },
};

const KAKAO_SDK_URL = "https://t1.kakaocdn.net/kakao_js_sdk/2.7.4/kakao.min.js";
const GOOGLE_SDK_URL = "https://accounts.google.com/gsi/client";
const NAVER_AUTHORIZE_URL = "https://nid.naver.com/oauth2.0/authorize";

const PENDING_KEY = "socialLoginPending";
// 회사 로그인 화면에서 너무 오래 머문 요청은 버린다 (code 수명보다 넉넉하게)
const PENDING_MAX_AGE_MS = 10 * 60 * 1000;

type PendingLogin = {
    provider: SocialProvider;
    state: string;
    next: string;
    startedAt: number;
};

type KakaoSdk = {
    init: (appKey: string) => void;
    isInitialized: () => boolean;
    Auth: { authorize: (settings: { redirectUri: string; state?: string }) => void };
};

type GoogleSdk = {
    accounts: {
        oauth2: {
            initCodeClient: (config: {
                client_id: string;
                scope: string;
                ux_mode: "redirect" | "popup";
                redirect_uri: string;
                state?: string;
            }) => { requestCode: () => void };
        };
    };
};

declare global {
    interface Window {
        Kakao?: KakaoSdk;
        google?: GoogleSdk;
    }
}

export class SocialLoginError extends Error {}

// 회사 로그인 화면이 돌아올 우리 주소. 각 회사 콘솔에 이 주소를 그대로 등록해야 한다
export function callbackUrl(provider: SocialProvider): string {
    return `${window.location.origin}/auth/callback/${provider}`;
}

function requireClientId(provider: SocialProvider): string {
    const { value, envName } = CLIENT_IDS[provider];
    if (!value) {
        throw new SocialLoginError(`${providerLabel(provider)} 로그인 설정이 없습니다. (${envName})`);
    }
    return value;
}

function createState(): string {
    const bytes = new Uint8Array(16);
    crypto.getRandomValues(bytes);
    return Array.from(bytes, (byte) => byte.toString(16).padStart(2, "0")).join("");
}

const scriptPromises = new Map<string, Promise<void>>();

function loadScript(src: string): Promise<void> {
    const cached = scriptPromises.get(src);
    if (cached) {
        return cached;
    }

    const promise = new Promise<void>((resolve, reject) => {
        const script = document.createElement("script");
        script.src = src;
        script.async = true;
        script.crossOrigin = "anonymous";
        script.onload = () => resolve();
        script.onerror = () => {
            scriptPromises.delete(src);
            script.remove();
            reject(new SocialLoginError("로그인 모듈을 불러오지 못했습니다. 잠시 후 다시 시도해주세요."));
        };
        document.head.appendChild(script);
    });

    scriptPromises.set(src, promise);
    return promise;
}

// 버튼 클릭 시 호출. 성공하면 페이지가 회사 로그인 화면으로 넘어가므로 돌아오지 않는다
export async function startSocialLogin(provider: SocialProvider, next = "/"): Promise<void> {
    const clientId = requireClientId(provider);
    const state = createState();
    const redirectUri = callbackUrl(provider);

    const pending: PendingLogin = { provider, state, next: safeNextPath(next), startedAt: Date.now() };
    sessionStorage.setItem(PENDING_KEY, JSON.stringify(pending));

    if (provider === "kakao") {
        await loadScript(KAKAO_SDK_URL);
        const kakao = window.Kakao;
        if (!kakao) {
            throw new SocialLoginError("카카오 로그인 모듈을 불러오지 못했습니다.");
        }
        if (!kakao.isInitialized()) {
            kakao.init(clientId);
        }
        kakao.Auth.authorize({ redirectUri, state });
        return;
    }

    if (provider === "google") {
        await loadScript(GOOGLE_SDK_URL);
        const google = window.google;
        if (!google) {
            throw new SocialLoginError("구글 로그인 모듈을 불러오지 못했습니다.");
        }
        google.accounts.oauth2
            .initCodeClient({
                client_id: clientId,
                scope: "openid email profile",
                ux_mode: "redirect",
                redirect_uri: redirectUri,
                state,
            })
            .requestCode();
        return;
    }

    // 네이버 JS SDK는 토큰을 브라우저로 바로 받는 방식(implicit)만 지원해서, 다른 두 곳과 같은 code 방식을 쓰려고 공식 인가 URL로 보낸다
    const params = new URLSearchParams({
        response_type: "code",
        client_id: clientId,
        redirect_uri: redirectUri,
        state,
    });
    window.location.assign(`${NAVER_AUTHORIZE_URL}?${params.toString()}`);
}

// 콜백에서 한 번만 꺼내 쓴다. 다른 회사·다른 탭에서 시작했거나 오래된 요청이면 null
export function takePendingLogin(provider: SocialProvider, state: string | null): PendingLogin | null {
    const raw = sessionStorage.getItem(PENDING_KEY);
    sessionStorage.removeItem(PENDING_KEY);
    if (!raw || !state) {
        return null;
    }

    try {
        const pending = JSON.parse(raw) as PendingLogin;
        const fresh = Date.now() - pending.startedAt <= PENDING_MAX_AGE_MS;
        return pending.provider === provider && pending.state === state && fresh ? pending : null;
    } catch {
        return null;
    }
}

const completePromises = new Map<string, Promise<Response>>();

// code는 한 번만 쓸 수 있으므로, 같은 code로 두 번 요청하지 않게 진행 중인 요청을 공유한다 (StrictMode의 effect 두 번 실행 대비)
export function completeSocialLogin(provider: SocialProvider, code: string, state: string): Promise<Response> {
    const cached = completePromises.get(code);
    if (cached) {
        return cached;
    }

    const promise = fetch(`${CLIENT_API_URL}/auth/oauth/${provider}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ code, state, redirectUri: callbackUrl(provider) }),
    });

    completePromises.set(code, promise);
    return promise;
}
