'use client';

import Link from "next/link";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { CLIENT_API_URL } from "@/lib/api";

const cartApiUrl = `${CLIENT_API_URL}/carts`;
const productApiUrl = `${CLIENT_API_URL}/products`;

const CHECKOUT_ITEMS_KEY = "checkoutItems";

type CartItem = {
    cartItemId: number;
    productItemId: number;
    quantity: number;
};

type Cart = {
    userId: number;
    cartItemList: CartItem[];
    totalCartPrice: number;
};

type Product = {
    productId: number;
    name: string;
    price: number;
};

type CartLineItem = {
    cartItemId: number;
    productItemId: number;
    quantity: number;
    checked: boolean;
};

export default function CartPage() {
    const router = useRouter();
    const [items, setItems] = useState<CartLineItem[]>([]);
    const [originalQuantities, setOriginalQuantities] = useState<Map<number, number>>(new Map());
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [checkingOut, setCheckingOut] = useState<boolean>(false);

    useEffect(() => {
        const loadCart = async () => {
            try {
                const [cartRes, productRes] = await Promise.all([
                    fetch(`${cartApiUrl}/me`, { credentials: "include" }),
                    fetch(productApiUrl),
                ]);

                if (!cartRes.ok) {
                    setError("장바구니를 불러오지 못했습니다. 로그인이 필요할 수 있습니다.");
                    return;
                }

                const { data: cartData }: { data: Cart } = await cartRes.json();
                const { data: productData } = await productRes.json();

                setItems(
                    cartData.cartItemList.map((item) => ({
                        cartItemId: item.cartItemId,
                        productItemId: item.productItemId,
                        quantity: item.quantity,
                        checked: true,
                    }))
                );
                setOriginalQuantities(
                    new Map(cartData.cartItemList.map((item) => [item.cartItemId, item.quantity]))
                );
                setProducts(productData ?? []);
            } catch {
                setError("통신 오류가 발생했습니다.");
            } finally {
                setLoading(false);
            }
        };

        loadCart();
    }, []);

    const productMap = new Map(products.map((p) => [p.productId, p]));

    const toggleChecked = (cartItemId: number) => {
        setItems((prev) =>
            prev.map((item) =>
                item.cartItemId === cartItemId ? { ...item, checked: !item.checked } : item
            )
        );
    };

    const changeQuantity = (cartItemId: number, quantity: number) => {
        const nextQuantity = Number.isNaN(quantity) || quantity < 1 ? 1 : quantity;
        setItems((prev) =>
            prev.map((item) =>
                item.cartItemId === cartItemId ? { ...item, quantity: nextQuantity } : item
            )
        );
    };

    const handleDeleteItem = async (cartItemId: number, productItemId: number) => {
        try {
            const response = await fetch(`${cartApiUrl}/items/${productItemId}`, {
                method: "DELETE",
                credentials: "include",
            });

            if (!response.ok) {
                alert("삭제에 실패했습니다.");
                return;
            }

            setItems((prev) => prev.filter((item) => item.cartItemId !== cartItemId));
            setOriginalQuantities((prev) => {
                const next = new Map(prev);
                next.delete(cartItemId);
                return next;
            });
        } catch {
            alert("통신 오류가 발생했습니다.");
        }
    };

    const selectedItems = items.filter((item) => item.checked);
    const selectedTotal = selectedItems.reduce((sum, item) => {
        const product = productMap.get(item.productItemId);
        return sum + (product ? product.price * item.quantity : 0);
    }, 0);

    const handleCheckout = async () => {
        if (selectedItems.length === 0) {
            alert("결제할 상품을 선택해주세요.");
            return;
        }

        setCheckingOut(true);
        try {
            const changedItems = items.filter(
                (item) => originalQuantities.get(item.cartItemId) !== item.quantity
            );

            if (changedItems.length > 0) {
                const patchRes = await fetch(`${cartApiUrl}/me`, {
                    method: "PATCH",
                    credentials: "include",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        cartItemUpdateDTOList: changedItems.map((item) => ({
                            productId: item.productItemId,
                            quantity: item.quantity,
                        })),
                    }),
                });

                if (!patchRes.ok) {
                    alert("변경한 수량을 저장하지 못했습니다.");
                    return;
                }
            }

            const checkoutItems = selectedItems.map((item) => ({
                productId: item.productItemId,
                quantity: item.quantity,
            }));

            sessionStorage.setItem(CHECKOUT_ITEMS_KEY, JSON.stringify(checkoutItems));
            router.push("/order");
        } catch {
            alert("통신 오류가 발생했습니다.");
        } finally {
            setCheckingOut(false);
        }
    };

    if (loading) {
        return <div className="p-6">불러오는 중...</div>;
    }

    if (error) {
        return <div className="p-6 text-red-600">{error}</div>;
    }

    return (
        <div className="mx-auto max-w-3xl p-6">
            <h1 className="mb-6 text-2xl font-bold">장바구니</h1>

            {items.length === 0 ? (
                <p className="text-zinc-500">장바구니가 비어있습니다.</p>
            ) : (
                <ul className="divide-y divide-zinc-200">
                    {items.map((item) => {
                        const product = productMap.get(item.productItemId);
                        return (
                            <li key={item.cartItemId} className="flex items-center gap-4 py-4">
                                <input
                                    type="checkbox"
                                    checked={item.checked}
                                    onChange={() => toggleChecked(item.cartItemId)}
                                    className="h-4 w-4"
                                    aria-label="상품 선택"
                                />

                                <div className="flex-1">
                                    <p className="font-medium">{product?.name ?? `상품 #${item.productItemId}`}</p>
                                    <p className="text-sm text-zinc-500">
                                        {product ? product.price.toLocaleString() : "-"}원
                                    </p>
                                </div>

                                <div className="flex items-center rounded border border-zinc-300">
                                    <button
                                        onClick={() => changeQuantity(item.cartItemId, item.quantity - 1)}
                                        className="px-3 py-1 text-zinc-600 hover:bg-zinc-100"
                                        aria-label="수량 감소"
                                    >
                                        -
                                    </button>
                                    <input
                                        type="number"
                                        min={1}
                                        value={item.quantity}
                                        onChange={(e) => changeQuantity(item.cartItemId, Number(e.target.value))}
                                        className="w-14 border-x border-zinc-300 py-1 text-center"
                                    />
                                    <button
                                        onClick={() => changeQuantity(item.cartItemId, item.quantity + 1)}
                                        className="px-3 py-1 text-zinc-600 hover:bg-zinc-100"
                                        aria-label="수량 증가"
                                    >
                                        +
                                    </button>
                                </div>

                                <p className="w-24 text-right font-semibold">
                                    {product ? (product.price * item.quantity).toLocaleString() : "-"}원
                                </p>

                                <button
                                    onClick={() => handleDeleteItem(item.cartItemId, item.productItemId)}
                                    className="text-sm text-zinc-400 hover:text-red-600"
                                    aria-label="삭제"
                                >
                                    삭제
                                </button>
                            </li>
                        );
                    })}
                </ul>
            )}

            <div className="mt-6 flex items-center justify-between border-t border-zinc-200 pt-4">
                <span className="text-lg font-bold">선택 상품 합계</span>
                <span className="text-lg font-bold">{selectedTotal.toLocaleString()}원</span>
            </div>

            <div className="mt-6 flex items-center justify-between">
                <Link href="/" className="text-sm text-zinc-500 hover:text-zinc-900">
                    ← 메인으로
                </Link>
                <button
                    onClick={handleCheckout}
                    disabled={checkingOut}
                    className="rounded bg-zinc-900 px-5 py-2 text-sm font-medium text-white hover:bg-zinc-700 disabled:opacity-50"
                >
                    {checkingOut ? "처리 중..." : "결제하기 →"}
                </button>
            </div>
        </div>
    );
}
