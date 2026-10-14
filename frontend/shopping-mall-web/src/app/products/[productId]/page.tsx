import Link from "next/link";
import { notFound } from "next/navigation";
import AddToCartForm from "./AddToCartForm";
import { SERVER_API_URL } from "@/lib/api";

const productApiUrl = `${SERVER_API_URL}/products`;

type ProductDetail = {
    detail: string;
};

type Product = {
    productId: number;
    name: string;
    price: number;
    productDetailResponseDTOList: ProductDetail[];
};

async function getProduct(productId: string): Promise<Product | null> {
    const res = await fetch(`${productApiUrl}/${productId}`, { cache: "no-store" });

    if (!res.ok) {
        return null;
    }

    const { data } = await res.json();
    return data;
}

export default async function ProductDetailPage({
    params,
}: {
    params: Promise<{ productId: string }>;
}) {
    const { productId } = await params;
    const product = await getProduct(productId);

    if (!product) {
        notFound();
    }

    return (
        <div className="mx-auto max-w-6xl px-6 py-10">
            <nav aria-label="breadcrumb" className="flex items-center gap-2 text-xs text-zinc-500">
                <Link href="/" className="hover:text-zinc-900">홈</Link>
                <span>/</span>
                <Link href="/products" className="hover:text-zinc-900">전체 상품</Link>
                <span>/</span>
                <span className="truncate text-zinc-900">{product.name}</span>
            </nav>

            <div className="mt-6 grid gap-10 md:grid-cols-2 md:gap-16">
                <div className="flex aspect-square w-full items-center justify-center bg-zinc-100 text-8xl font-bold text-zinc-300">
                    {product.name.slice(0, 1)}
                </div>

                <div className="md:sticky md:top-24 md:self-start">
                    <h1 className="text-2xl font-bold tracking-tight text-zinc-900 sm:text-3xl">{product.name}</h1>
                    <p className="mt-4 text-2xl font-bold text-zinc-900">
                        {product.price.toLocaleString("ko-KR")}
                        <span className="ml-0.5 text-lg font-medium">원</span>
                    </p>

                    {product.productDetailResponseDTOList.length > 0 && (
                        <div className="mt-8 border-t border-zinc-900 pt-6">
                            <h2 className="text-sm font-semibold text-zinc-900">상품 정보</h2>
                            <ul className="mt-3 space-y-2 text-sm text-zinc-600">
                                {product.productDetailResponseDTOList.map((detail, index) => (
                                    <li key={index} className="flex gap-2">
                                        <span className="text-zinc-300">·</span>
                                        <span>{detail.detail}</span>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}

                    <AddToCartForm productId={product.productId} price={product.price} />
                </div>
            </div>
        </div>
    );
}
