import { notFound } from "next/navigation";
import BackButton from "./BackButton";
import AddToCartForm from "./AddToCartForm";

const productApiUrl = "http://localhost:8080/api/v1/products";

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
        <div className="mx-auto max-w-3xl px-6 py-16">
            <BackButton />

            <div className="mt-6 flex flex-col gap-8 sm:flex-row">
                <div className="flex aspect-square w-full items-center justify-center rounded-lg bg-zinc-100 text-6xl font-bold text-zinc-300 sm:w-64">
                    {product.name.slice(0, 1)}
                </div>

                <div className="flex-1">
                    <h1 className="text-2xl font-bold text-zinc-900">{product.name}</h1>
                    <p className="mt-2 text-xl font-semibold text-zinc-900">
                        {product.price.toLocaleString("ko-KR")}원
                    </p>

                    {product.productDetailResponseDTOList.length > 0 && (
                        <ul className="mt-6 space-y-1 text-sm text-zinc-600">
                            {product.productDetailResponseDTOList.map((detail, index) => (
                                <li key={index}>{detail.detail}</li>
                            ))}
                        </ul>
                    )}

                    <AddToCartForm productId={product.productId} />
                </div>
            </div>
        </div>
    );
}
