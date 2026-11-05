'use client';

import Link from "next/link";
import { Suspense, useEffect, useRef, useState } from "react";
import { useParams, useRouter, useSearchParams } from "next/navigation";
import { completeSocialLogin, isSocialProvider, providerLabel, takePendingLogin } from "@/lib/socialLogin";

type LoginResult = { next: string } | { error: string };

// state 확인과 code 전송. 결과만 돌려주고 화면 상태는 호출한 쪽에서 바꾼다
async function finishSocialLogin(providerParam: string, searchParams: URLSearchParams): Promise<LoginResult> {
    if (!isSocialProvider(providerParam)) {
        return { error: "지원하지 않는 로그인 방식입니다." };
    }

    const provider = providerParam;
    const label = providerLabel(provider);
    const code = searchParams.get("code");
    const state = searchParams.get("state");
    const pending = takePendingLogin(provider, state);

    // 사용자가 동의 화면에서 취소하면 세 회사 모두 error=access_denied로 돌아온다
    if (searchParams.get("error")) {
        return { error: `${label} 로그인을 취소했습니다.` };
    }
    if (!code || !state || !pending) {
        return { error: "로그인 요청이 만료되었거나 올바르지 않습니다. 다시 시도해주세요." };
    }

    try {
        const response = await completeSocialLogin(provider, code, state);
        return response.ok ? { next: pending.next } : { error: `${label} 로그인에 실패했습니다. 잠시 후 다시 시도해주세요.` };
    } catch {
        return { error: "통신 오류가 발생했습니다. 잠시 후 다시 시도해주세요." };
    }
}

// 카카오·네이버·구글 로그인 화면에서 돌아오는 주소. 각 회사 콘솔에 {사이트 주소}/auth/callback/{provider}로 등록한다
function CallbackContent() {
    const router = useRouter();
    const params = useParams<{ provider: string }>();
    const searchParams = useSearchParams();
    const [error, setError] = useState<string | null>(null);
    // 개발 모드(StrictMode)에서 effect가 두 번 실행돼도 state 확인과 code 전송은 한 번만
    const startedRef = useRef(false);

    useEffect(() => {
        if (startedRef.current) {
            return;
        }
        startedRef.current = true;

        finishSocialLogin(params.provider, new URLSearchParams(searchParams.toString())).then((result) => {
            if ("error" in result) {
                setError(result.error);
                return;
            }
            router.replace(result.next);
            // Header(서버 컴포넌트)가 새 쿠키 기준으로 다시 렌더링되도록 갱신
            router.refresh();
        });
    }, [params.provider, router, searchParams]);

    if (error) {
        return (
            <div className="mx-auto w-full max-w-sm px-6 py-20 text-center">
                <p role="alert" className="text-sm text-red-600">{error}</p>
                <Link href="/login" className="mt-6 inline-block text-sm font-semibold text-zinc-900 underline underline-offset-4">
                    로그인 화면으로
                </Link>
            </div>
        );
    }

    return <div className="mx-auto max-w-xl p-6 text-sm text-zinc-500">로그인하는 중...</div>;
}

export default function SocialLoginCallbackPage() {
    return (
        <Suspense fallback={<div className="mx-auto max-w-xl p-6 text-sm text-zinc-500">로그인하는 중...</div>}>
            <CallbackContent />
        </Suspense>
    );
}
