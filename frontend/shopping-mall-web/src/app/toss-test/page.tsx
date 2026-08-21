'use client';

import React, { useEffect, useState } from "react";

const TOSS_CLIENT_KEY = "test_ck_DpexMgkW36xxbXXGBwoMrGbR5ozO";

declare global {
    interface Window {
        TossPayments?: (clientKey: string) => {
            requestPayment: (method: string, params: Record<string, unknown>) => Promise<void>;
        };
    }
}

export default function TossTestPage() {
    const [orderId, setOrderId] = useState<string>('');
    const [orderName, setOrderName] = useState<string>('테스트 주문');
    const [amount, setAmount] = useState<string>('');
    const [sdkReady, setSdkReady] = useState<boolean>(false);

    useEffect(() => {
        if (window.TossPayments) {
            setSdkReady(true);
            return;
        }

        const script = document.createElement('script');
        script.src = 'https://js.tosspayments.com/v1/payment';
        script.onload = () => setSdkReady(true);
        document.head.appendChild(script);
    }, []);

    const handlePay = async () => {
        if (!window.TossPayments) {
            alert('Toss SDK가 아직 로드되지 않았어요. 잠시 후 다시 시도해줘.');
            return;
        }
        if (!orderId || !amount) {
            alert('orderId와 amount를 입력해줘. 백엔드 주문 생성 API로 만든 Order.orderId, Order.totalOrderPrice와 정확히 일치해야 승인 단계에서 통과돼.');
            return;
        }

        const tossPayments = window.TossPayments(TOSS_CLIENT_KEY);

        try {
            await tossPayments.requestPayment('카드', {
                amount: Number(amount),
                orderId,
                orderName,
                successUrl: `${window.location.origin}/toss-test/result`,
                failUrl: `${window.location.origin}/toss-test/result`,
                customerName: 'test-customer',
            });
        } catch (error) {
            console.error('Toss 결제 요청 실패', error);
        }
    };

    return (
        <div style={{ padding: 24, maxWidth: 480 }}>
            <h1>Toss 결제 테스트 (paymentKey 확보용)</h1>
            <p>
                백엔드 주문 생성 API 응답의 <b>orderUid</b>(Toss로 보내는 6~12자리 주문 식별자, DB PK인 orderId와는 다른 값)와
                <b>amount</b>(=Order.totalOrderPrice)를 아래에 그대로 입력하고 결제를 진행하면, 성공/실패 결과가 결과 페이지로 전달돼.
            </p>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                <label>
                    orderUid (주문 생성 응답의 orderUid, DB PK orderId 아님)
                    <input
                        style={{ display: 'block', width: '100%' }}
                        value={orderId}
                        onChange={(e) => setOrderId(e.target.value)}
                        placeholder="예: 384719205613"
                    />
                </label>
                <label>
                    orderName
                    <input
                        style={{ display: 'block', width: '100%' }}
                        value={orderName}
                        onChange={(e) => setOrderName(e.target.value)}
                    />
                </label>
                <label>
                    amount (Order.totalOrderPrice와 정확히 일치해야 함)
                    <input
                        style={{ display: 'block', width: '100%' }}
                        value={amount}
                        onChange={(e) => setAmount(e.target.value)}
                        placeholder="예: 30000"
                    />
                </label>
                <button onClick={handlePay} disabled={!sdkReady}>
                    {sdkReady ? '테스트 결제 진행' : 'SDK 로딩 중...'}
                </button>
            </div>
        </div>
    );
}
