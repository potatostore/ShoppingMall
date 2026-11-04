'use client';

import { useSearchParams } from "next/navigation";
import { useCallback, useEffect, useRef, useState, useSyncExternalStore } from "react";
import BookSpread from "./BookSpread";
import { BOOK_PAGES, COVER, LOOKS, SHEET_PAGES, clampPage, toBookPage, toSheetPage } from "./content";
import MagazineMenu from "./MagazineMenu";
import { BookTurn, SheetTurn, TURN_MS, type Turn } from "./PageTurn";
import Sheet from "./Sheet";

// globals.css의 book 변형과 값을 맞춘다
const BOOK_QUERY = "(min-width: 1024px) and (min-aspect-ratio: 5/4)";
const WHEEL_THRESHOLD = 20;
// 트랙패드 관성 스크롤 한 번에 여러 쪽이 넘어가지 않도록, 휠 입력이 이만큼 멈춘 뒤에만 다음 입력을 받는다
const WHEEL_IDLE_MS = 180;
const SWIPE_THRESHOLD = 40;

function subscribeBook(onChange: () => void) {
    const query = window.matchMedia(BOOK_QUERY);
    query.addEventListener("change", onChange);
    return () => query.removeEventListener("change", onChange);
}

// 넘길 수 있는 쪽임을 접힌 모서리로 보여준다. 가로 화면은 가장자리 전체를, 세로 화면은 메모를 가리지 않게 모서리만 누른다
function EdgeButton({ side, wide, onClick }: { side: "left" | "right"; wide: boolean; onClick: () => void }) {
    const isLeft = side === "left";
    const area = wide ? "inset-y-0 w-12" : "bottom-0 h-12 w-12";
    return (
        <button
            type="button"
            onClick={onClick}
            aria-label={isLeft ? "이전 쪽" : "다음 쪽"}
            className={`group absolute z-20 cursor-pointer ${area} ${isLeft ? "left-0" : "right-0"}`}
        >
            <span
                className={`absolute bottom-0 h-7 w-7 bg-fold transition-all duration-200 group-hover:h-12 group-hover:w-12 ${
                    isLeft ? "left-0 [clip-path:polygon(0_0,100%_100%,0_100%)]" : "right-0 [clip-path:polygon(100%_0,100%_100%,0_100%)]"
                }`}
            />
        </button>
    );
}

export default function Magazine({ isLoggedIn }: { isLoggedIn: boolean }) {
    const page = clampPage(Number(useSearchParams().get("page") ?? 0));
    // 서버에서는 화면 크기를 모르므로 null일 때 두 형태를 모두 그리고 CSS로 하나만 보인다
    const isBook = useSyncExternalStore(subscribeBook, () => window.matchMedia(BOOK_QUERY).matches, () => null);
    const [activeTurn, setActiveTurn] = useState<Turn | null>(null);

    const pageRef = useRef(page);
    const turnTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

    useEffect(() => {
        pageRef.current = page;
    }, [page]);

    const turnPage = useCallback((delta: 1 | -1) => {
        if (turnTimerRef.current) return;

        const book = window.matchMedia(BOOK_QUERY).matches;
        const pages = book ? BOOK_PAGES : SHEET_PAGES;
        const from = book ? toBookPage(pageRef.current) : toSheetPage(pageRef.current);
        const to = pages[pages.indexOf(from) + delta];
        if (to === undefined) return;

        pageRef.current = to;
        // 쪽 번호를 주소에 두어 상품 상세에서 뒤로 왔을 때 읽던 쪽으로 돌아온다
        window.history.replaceState(null, "", to === 0 ? "/" : `/?page=${to}`);

        if (window.matchMedia("(prefers-reduced-motion: reduce)").matches) return;

        setActiveTurn({ book, from, to, dir: delta });
        turnTimerRef.current = setTimeout(() => {
            turnTimerRef.current = null;
            setActiveTurn(null);
        }, TURN_MS);
    }, []);

    useEffect(() => {
        return () => {
            if (turnTimerRef.current) clearTimeout(turnTimerRef.current);
        };
    }, []);

    // 첫 넘김에서 다음 쪽 사진이 비어 보이지 않도록 미리 받아 둔다
    useEffect(() => {
        [COVER.image, ...LOOKS.map((look) => look.image)].forEach((src) => {
            new window.Image().src = src;
        });
    }, []);

    useEffect(() => {
        let ready = true;
        let idleTimer: ReturnType<typeof setTimeout> | undefined;
        let touchStart: { x: number; y: number } | null = null;

        const handleWheel = (e: WheelEvent) => {
            clearTimeout(idleTimer);
            idleTimer = setTimeout(() => {
                ready = true;
            }, WHEEL_IDLE_MS);

            const delta = Math.abs(e.deltaY) >= Math.abs(e.deltaX) ? e.deltaY : e.deltaX;
            if (!ready || Math.abs(delta) < WHEEL_THRESHOLD) return;

            ready = false;
            turnPage(delta > 0 ? 1 : -1);
        };

        const handleKeyDown = (e: KeyboardEvent) => {
            if (e.key === "ArrowRight") turnPage(1);
            if (e.key === "ArrowLeft") turnPage(-1);
        };

        const handleTouchStart = (e: TouchEvent) => {
            touchStart = e.touches.length === 1 ? { x: e.touches[0].clientX, y: e.touches[0].clientY } : null;
        };

        const handleTouchEnd = (e: TouchEvent) => {
            if (!touchStart) return;
            const dx = e.changedTouches[0].clientX - touchStart.x;
            const dy = e.changedTouches[0].clientY - touchStart.y;
            touchStart = null;

            const delta = Math.abs(dx) >= Math.abs(dy) ? dx : dy;
            if (Math.abs(delta) < SWIPE_THRESHOLD) return;
            turnPage(delta < 0 ? 1 : -1);
        };

        window.addEventListener("wheel", handleWheel, { passive: true });
        window.addEventListener("keydown", handleKeyDown);
        window.addEventListener("touchstart", handleTouchStart, { passive: true });
        window.addEventListener("touchend", handleTouchEnd, { passive: true });
        return () => {
            clearTimeout(idleTimer);
            window.removeEventListener("wheel", handleWheel);
            window.removeEventListener("keydown", handleKeyDown);
            window.removeEventListener("touchstart", handleTouchStart);
            window.removeEventListener("touchend", handleTouchEnd);
        };
    }, [turnPage]);

    const bookPage = toBookPage(page);
    const sheetPage = toSheetPage(page);
    const bookIndex = BOOK_PAGES.indexOf(bookPage);
    const sheetIndex = SHEET_PAGES.indexOf(sheetPage);

    return (
        <main className="magazine-root fixed inset-0 overflow-hidden break-keep bg-paper [touch-action:pinch-zoom]">
            <MagazineMenu isLoggedIn={isLoggedIn} />

            {isBook !== false && (
                <div className={`relative h-full ${isBook === null ? "hidden book:block" : ""}`}>
                    {activeTurn?.book ? <BookTurn turn={activeTurn} /> : <BookSpread page={bookPage} />}
                    {bookIndex > 0 && <EdgeButton side="left" wide onClick={() => turnPage(-1)} />}
                    {bookIndex < BOOK_PAGES.length - 1 && <EdgeButton side="right" wide onClick={() => turnPage(1)} />}
                </div>
            )}

            {isBook !== true && (
                <div className={`relative h-full ${isBook === null ? "book:hidden" : ""}`}>
                    {activeTurn && !activeTurn.book ? <SheetTurn turn={activeTurn} /> : <Sheet page={sheetPage} />}
                    {sheetIndex > 0 && <EdgeButton side="left" wide={false} onClick={() => turnPage(-1)} />}
                    {sheetIndex < SHEET_PAGES.length - 1 && <EdgeButton side="right" wide={false} onClick={() => turnPage(1)} />}
                </div>
            )}
        </main>
    );
}
