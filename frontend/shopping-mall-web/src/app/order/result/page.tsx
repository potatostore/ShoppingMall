'use client';

import { Suspense, useState } from "react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { CLIENT_API_URL } from "@/lib/api";

const tossAuthUrl = `${CLIENT_API_URL}/orders/toss/payment/auth`;

function OrderResultContent() {
    const searchParams = useSearchParams();
    const paymentKey = searchParams.get("paymentKey");
    const orderId = searchParams.get("orderId");
    const amount = searchParams.get("amount");
    const errorMessage = searchParams.get("message");

    const [status, setStatus] = useState<"idle" | "confirming" | "success" | "failed">("idle");
    const [detail, setDetail] = useState<string>("");

    const handleConfirm = async () => {
        if (!paymentKey || !orderId || !amount) {
            setStatus("failed");
            setDetail("결제 정보가 없습니다.");
            return;
        }

        setStatus("confirming");
        try {
            const response = await fetch(tossAuthUrl, {
                method: "POST",
                credentials: "include",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ paymentKey, orderId: Number(orderId), amount: Number(amount) }),
            });

            if (!response.ok) {
                setStatus("failed");
                setDetail("결제 승인에 실패했습니다.");
                return;
            }

            setStatus("success");
        } catch {
            setStatus("failed");
            setDetail("통신 오류가 발생했습니다.");
        }
    };

    return (
        <div className="mx-auto max-w-xl p-6">
            <h1 className="mb-4 text-2xl font-bold">주문 결과</h1>
            {paymentKey ? (
                <>
                    <p className="mb-4 text-zinc-600">
                        결제 인증이 완료되었습니다. 아래 버튼으로 최종 승인을 진행하세요.
                    </p>
                    {status !== "success" && (
                        <button
                            onClick={handleConfirm}
                            disabled={status === "confirming"}
                            className="rounded bg-zinc-900 px-4 py-2 text-white disabled:opacity-50"
                        >
                            {status === "confirming" ? "승인 중..." : "결제 승인 확정"}
                        </button>
                    )}
                    {status === "success" && (
                        <div className="mt-4">
                            <p className="mb-3 font-medium text-green-600">결제가 완료되었습니다!</p>
                            <Link href="/" className="text-sm text-zinc-500 hover:text-zinc-900">
                                메인으로 돌아가기
                            </Link>
                        </div>
                    )}
                    {status === "failed" && <p className="mt-3 text-red-600">{detail}</p>}
                </>
            ) : (
                <>
                    <p className="text-red-600">결제가 실패했습니다.</p>
                    {errorMessage && <p className="mt-2 text-sm text-zinc-500">{errorMessage}</p>}
                    <Link href="/cart" className="mt-4 inline-block text-sm text-zinc-500 hover:text-zinc-900">
                        장바구니로 돌아가기
                    </Link>
                </>
            )}
        </div>
    );
}

export default function OrderResultPage() {
    return (
        <Suspense fallback={<div className="p-6">로딩 중...</div>}>
            <OrderResultContent />
        </Suspense>
    );
}
