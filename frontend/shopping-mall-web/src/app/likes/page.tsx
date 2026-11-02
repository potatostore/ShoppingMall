'use client';

import Link from "next/link";
import { useEffect, useState } from "react";
import { CLIENT_API_URL, authFetch } from "@/lib/api";

const myLikesUrl = `${CLIENT_API_URL}/users/me/likes`;

type LikedProduct = {
    productId: number;
    name: string;
    price: number;
    stock: number;
    likedAt: string;
};

export default function LikesPage() {
    // null = 불러오는 중, "unauthorized" = 로그인 필요
    const [likes, setLikes] = useState<LikedProduct[] | "unauthorized" | "error" | null>(null);

    useEffect(() => {
        let cancelled = false;

        authFetch(myLikesUrl)
            .then(async (res) => {
                if (cancelled) {
                    return;
                }
                if (res.status === 401) {
                    setLikes("unauthorized");
                    return;
                }
                if (!res.ok) {
                    setLikes("error");
                    return;
                }
                const { data } = await res.json();
                setLikes(data ?? []);
            })
            .catch(() => {
                if (!cancelled) {
                    setLikes("error");
                }
            });

        return () => {
            cancelled = true;
        };
    }, []);

    const handleUnlike = async (productId: number) => {
        const res = await authFetch(`${CLIENT_API_URL}/products/${productId}/likes`, { method: "DELETE" });
        if (res.ok) {
            setLikes((prev) => (Array.isArray(prev) ? prev.filter((item) => item.productId !== productId) : prev));
        }
    };

    return (
        <div className="mx-auto max-w-3xl p-6">
            <h1 className="mb-6 text-2xl font-bold">찜한 상품</h1>

            {likes === null && <p className="text-zinc-500">불러오는 중...</p>}
            {likes === "unauthorized" && (
                <p className="text-red-600">
                    로그인이 필요합니다. <Link href="/login" className="underline">로그인하러 가기</Link>
                </p>
            )}
            {likes === "error" && <p className="text-red-600">찜 목록을 불러오지 못했습니다.</p>}
            {Array.isArray(likes) && likes.length === 0 && <p className="text-zinc-500">찜한 상품이 없습니다.</p>}
            {Array.isArray(likes) && likes.length > 0 && (
                <ul className="divide-y divide-zinc-200 rounded border border-zinc-200">
                    {likes.map((item) => (
                        <li key={item.productId} className="flex items-center justify-between p-4">
                            <Link href={`/products/${item.productId}`} className="hover:underline">
                                <p className="font-medium text-zinc-900">{item.name}</p>
                                <p className="text-sm text-zinc-500">
                                    {item.price.toLocaleString("ko-KR")}원 · {item.stock > 0 ? `재고 ${item.stock}개` : "품절"}
                                </p>
                            </Link>
                            <button
                                type="button"
                                onClick={() => handleUnlike(item.productId)}
                                className="rounded border border-zinc-300 px-3 py-1 text-sm text-zinc-700 hover:bg-zinc-50"
                            >
                                찜 취소
                            </button>
                        </li>
                    ))}
                </ul>
            )}

            <Link href="/profile" className="mt-6 inline-block text-sm text-zinc-500 hover:text-zinc-900">
                ← 마이페이지로
            </Link>
        </div>
    );
}
