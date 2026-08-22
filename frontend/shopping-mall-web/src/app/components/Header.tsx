import Link from "next/link";
import { cookies } from "next/headers";
import ProfileMenu from "./ProfileMenu";

export default async function Header() {
    const cookieStore = await cookies();
    const isLoggedIn = cookieStore.has("accessToken");

    return (
        <header className="sticky top-0 z-10 border-b border-zinc-200 bg-white/90 backdrop-blur">
            <nav className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
                <Link href="/" className="text-xl font-bold tracking-tight text-zinc-900">
                    감자몰
                </Link>

                <div className="hidden gap-8 text-sm font-medium text-zinc-600 sm:flex">
                    <Link href="/products" className="hover:text-zinc-900">전체 상품</Link>
                    <Link href="/cart" className="hover:text-zinc-900">장바구니</Link>
                </div>

                <div className="flex items-center text-sm font-medium">
                    {isLoggedIn ? (
                        <ProfileMenu />
                    ) : (
                        <Link href="/login" className="text-zinc-600 hover:text-zinc-900">
                            로그인
                        </Link>
                    )}
                </div>
            </nav>
        </header>
    );
}
