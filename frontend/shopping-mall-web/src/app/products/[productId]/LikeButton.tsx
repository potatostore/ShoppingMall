'use client';

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { CLIENT_API_URL, authFetch } from "@/lib/api";

type LikeStatus = {
    productId: number;
    liked: boolean;
    likeCount: number;
};

// 찜하기 토글. 비로그인(401)이면 버튼을 누를 때 로그인 페이지로 이동
export default function LikeButton({ productId }: { productId: number }) {
    const router = useRouter();
    const likeUrl = `${CLIENT_API_URL}/products/${productId}/likes`;
    const [status, setStatus] = useState<LikeStatus | null>(null);
    const [loggedIn, setLoggedIn] = useState<boolean>(true);
    const [submitting, setSubmitting] = useState<boolean>(false);

    useEffect(() => {
        let cancelled = false;

        authFetch(`${likeUrl}/me`)
            .then(async (res) => {
                if (cancelled) {
                    return;
                }
                if (res.status === 401) {
                    setLoggedIn(false);
                    return;
                }
                if (res.ok) {
                    const { data } = await res.json();
                    setStatus(data);
                }
            })
            .catch(() => {});

        return () => {
            cancelled = true;
        };
    }, [likeUrl]);

    const handleToggle = async () => {
        if (!loggedIn) {
            router.push("/login");
            return;
        }

        setSubmitting(true);
        try {
            const res = await authFetch(likeUrl, { method: status?.liked ? "DELETE" : "POST" });
            if (res.status === 401) {
                router.push("/login");
                return;
            }
            if (res.ok) {
                const { data } = await res.json();
                setStatus(data);
            }
        } finally {
            setSubmitting(false);
        }
    };

    const liked = status?.liked ?? false;

    return (
        <button
            type="button"
            onClick={handleToggle}
            disabled={submitting}
            aria-pressed={liked}
            className="mt-4 inline-flex items-center gap-2 rounded-md border border-zinc-300 px-4 py-2 text-sm text-zinc-700 transition hover:bg-zinc-50 disabled:opacity-50"
        >
            <span aria-hidden>{liked ? "♥" : "♡"}</span>
            <span>{liked ? "찜 취소" : "찜하기"}</span>
            {status && <span className="text-zinc-400">{status.likeCount.toLocaleString("ko-KR")}</span>}
        </button>
    );
}
