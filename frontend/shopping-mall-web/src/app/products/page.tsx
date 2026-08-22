'use client';

import Link from "next/link";
import { useEffect, useMemo, useState } from "react";

const productApiUrl = 'http://localhost:8080/api/v1/products';

type ProductDetail = {
    detail: string;
};

type Product = {
    productId: number;
    name: string;
    price: number;
    productDetailResponseDTOList: ProductDetail[];
};

type SortOption = 'name' | 'priceAsc' | 'priceDesc';

export default function ProductPage() {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [keyword, setKeyword] = useState<string>('');
    const [minPrice, setMinPrice] = useState<string>('');
    const [maxPrice, setMaxPrice] = useState<string>('');
    const [sort, setSort] = useState<SortOption>('name');

    useEffect(() => {
        const loadProducts = async () => {
            try {
                const res = await fetch(productApiUrl);
                const { data } = await res.json();
                setProducts(data ?? []);
            } finally {
                setLoading(false);
            }
        };

        loadProducts();
    }, []);

    const filteredProducts = useMemo(() => {
        const min = minPrice === '' ? null : Number(minPrice);
        const max = maxPrice === '' ? null : Number(maxPrice);

        const filtered = products.filter((product) => {
            const matchesKeyword = product.name.toLowerCase().includes(keyword.trim().toLowerCase());
            const matchesMin = min === null || product.price >= min;
            const matchesMax = max === null || product.price <= max;
            return matchesKeyword && matchesMin && matchesMax;
        });

        const sorted = [...filtered];
        if (sort === 'name') {
            sorted.sort((a, b) => a.name.localeCompare(b.name));
        } else if (sort === 'priceAsc') {
            sorted.sort((a, b) => a.price - b.price);
        } else {
            sorted.sort((a, b) => b.price - a.price);
        }

        return sorted;
    }, [products, keyword, minPrice, maxPrice, sort]);

    return (
        <div className="mx-auto max-w-6xl px-6 py-10">
            <h1 className="mb-6 text-2xl font-bold text-zinc-900">전체 상품</h1>

            <div className="mb-8 flex flex-wrap items-end gap-4">
                <div className="flex flex-col gap-1">
                    <label className="text-xs text-zinc-500">상품명 검색</label>
                    <input
                        type="text"
                        placeholder="상품명으로 검색"
                        value={keyword}
                        onChange={(e) => setKeyword(e.target.value)}
                        className="w-56 rounded border border-zinc-300 px-3 py-2 text-sm"
                    />
                </div>

                <div className="flex flex-col gap-1">
                    <label className="text-xs text-zinc-500">최소 가격</label>
                    <input
                        type="number"
                        placeholder="0"
                        value={minPrice}
                        onChange={(e) => setMinPrice(e.target.value)}
                        className="w-28 rounded border border-zinc-300 px-3 py-2 text-sm"
                    />
                </div>

                <div className="flex flex-col gap-1">
                    <label className="text-xs text-zinc-500">최대 가격</label>
                    <input
                        type="number"
                        placeholder="제한 없음"
                        value={maxPrice}
                        onChange={(e) => setMaxPrice(e.target.value)}
                        className="w-28 rounded border border-zinc-300 px-3 py-2 text-sm"
                    />
                </div>

                <div className="flex flex-col gap-1">
                    <label className="text-xs text-zinc-500">정렬</label>
                    <select
                        value={sort}
                        onChange={(e) => setSort(e.target.value as SortOption)}
                        className="rounded border border-zinc-300 px-3 py-2 text-sm"
                    >
                        <option value="name">이름순</option>
                        <option value="priceAsc">가격 낮은순</option>
                        <option value="priceDesc">가격 높은순</option>
                    </select>
                </div>
            </div>

            {loading ? (
                <p className="text-zinc-500">불러오는 중...</p>
            ) : filteredProducts.length === 0 ? (
                <p className="text-zinc-500">조건에 맞는 상품이 없습니다.</p>
            ) : (
                <div className="grid grid-cols-2 gap-6 sm:grid-cols-3 lg:grid-cols-4">
                    {filteredProducts.map((product) => (
                        <Link key={product.productId} href={`/products/${product.productId}`} className="group">
                            <div className="flex aspect-square items-center justify-center rounded-lg bg-zinc-100 text-3xl font-bold text-zinc-300 transition group-hover:bg-zinc-200">
                                {product.name.slice(0, 1)}
                            </div>
                            <p className="mt-3 text-sm font-medium text-zinc-900">{product.name}</p>
                            <p className="text-sm text-zinc-500">{product.price.toLocaleString('ko-KR')}원</p>
                        </Link>
                    ))}
                </div>
            )}
        </div>
    );
}
