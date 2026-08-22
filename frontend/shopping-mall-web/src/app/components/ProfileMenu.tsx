'use client';

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useRef, useState } from "react";

const logoutApiUrl = "http://localhost:8080/api/v1/users/logout";

export default function ProfileMenu() {
    const router = useRouter();
    const [open, setOpen] = useState(false);
    const [loggingOut, setLoggingOut] = useState(false);
    const menuRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (e: MouseEvent) => {
            if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
                setOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const handleLogout = async () => {
        setLoggingOut(true);
        try {
            await fetch(logoutApiUrl, {
                method: "DELETE",
                credentials: "include",
            });
        } catch {
            // 통신 실패해도 일단 로그인 페이지로 보냄
        } finally {
            setOpen(false);
            setLoggingOut(false);
            router.push("/login");
            router.refresh();
        }
    };

    return (
        <div ref={menuRef} className="relative">
            <button
                onClick={() => setOpen((prev) => !prev)}
                aria-label="프로필 메뉴"
                className="flex h-9 w-9 items-center justify-center rounded-full bg-zinc-900 text-white"
            >
                👤
            </button>

            {open && (
                <div className="absolute right-0 mt-2 w-40 overflow-hidden rounded border border-zinc-200 bg-white shadow-lg">
                    <Link
                        href="/profile"
                        onClick={() => setOpen(false)}
                        className="block px-4 py-2 text-sm text-zinc-700 hover:bg-zinc-50"
                    >
                        프로필 변경
                    </Link>
                    <button
                        onClick={handleLogout}
                        disabled={loggingOut}
                        className="block w-full px-4 py-2 text-left text-sm text-zinc-700 hover:bg-zinc-50 disabled:opacity-50"
                    >
                        {loggingOut ? "로그아웃 중..." : "로그아웃"}
                    </button>
                </div>
            )}
        </div>
    );
}
