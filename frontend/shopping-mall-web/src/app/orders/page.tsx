import Link from "next/link";
import { cookies } from "next/headers";
import { SERVER_API_URL } from "@/lib/api";

const orderApiUrl = `${SERVER_API_URL}/orders`;
const productApiUrl = `${SERVER_API_URL}/products`;

type OrderItem = {
    productId: number;
    curOrderItemPrice: number;
    quantity: number;
    totalOrderItemPrice: number;
};

type Order = {
    orderId: number;
    orderUid: number;
    orderItemResponseDTOList: OrderItem[];
    totalOrderPrice: number;
};

type Product = {
    productId: number;
    name: string;
    price: number;
};

async function getMyOrders(): Promise<Order[] | null> {
    const cookieStore = await cookies();

    const res = await fetch(`${orderApiUrl}/user/me`, {
        cache: "no-store",
        headers: { Cookie: cookieStore.toString() },
    });

    if (!res.ok) {
        return null;
    }

    const { data } = await res.json();
    return data;
}

async function getProducts(): Promise<Product[]> {
    const res = await fetch(productApiUrl, { cache: "no-store" });

    if (!res.ok) {
        return [];
    }

    const { data } = await res.json();
    return data;
}

export default async function OrdersPage() {
    const [orders, products] = await Promise.all([getMyOrders(), getProducts()]);

    if (!orders) {
        return (
            <div className="mx-auto max-w-3xl p-6">
                <p className="text-red-600">로그인이 필요합니다.</p>
                <Link href="/login" className="mt-4 inline-block text-sm text-zinc-500 hover:text-zinc-900">
                    로그인하러 가기
                </Link>
            </div>
        );
    }

    const productMap = new Map(products.map((p) => [p.productId, p]));

    return (
        <div className="mx-auto max-w-3xl p-6">
            <h1 className="mb-6 text-2xl font-bold">주문 내역</h1>

            {orders.length === 0 ? (
                <p className="text-zinc-500">아직 주문 내역이 없습니다.</p>
            ) : (
                <ul className="space-y-6">
                    {orders.map((order) => (
                        <li key={order.orderId} className="rounded border border-zinc-200 p-5">
                            <p className="mb-3 text-sm text-zinc-500">주문번호 {order.orderUid}</p>

                            <ul className="divide-y divide-zinc-100">
                                {order.orderItemResponseDTOList.map((item) => {
                                    const product = productMap.get(item.productId);
                                    return (
                                        <li key={item.productId} className="flex items-center justify-between py-2">
                                            <div>
                                                <p className="font-medium">{product?.name ?? `상품 #${item.productId}`}</p>
                                                <p className="text-sm text-zinc-500">
                                                    {item.curOrderItemPrice.toLocaleString()}원 x {item.quantity}개
                                                </p>
                                            </div>
                                            <p className="font-semibold">{item.totalOrderItemPrice.toLocaleString()}원</p>
                                        </li>
                                    );
                                })}
                            </ul>

                            <div className="mt-3 flex items-center justify-between border-t border-zinc-200 pt-3">
                                <span className="font-bold">총 결제금액</span>
                                <span className="font-bold">{order.totalOrderPrice.toLocaleString()}원</span>
                            </div>
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
