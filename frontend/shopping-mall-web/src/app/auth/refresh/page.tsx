'use client';

import { Suspense, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { reissueToken, safeNextPath } from "@/lib/api";

// 서버 컴포넌트(profile, orders 등)가 access token 만료로 401을 받으면 이 페이지로 보냄
// 브라우저에서 refresh token 쿠키로 재발급한 뒤 원래 페이지로 돌아감 (reissued=1로 재시도 표시 → 무한 반복 방지)
function RefreshContent() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const next = safeNextPath(searchParams.get("next"));

    useEffect(() => {
        let cancelled = false;

        // reissueToken은 진행 중인 요청을 공유하므로 StrictMode의 effect 두 번 실행에도 재발급은 한 번만 호출됨
        reissueToken().then((ok) => {
            if (cancelled) {
                return;
            }
            if (ok) {
                const separator = next.includes("?") ? "&" : "?";
                router.replace(`${next}${separator}reissued=1`);
                router.refresh();
            } else {
                router.replace("/login");
            }
        });

        return () => {
            cancelled = true;
        };
    }, [next, router]);

    return <div className="mx-auto max-w-xl p-6 text-sm text-zinc-500">로그인 상태를 확인하는 중...</div>;
}

export default function RefreshPage() {
    return (
        <Suspense fallback={<div className="mx-auto max-w-xl p-6 text-sm text-zinc-500">로그인 상태를 확인하는 중...</div>}>
            <RefreshContent />
        </Suspense>
    );
}
