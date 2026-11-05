'use client';

import { useState } from "react";
import { SOCIAL_PROVIDERS, SocialLoginError, startSocialLogin, type SocialProvider } from "@/lib/socialLogin";

// 로그인·회원가입 화면 공용. 소셜 계정은 처음 로그인할 때 가입되므로 두 화면 모두 같은 흐름을 탄다
export default function SocialLoginButtons({
    providers = SOCIAL_PROVIDERS.map((provider) => provider.id),
    action = "로그인",
}: {
    providers?: SocialProvider[];
    action?: string;
}) {
    const [pending, setPending] = useState<SocialProvider | null>(null);
    const [error, setError] = useState<string | null>(null);

    const handleClick = async (provider: SocialProvider) => {
        setPending(provider);
        setError(null);
        try {
            await startSocialLogin(provider);
            // 성공하면 회사 로그인 화면으로 넘어간다. 뒤로 가기로 돌아왔을 때 버튼이 잠겨 있지 않도록 풀어 둔다
        } catch (e) {
            setError(e instanceof SocialLoginError ? e.message : "로그인을 시작하지 못했습니다. 잠시 후 다시 시도해주세요.");
        } finally {
            setPending(null);
        }
    };

    return (
        <div className="flex flex-col gap-3">
            {SOCIAL_PROVIDERS.filter((provider) => providers.includes(provider.id)).map((provider) => (
                <button
                    key={provider.id}
                    type="button"
                    onClick={() => handleClick(provider.id)}
                    disabled={pending !== null}
                    className="h-12 rounded-md border border-zinc-300 text-sm font-medium text-zinc-900 transition hover:border-zinc-900 disabled:cursor-not-allowed disabled:text-zinc-400"
                >
                    {pending === provider.id ? `${provider.label} 로그인 화면으로 이동 중...` : `${provider.label}로 ${action}`}
                </button>
            ))}
            {error && <p role="alert" className="text-sm text-red-600">{error}</p>}
        </div>
    );
}
