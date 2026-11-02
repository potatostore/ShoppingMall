'use client';

import Link from "next/link";
import { useState } from "react";
import { CLIENT_API_URL } from "@/lib/api";

const cartApiUrl = `${CLIENT_API_URL}/carts`;

type AddToCartStatus = "idle" | "success" | "unauthorized" | "error";

export default function AddToCartForm({ productId, price }: { productId: number; price: number }) {
    const [quantity, setQuantity] = useState<number>(1);
    const [submitting, setSubmitting] = useState<boolean>(false);
    const [status, setStatus] = useState<AddToCartStatus>("idle");

    const decreaseQuantity = () => {
        setQuantity((prev) => Math.max(1, prev - 1));
    };

    const increaseQuantity = () => {
        setQuantity((prev) => prev + 1);
    };

    const handleQuantityChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = Number(e.target.value);
        setQuantity(Number.isNaN(value) || value < 1 ? 1 : value);
    };

    const handleAddToCart = async () => {
        setSubmitting(true);
        setStatus("idle");

        try {
            const response = await fetch(cartApiUrl, {
                method: "POST",
                credentials: "include",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ productId, quantity }),
            });

            if (response.status === 401) {
                setStatus("unauthorized");
                return;
            }

            if (!response.ok) {
                setStatus("error");
                return;
            }

            setStatus("success");
        } catch {
            setStatus("error");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="mt-8 border-t border-zinc-200 pt-6">
            <div className="flex items-center justify-between">
                <span className="text-sm font-medium text-zinc-900">수량</span>
                <div className="flex h-10 items-center rounded-md border border-zinc-300">
                    <button
                        type="button"
                        onClick={decreaseQuantity}
                        disabled={quantity <= 1}
                        className="h-full w-10 text-zinc-700 transition hover:bg-zinc-100 disabled:text-zinc-300 disabled:hover:bg-transparent"
                        aria-label="수량 감소"
                    >
                        −
                    </button>
                    <input
                        type="number"
                        min={1}
                        value={quantity}
                        onChange={handleQuantityChange}
                        aria-label="수량"
                        className="h-full w-12 border-x border-zinc-300 text-center text-sm text-zinc-900 [appearance:textfield] focus:outline-none [&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:appearance-none"
                    />
                    <button
                        type="button"
                        onClick={increaseQuantity}
                        className="h-full w-10 text-zinc-700 transition hover:bg-zinc-100"
                        aria-label="수량 증가"
                    >
                        +
                    </button>
                </div>
            </div>

            <div className="mt-6 flex items-baseline justify-between">
                <span className="text-sm text-zinc-500">총 상품 금액</span>
                <span className="text-2xl font-bold text-zinc-900">
                    {(price * quantity).toLocaleString("ko-KR")}
                    <span className="ml-0.5 text-lg font-medium">원</span>
                </span>
            </div>

            <button
                type="button"
                onClick={handleAddToCart}
                disabled={submitting}
                className="mt-6 h-14 w-full rounded-md bg-zinc-900 text-base font-semibold text-white transition hover:bg-zinc-700 disabled:cursor-not-allowed disabled:opacity-50"
            >
                {submitting ? "담는 중..." : "장바구니 담기"}
            </button>

            <div aria-live="polite">
                {status === "success" && (
                    <div className="mt-4 flex items-center justify-between rounded-md border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm">
                        <span className="text-zinc-700">장바구니에 담았습니다.</span>
                        <Link href="/cart" className="font-semibold text-zinc-900 underline underline-offset-4">
                            장바구니 보기
                        </Link>
                    </div>
                )}
                {status === "unauthorized" && (
                    <div className="mt-4 flex items-center justify-between rounded-md border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm">
                        <span className="text-zinc-700">로그인이 필요한 서비스입니다.</span>
                        <Link href="/login" className="font-semibold text-zinc-900 underline underline-offset-4">
                            로그인하기
                        </Link>
                    </div>
                )}
                {status === "error" && (
                    <p className="mt-4 text-sm text-red-600">장바구니 담기에 실패했습니다. 잠시 후 다시 시도해주세요.</p>
                )}
            </div>
        </div>
    );
}
