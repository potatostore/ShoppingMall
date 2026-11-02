'use client';

import Link from "next/link";
import { useEffect, useMemo, useState } from "react";
import { CLIENT_API_URL } from "@/lib/api";

const productApiUrl = `${CLIENT_API_URL}/products`;
const PAGE_SIZE = 20;

type ProductDetail = {
    detail: string;
};

type Product = {
    productId: number;
    name: string;
    price: number;
    productDetailResponseDTOList: ProductDetail[];
};

type ProductPage = {
    content: Product[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    hasNext: boolean;
};

type SortOption = 'name' | 'priceAsc' | 'priceDesc';

// 화면의 정렬 값 → API sort 파라미터
const SORT_PARAM: Record<SortOption, string> = {
    name: 'NAME',
    priceAsc: 'PRICE_ASC',
    priceDesc: 'PRICE_DESC',
};

export default function ProductPage() {
    const [keyword, setKeyword] = useState<string>('');
    const [minPrice, setMinPrice] = useState<string>('');
    const [maxPrice, setMaxPrice] = useState<string>('');
    const [sort, setSort] = useState<SortOption>('name');
    const [page, setPage] = useState<number>(0);
    // 마지막으로 받은 결과와 그 요청 조건. 현재 조건과 다르면 로딩 중으로 표시
    const [result, setResult] = useState<{ query: string; data: ProductPage | null } | null>(null);

    // 검색·가격·정렬·페이지는 서버에서 처리 (상품이 많아져도 전체 목록을 받지 않음)
    const query = useMemo(() => {
        const params = new URLSearchParams({ sort: SORT_PARAM[sort], page: String(page), size: String(PAGE_SIZE) });
        if (keyword.trim() !== '') params.set('keyword', keyword.trim());
        if (minPrice !== '') params.set('minPrice', minPrice);
        if (maxPrice !== '') params.set('maxPrice', maxPrice);
        return params.toString();
    }, [keyword, minPrice, maxPrice, sort, page]);

    useEffect(() => {
        // 입력 중 매 글자마다 요청하지 않도록 300ms 디바운스
        const timer = setTimeout(async () => {
            try {
                const res = await fetch(`${productApiUrl}?${query}`);
                const { data } = await res.json();
                setResult({ query, data: res.ok ? data : null });
            } catch {
                setResult({ query, data: null });
            }
        }, 300);

        return () => clearTimeout(timer);
    }, [query]);

    const loading = result?.query !== query;
    const productPage = result?.data ?? null;
    const products = productPage?.content ?? [];

    // 조건이 바뀌면 첫 페이지부터
    const onFilterChange = <T,>(setter: (value: T) => void) => (value: T) => {
        setter(value);
        setPage(0);
    };

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
                        onChange={(e) => onFilterChange(setKeyword)(e.target.value)}
                        className="w-56 rounded border border-zinc-300 px-3 py-2 text-sm"
                    />
                </div>

                <div className="flex flex-col gap-1">
                    <label className="text-xs text-zinc-500">최소 가격</label>
                    <input
                        type="number"
                        placeholder="0"
                        value={minPrice}
                        onChange={(e) => onFilterChange(setMinPrice)(e.target.value)}
                        className="w-28 rounded border border-zinc-300 px-3 py-2 text-sm"
                    />
                </div>

                <div className="flex flex-col gap-1">
                    <label className="text-xs text-zinc-500">최대 가격</label>
                    <input
                        type="number"
                        placeholder="제한 없음"
                        value={maxPrice}
                        onChange={(e) => onFilterChange(setMaxPrice)(e.target.value)}
                        className="w-28 rounded border border-zinc-300 px-3 py-2 text-sm"
                    />
                </div>

                <div className="flex flex-col gap-1">
                    <label className="text-xs text-zinc-500">정렬</label>
                    <select
                        value={sort}
                        onChange={(e) => onFilterChange(setSort)(e.target.value as SortOption)}
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
            ) : products.length === 0 ? (
                <p className="text-zinc-500">조건에 맞는 상품이 없습니다.</p>
            ) : (
                <div className="grid grid-cols-2 gap-6 sm:grid-cols-3 lg:grid-cols-4">
                    {products.map((product) => (
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

            {productPage && productPage.totalPages > 1 && (
                <div className="mt-10 flex items-center justify-center gap-4 text-sm">
                    <button
                        type="button"
                        onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
                        disabled={page === 0 || loading}
                        className="rounded border border-zinc-300 px-3 py-1 disabled:opacity-40"
                    >
                        이전
                    </button>
                    <span className="text-zinc-500">
                        {page + 1} / {productPage.totalPages}
                    </span>
                    <button
                        type="button"
                        onClick={() => setPage((prev) => prev + 1)}
                        disabled={!productPage.hasNext || loading}
                        className="rounded border border-zinc-300 px-3 py-1 disabled:opacity-40"
                    >
                        다음
                    </button>
                </div>
            )}
        </div>
    );
}
