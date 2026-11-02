// Server Components/서버 사이드 fetch용 — 같은 Docker 네트워크 안에서 backend 컨테이너를 호출할 때 사용
export const SERVER_API_URL = process.env.API_INTERNAL_URL ?? "http://localhost:8080/api/v1";

// Client Components(브라우저)용 — 사용자의 브라우저가 직접 호출할 때 사용, 반드시 NEXT_PUBLIC_ 접두사 필요
export const CLIENT_API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api/v1";

// 동시에 여러 요청이 401을 받아도 재발급은 한 번만 호출 (refresh token rotation이라 중복 호출 시 두 번째가 재사용으로 간주됨)
let reissuePromise: Promise<boolean> | null = null;

export function reissueToken(): Promise<boolean> {
    if (!reissuePromise) {
        reissuePromise = fetch(`${CLIENT_API_URL}/auth/reissue`, {
            method: "POST",
            credentials: "include",
        })
            .then((res) => res.ok)
            .catch(() => false)
            .finally(() => {
                reissuePromise = null;
            });
    }
    return reissuePromise;
}

// 브라우저(Client Component)에서 로그인이 필요한 API를 호출할 때 사용
// access token 만료로 401이면 refresh token 쿠키로 재발급(POST /auth/reissue) 후 원래 요청을 한 번 재시도
export async function authFetch(input: string, init: RequestInit = {}): Promise<Response> {
    const options: RequestInit = { ...init, credentials: "include" };
    const response = await fetch(input, options);

    if (response.status !== 401) {
        return response;
    }

    const reissued = await reissueToken();
    return reissued ? fetch(input, options) : response;
}

// 서버 컴포넌트가 401을 받았을 때 보낼 재발급 페이지 경로. next는 같은 사이트 내부 경로만 허용 (오픈 리다이렉트 방지)
export function reissuePagePath(next: string): string {
    return `/auth/refresh?next=${encodeURIComponent(next)}`;
}

export function safeNextPath(next: string | null): string {
    if (!next || !next.startsWith("/") || next.startsWith("//") || next.startsWith("/\\")) {
        return "/";
    }
    return next;
}
