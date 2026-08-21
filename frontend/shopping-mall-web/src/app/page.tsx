import Link from "next/link";

const productApiUrl = "http://localhost:8080/api/v1/products";

type Product = {
  productId: number;
  name: string;
  price: number;
};

async function getProducts(): Promise<Product[]> {
  const res = await fetch(productApiUrl, { cache: "no-store" });

  if (!res.ok) {
    return [];
  }

  const { data } = await res.json();
  return data;
}

export default async function Home() {
  const products = await getProducts();

  return (
    <div className="min-h-screen bg-white">
      {/* 네비게이션 */}
      <header className="sticky top-0 z-10 border-b border-zinc-200 bg-white/90 backdrop-blur">
        <nav className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
          <Link href="/" className="text-xl font-bold tracking-tight text-zinc-900">
            감자몰
          </Link>
          <div className="hidden gap-8 text-sm font-medium text-zinc-600 sm:flex">
            <Link href="/products" className="hover:text-zinc-900">전체 상품</Link>
            <Link href="/cart" className="hover:text-zinc-900">장바구니</Link>
          </div>
          <div className="flex gap-4 text-sm font-medium">
            <Link href="/login" className="text-zinc-600 hover:text-zinc-900">로그인</Link>
            <Link href="/cart" className="text-zinc-600 hover:text-zinc-900">장바구니</Link>
          </div>
        </nav>
      </header>

      {/* 배너 */}
      <section className="bg-zinc-900 px-6 py-24 text-center text-white">
        <p className="text-sm font-medium tracking-widest text-zinc-400 uppercase">Welcome</p>
        <h1 className="mt-3 text-4xl font-bold tracking-tight sm:text-5xl">감자몰에 오신 걸 환영해요</h1>
        <p className="mx-auto mt-4 max-w-md text-zinc-300">
          지금 등록된 상품들을 둘러보고 마음에 드는 걸 장바구니에 담아보세요.
        </p>
        <Link
          href="/products"
          className="mt-8 inline-block rounded-full bg-white px-8 py-3 text-sm font-semibold text-zinc-900 transition hover:bg-zinc-200"
        >
          지금 쇼핑하기
        </Link>
      </section>

      {/* 상품 그리드 */}
      <section className="mx-auto max-w-6xl px-6 py-16">
        <h2 className="text-2xl font-bold text-zinc-900">전체 상품</h2>

        {products.length === 0 ? (
          <p className="mt-6 text-zinc-500">아직 등록된 상품이 없어요.</p>
        ) : (
          <div className="mt-8 grid grid-cols-2 gap-6 sm:grid-cols-3 lg:grid-cols-4">
            {products.map((product) => (
              <Link
                key={product.productId}
                href={`/products/${product.productId}`}
                className="group"
              >
                <div className="flex aspect-square items-center justify-center rounded-lg bg-zinc-100 text-3xl font-bold text-zinc-300 transition group-hover:bg-zinc-200">
                  {product.name.slice(0, 1)}
                </div>
                <p className="mt-3 text-sm font-medium text-zinc-900">{product.name}</p>
                <p className="text-sm text-zinc-500">{product.price.toLocaleString("ko-KR")}원</p>
              </Link>
            ))}
          </div>
        )}
      </section>

      <footer className="border-t border-zinc-200 px-6 py-10 text-center text-sm text-zinc-400">
        © 2026 감자몰
      </footer>
    </div>
  );
}
