'use client';

import { useState } from "react";
import { useRouter } from "next/navigation";
import { CLIENT_API_URL, authFetch } from "@/lib/api";

const orderApiUrl = `${CLIENT_API_URL}/orders`;

// 결제 전(PENDING)·결제 완료(PAID) 주문 취소. PAID는 서버가 토스 결제 취소(환불)까지 처리
export default function CancelOrderButton({ orderId, paid }: { orderId: number; paid: boolean }) {
    const router = useRouter();
    const [submitting, setSubmitting] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    const handleCancel = async () => {
        const message = paid ? "주문을 취소하고 결제를 환불할까요?" : "주문을 취소할까요?";
        if (!window.confirm(message)) {
            return;
        }

        setSubmitting(true);
        setError(null);
        try {
            const response = await authFetch(`${orderApiUrl}/${orderId}/cancel`, { method: "POST" });

            if (!response.ok) {
                const body = await response.json().catch(() => null);
                setError(body?.message ?? "주문 취소에 실패했습니다.");
                return;
            }

            router.refresh();
        } catch {
            setError("통신 오류가 발생했습니다.");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="flex flex-col items-end gap-1">
            <button
                type="button"
                onClick={handleCancel}
                disabled={submitting}
                className="rounded border border-zinc-300 px-3 py-1 text-sm text-zinc-700 hover:bg-zinc-50 disabled:opacity-50"
            >
                {submitting ? "취소 중..." : paid ? "주문 취소·환불" : "주문 취소"}
            </button>
            {error && <p role="alert" className="text-xs text-red-600">{error}</p>}
        </div>
    );
}
