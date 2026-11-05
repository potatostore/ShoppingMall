'use client';

import Link from "next/link";
import { useEffect, useRef, useState } from "react";

export default function MagazineMenu({ isLoggedIn }: { isLoggedIn: boolean }) {
    const [open, setOpen] = useState(false);
    const rootRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        if (!open) return;

        const handlePointerDown = (e: PointerEvent) => {
            if (!rootRef.current?.contains(e.target as Node)) setOpen(false);
        };
        const handleKeyDown = (e: KeyboardEvent) => {
            if (e.key === "Escape") setOpen(false);
        };

        window.addEventListener("pointerdown", handlePointerDown);
        window.addEventListener("keydown", handleKeyDown);
        return () => {
            window.removeEventListener("pointerdown", handlePointerDown);
            window.removeEventListener("keydown", handleKeyDown);
        };
    }, [open]);

    const links = [
        { href: "/products", label: "전체 상품" },
        { href: "/cart", label: "장바구니" },
        { href: "/support", label: "고객센터" },
        isLoggedIn ? { href: "/profile", label: "내 정보" } : { href: "/login", label: "로그인" },
    ];

    return (
        <div ref={rootRef}>
            <button
                type="button"
                onClick={() => setOpen((prev) => !prev)}
                aria-expanded={open}
                aria-controls="magazine-menu"
                aria-label={open ? "목록 숨기기" : "목록 보기"}
                className="absolute right-3 top-2 z-30 block h-9 w-9 cursor-pointer mix-blend-difference book:right-8 book:top-4"
            >
                <span className={`absolute left-2 right-2 top-1/2 h-px bg-white transition-transform duration-200 ${open ? "rotate-45" : "-translate-y-[3px]"}`} />
                <span className={`absolute left-2 right-2 top-1/2 h-px bg-white transition-transform duration-200 ${open ? "-rotate-45" : "translate-y-[3px]"}`} />
            </button>

            {open && (
                <nav id="magazine-menu" className="absolute right-4 top-11 z-30 min-w-28 border border-ink bg-paper py-1 font-label text-[13px] text-ink book:right-9 book:top-[3.25rem]">
                    {links.map((link) => (
                        <Link key={link.href} href={link.href} className="block px-4 py-2 hover:underline hover:underline-offset-4">
                            {link.label}
                        </Link>
                    ))}
                </nav>
            )}
        </div>
    );
}
