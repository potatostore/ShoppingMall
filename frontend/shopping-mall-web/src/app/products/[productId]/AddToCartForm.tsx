'use client';

import { useState } from "react";
import { CLIENT_API_URL } from "@/lib/api";

const cartApiUrl = `${CLIENT_API_URL}/carts`;

export default function AddToCartForm({ productId }: { productId: number }) {
    const [quantity, setQuantity] = useState<number>(1);
    const [submitting, setSubmitting] = useState<boolean>(false);
    const [message, setMessage] = useState<string | null>(null);

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
        setMessage(null);

        try {
            const response = await fetch(cartApiUrl, {
                method: "POST",
                credentials: "include",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ productId, quantity }),
            });

            if (!response.ok) {
                setMessage("장바구니 담기에 실패했습니다. 로그인이 필요할 수 있습니다.");
                return;
            }

            setMessage("장바구니에 담았습니다.");
        } catch {
            setMessage("통신 오류가 발생했습니다.");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="mt-6">
            <div className="flex items-center gap-3">
                <span className="text-sm font-medium text-zinc-600">수량</span>
                <div className="flex items-center rounded border border-zinc-300">
                    <button
                        onClick={decreaseQuantity}
                        className="px-3 py-1 text-zinc-600 hover:bg-zinc-100"
                        aria-label="수량 감소"
                    >
                        -
                    </button>
                    <input
                        type="number"
                        min={1}
                        value={quantity}
                        onChange={handleQuantityChange}
                        className="w-14 border-x border-zinc-300 py-1 text-center"
                    />
                    <button
                        onClick={increaseQuantity}
                        className="px-3 py-1 text-zinc-600 hover:bg-zinc-100"
                        aria-label="수량 증가"
                    >
                        +
                    </button>
                </div>
            </div>

            <button
                onClick={handleAddToCart}
                disabled={submitting}
                className="mt-4 w-full rounded bg-zinc-900 py-3 text-white disabled:opacity-50 sm:w-auto sm:px-8"
            >
                {submitting ? "담는 중..." : "장바구니에 추가하기"}
            </button>

            {message && <p className="mt-3 text-sm text-zinc-600">{message}</p>}
        </div>
    );
}
