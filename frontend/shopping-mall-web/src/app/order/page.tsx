'use client';

import Link from "next/link";
import { useEffect, useState } from "react";

const productApiUrl = "http://localhost:8080/api/v1/products";
const orderApiUrl = "http://localhost:8080/api/v1/orders";

const CHECKOUT_ITEMS_KEY = "checkoutItems";
const TOSS_CLIENT_KEY = "test_ck_DpexMgkW36xxbXXGBwoMrGbR5ozO";

declare global {
    interface Window {
        TossPayments?: (clientKey: string) => {
            requestPayment: (method: string, params: Record<string, unknown>) => Promise<void>;
        };
    }
}

type CheckoutItem = {
    productId: number;
    quantity: number;
};

type Product = {
    productId: number;
    name: string;
    price: number;
};

export default function OrderPage() {
    const [checkoutItems, setCheckoutItems] = useState<CheckoutItem[]>([]);
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [sdkReady, setSdkReady] = useState<boolean>(false);
    const [processing, setProcessing] = useState<boolean>(false);

    useEffect(() => {
        const loadOrderSummary = async () => {
            try {
                const raw = sessionStorage.getItem(CHECKOUT_ITEMS_KEY);
                const parsed: CheckoutItem[] = raw ? JSON.parse(raw) : [];

                if (parsed.length === 0) {
                    setError("장바구니에서 결제할 상품을 먼저 선택해주세요.");
                    return;
                }

                const productRes = await fetch(productApiUrl);
                const { data: productData } = await productRes.json();

                setCheckoutItems(parsed);
                setProducts(productData ?? []);
            } catch {
                setError("통신 오류가 발생했습니다.");
            } finally {
                setLoading(false);
            }
        };

        loadOrderSummary();
    }, []);

    useEffect(() => {
        if (window.TossPayments) {
            setSdkReady(true);
            return;
        }

        const script = document.createElement("script");
        script.src = "https://js.tosspayments.com/v1/payment";
        script.onload = () => setSdkReady(true);
        document.head.appendChild(script);
    }, []);

    const productMap = new Map(products.map((p) => [p.productId, p]));

    const totalPrice = checkoutItems.reduce((sum, item) => {
        const product = productMap.get(item.productId);
        return sum + (product ? product.price * item.quantity : 0);
    }, 0);

    const handlePayWithToss = async () => {
        if (checkoutItems.length === 0) {
            alert("결제할 상품이 없습니다.");
            return;
        }
        if (!sdkReady || !window.TossPayments) {
            alert("결제 SDK가 아직 로드되지 않았습니다. 잠시 후 다시 시도해주세요.");
            return;
        }

        setProcessing(true);
        try {
            const orderItemCreateDTOList = checkoutItems.map((item) => {
                const product = productMap.get(item.productId);
                return {
                    productId: item.productId,
                    curOrderItemPrice: product?.price ?? 0,
                    quantity: item.quantity,
                };
            });

            const response = await fetch(orderApiUrl, {
                method: "POST",
                credentials: "include",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ orderItemCreateDTOList }),
            });

            if (!response.ok) {
                alert("주문 생성에 실패했습니다.");
                return;
            }

            const { data: order } = await response.json();

            const firstProduct = productMap.get(checkoutItems[0].productId);
            const orderName = checkoutItems.length > 1
                ? `${firstProduct?.name ?? "상품"} 외 ${checkoutItems.length - 1}건`
                : (firstProduct?.name ?? "주문 상품");

            const tossPayments = window.TossPayments(TOSS_CLIENT_KEY);
            await tossPayments.requestPayment("카드", {
                amount: order.totalOrderPrice,
                orderId: String(order.orderUid),
                orderName,
                successUrl: `${window.location.origin}/order/result`,
                failUrl: `${window.location.origin}/order/result`,
                customerName: "구매자",
            });

            sessionStorage.removeItem(CHECKOUT_ITEMS_KEY);
        } catch (error) {
            console.error("결제 요청 실패", error);
        } finally {
            setProcessing(false);
        }
    };

    const handlePayWithKakao = () => {
        alert("카카오페이는 추후 지원 예정입니다.");
    };

    const handlePayWithNaver = () => {
        alert("네이버페이는 추후 지원 예정입니다.");
    };

    if (loading) {
        return <div className="p-6">불러오는 중...</div>;
    }

    if (error) {
        return (
            <div className="p-6">
                <p className="text-red-600">{error}</p>
                <Link href="/cart" className="mt-4 inline-block text-sm text-zinc-500 hover:text-zinc-900">
                    ← 장바구니로 돌아가기
                </Link>
            </div>
        );
    }

    return (
        <div className="mx-auto max-w-3xl p-6">
            <h1 className="mb-6 text-2xl font-bold">주문/결제</h1>

            <ul className="divide-y divide-zinc-200">
                {checkoutItems.map((item) => {
                    const product = productMap.get(item.productId);
                    return (
                        <li key={item.productId} className="flex items-center justify-between py-4">
                            <div>
                                <p className="font-medium">{product?.name ?? `상품 #${item.productId}`}</p>
                                <p className="text-sm text-zinc-500">수량 {item.quantity}개</p>
                            </div>
                            <p className="font-semibold">
                                {product ? (product.price * item.quantity).toLocaleString() : "-"}원
                            </p>
                        </li>
                    );
                })}
            </ul>

            <div className="mt-6 flex items-center justify-between border-t border-zinc-200 pt-4">
                <span className="text-lg font-bold">결제 금액</span>
                <span className="text-lg font-bold">{totalPrice.toLocaleString()}원</span>
            </div>

            <div className="mt-8">
                <p className="mb-3 text-sm font-medium text-zinc-600">결제 방식을 선택하세요</p>
                <div className="flex flex-col gap-3">
                    <button
                        onClick={handlePayWithToss}
                        disabled={processing || checkoutItems.length === 0}
                        className="rounded bg-zinc-900 py-3 text-white disabled:opacity-50"
                    >
                        {processing ? "결제 진행 중..." : "토스페이로 결제하기"}
                    </button>
                    <button onClick={handlePayWithKakao} className="rounded border border-zinc-300 py-3">
                        카카오페이로 결제하기
                    </button>
                    <button onClick={handlePayWithNaver} className="rounded border border-zinc-300 py-3">
                        네이버페이로 결제하기
                    </button>
                </div>
            </div>
        </div>
    );
}
