'use client';

import React, { Suspense, useState } from "react";
import { useSearchParams } from "next/navigation";

function ResultContent() {
    const searchParams = useSearchParams();
    const paymentKey = searchParams.get('paymentKey');
    const orderId = searchParams.get('orderId');
    const amount = searchParams.get('amount');
    const errorCode = searchParams.get('code');
    const errorMessage = searchParams.get('message');

    const [confirmResult, setConfirmResult] = useState<string>('');

    const handleConfirm = async () => {
        if (!paymentKey || !orderId || !amount) {
            alert('paymentKey/orderId/amount가 없어. 결제가 실패했거나 잘못 진입한 페이지야.');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/api/v1/orders/toss/payment/auth', {
                method: 'POST',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ paymentKey, orderId: Number(orderId), amount: Number(amount) }),
            });

            const result = await response.json();
            setConfirmResult(JSON.stringify(result, null, 2));
        } catch (error) {
            setConfirmResult('통신 오류: ' + String(error));
        }
    };

    return (
        <div style={{ padding: 24, maxWidth: 640 }}>
            <h1>Toss 결제 결과</h1>
            {paymentKey ? (
                <>
                    <p>결제 성공 (Toss 기준) — 아래 값을 백엔드 승인 API로 그대로 보낼 수 있어.</p>
                    <ul>
                        <li>paymentKey: <code>{paymentKey}</code></li>
                        <li>orderId: <code>{orderId}</code></li>
                        <li>amount: <code>{amount}</code></li>
                    </ul>
                    <button onClick={handleConfirm}>백엔드 결제 승인(authTossPayment) 호출</button>
                </>
            ) : (
                <>
                    <p>결제 실패 (Toss 기준)</p>
                    <ul>
                        <li>code: <code>{errorCode}</code></li>
                        <li>message: <code>{errorMessage}</code></li>
                    </ul>
                </>
            )}
            {confirmResult && (
                <pre style={{ background: '#f5f5f5', color: '#111', padding: 12, marginTop: 16, whiteSpace: 'pre-wrap' }}>
                    {confirmResult}
                </pre>
            )}
        </div>
    );
}

export default function TossTestResultPage() {
    return (
        <Suspense fallback={<div>로딩 중...</div>}>
            <ResultContent />
        </Suspense>
    );
}
