'use client';

import Image from "next/image";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { useCallback, useEffect, useLayoutEffect, useRef, useState, type ReactNode } from "react";
import { BACK_COVER, COVER, LAST_PAGE, LOOKS, clampPage, type Look } from "./content";

const DESKTOP_QUERY = "(min-width: 768px)";
const WHEEL_THRESHOLD = 20;
// 트랙패드 관성 스크롤 한 번에 여러 쪽이 넘어가지 않도록, 휠 입력이 이만큼 멈춘 뒤에만 다음 입력을 받는다
const WHEEL_IDLE_MS = 180;

type Wire = { id: string; points: string };

// 사진을 페이지에 꽉 채우되(cover), 안쪽 박스가 사진과 같은 비율이라 옷 위치(%)가 화면 크기와 상관없이 유지된다
function Photo({ src, alt, children }: { src: string; alt: string; children?: ReactNode }) {
    return (
        <div className="relative h-full w-full overflow-hidden bg-zinc-200 [container-type:size]">
            <div className="absolute left-1/2 top-1/2 aspect-[4/5] w-[max(100cqw,80cqh)] -translate-x-1/2 -translate-y-1/2">
                <Image src={src} alt={alt} fill unoptimized priority />
                {children}
            </div>
        </div>
    );
}

function CoverSpread() {
    return (
        <>
            <section className="order-2 flex min-h-dvh shrink-0 flex-col justify-between bg-paper p-8 text-ink md:order-1 md:min-h-0 md:py-12 md:pl-20 md:pr-16">
                <p className="max-w-[23rem] font-batang text-xl leading-[1.75]">{BACK_COVER.purpose}</p>

                <div className="mt-16 font-label text-xs leading-6 text-ink-soft">
                    <p className="mb-5 max-w-[20rem]">
                        {BACK_COVER.guide} {BACK_COVER.note}
                    </p>
                    {BACK_COVER.contacts.map((contact) => (
                        <p key={contact.label}>
                            <span className="inline-block w-16">{contact.label}</span>
                            <span className="text-ink">{contact.value}</span>
                        </p>
                    ))}
                </div>
            </section>

            <section className="relative order-1 h-dvh shrink-0 md:order-2 md:h-auto">
                <Photo src={COVER.image} alt={COVER.alt} />
                <div className="pointer-events-none absolute inset-0 flex flex-col justify-between p-6 text-paper md:pr-16">
                    <h1 className="font-display text-[clamp(5rem,14vw,13rem)] leading-none">감자몰</h1>
                    <div className="flex items-end justify-between gap-6">
                        <p className="font-batang text-2xl leading-snug lg:text-3xl">{COVER.headline}</p>
                        <p className="shrink-0 pb-1 font-label text-xs">{COVER.issue}, {COVER.season}</p>
                    </div>
                </div>
            </section>
        </>
    );
}

function LookSpread({
    look,
    page,
    activeId,
    onActive,
}: {
    look: Look;
    page: number;
    activeId: string | null;
    onActive: (id: string | null) => void;
}) {
    const leftFolio = page * 2;

    return (
        <>
            <section className="relative h-[75dvh] shrink-0 md:h-auto">
                <Photo src={look.image} alt={look.alt}>
                    {look.items.map((item, index) => (
                        <Link
                            key={item.id}
                            href={`/products/${item.productId}`}
                            data-dot={item.id}
                            aria-label={`${item.name} 상세 보기`}
                            onMouseEnter={() => onActive(item.id)}
                            onMouseLeave={() => onActive(null)}
                            onFocus={() => onActive(item.id)}
                            onBlur={() => onActive(null)}
                            style={{ left: `${item.x}%`, top: `${item.y}%` }}
                            className="absolute z-10 flex h-8 w-8 -translate-x-1/2 -translate-y-1/2 items-center justify-center"
                        >
                            <span
                                className={`flex h-5 w-5 items-center justify-center rounded-full bg-ink font-label text-[10px] text-paper ring-1 ring-paper transition-transform md:h-2 md:w-2 ${
                                    activeId === item.id ? "md:scale-[1.8]" : ""
                                }`}
                            >
                                <span className="md:hidden">{index + 1}</span>
                            </span>
                        </Link>
                    ))}
                </Photo>
                <span className="absolute bottom-3 left-14 z-10 hidden font-label text-[11px] text-ink md:block">{leftFolio}</span>
            </section>

            <section className="relative flex shrink-0 flex-col gap-8 bg-paper px-8 py-10 text-ink md:flex-row-reverse md:gap-10 md:py-14 md:pl-16 md:pr-16">
                <h2 className="font-display text-[clamp(2.25rem,7vh,4rem)] leading-tight md:[writing-mode:vertical-rl]">
                    {look.title}
                </h2>

                <div className="flex min-w-0 flex-1 flex-col md:justify-between">
                    <ul className="flex flex-col gap-6 md:flex-1 md:justify-around md:gap-2">
                        {look.items.map((item, index) => (
                            <li key={item.id}>
                                <Link
                                    href={`/products/${item.productId}`}
                                    onMouseEnter={() => onActive(item.id)}
                                    onMouseLeave={() => onActive(null)}
                                    onFocus={() => onActive(item.id)}
                                    onBlur={() => onActive(null)}
                                    className="flex gap-3"
                                >
                                    <span className="font-label text-xs leading-7 text-ink-soft md:hidden">{index + 1}</span>
                                    <span data-anchor={item.id} className="block">
                                        <span className={`block font-batang text-lg font-bold leading-7 ${activeId === item.id ? "underline decoration-1 underline-offset-4" : ""}`}>
                                            {item.name}
                                        </span>
                                        <span className="block font-label text-xs leading-5 text-ink-soft">
                                            {item.description}, {item.price.toLocaleString("ko-KR")}원
                                        </span>
                                    </span>
                                </Link>
                            </li>
                        ))}
                    </ul>
                    <p className="mt-8 max-w-[20rem] font-batang text-sm leading-[1.8]">{look.intro}</p>
                </div>
                <span className="absolute bottom-3 right-14 hidden font-label text-[11px] md:block">{leftFolio + 1}</span>
            </section>
        </>
    );
}

// 잡지 모서리를 접어 넘길 수 있는 쪽임을 보여준다
function EdgeButton({ side, onClick }: { side: "left" | "right"; onClick: () => void }) {
    const isLeft = side === "left";
    return (
        <button
            type="button"
            onClick={onClick}
            aria-label={isLeft ? "이전 쪽" : "다음 쪽"}
            className={`group absolute inset-y-0 z-20 w-8 cursor-pointer md:w-12 ${isLeft ? "left-0" : "right-0"}`}
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
    const [wires, setWires] = useState<Wire[]>([]);
    const [activeId, setActiveId] = useState<string | null>(null);
    const spreadRef = useRef<HTMLDivElement>(null);
    const look = page === 0 ? null : LOOKS[page - 1];

    const pageRef = useRef(page);

    useEffect(() => {
        pageRef.current = page;
    }, [page]);

    const turn = useCallback((delta: number) => {
        const next = clampPage(pageRef.current + delta);
        if (next === pageRef.current) return;

        pageRef.current = next;
        setActiveId(null);
        // 쪽 번호를 주소에 두어 상품 상세에서 뒤로 왔을 때 읽던 쪽으로 돌아온다
        window.history.replaceState(null, "", next === 0 ? "/" : `/?page=${next}`);
    }, []);

    useEffect(() => {
        let ready = true;
        let idleTimer: ReturnType<typeof setTimeout> | undefined;

        const handleWheel = (e: WheelEvent) => {
            if (!window.matchMedia(DESKTOP_QUERY).matches) return;

            clearTimeout(idleTimer);
            idleTimer = setTimeout(() => {
                ready = true;
            }, WHEEL_IDLE_MS);

            const delta = Math.abs(e.deltaY) >= Math.abs(e.deltaX) ? e.deltaY : e.deltaX;
            if (!ready || Math.abs(delta) < WHEEL_THRESHOLD) return;

            ready = false;
            turn(delta > 0 ? 1 : -1);
        };

        const handleKeyDown = (e: KeyboardEvent) => {
            if (e.key === "ArrowRight") turn(1);
            if (e.key === "ArrowLeft") turn(-1);
        };

        window.addEventListener("wheel", handleWheel, { passive: true });
        window.addEventListener("keydown", handleKeyDown);
        return () => {
            clearTimeout(idleTimer);
            window.removeEventListener("wheel", handleWheel);
            window.removeEventListener("keydown", handleKeyDown);
        };
    }, [turn]);

    // 사진 속 옷 번호와 오른쪽 목록 번호의 실제 위치를 재서 실금을 잇는다
    useLayoutEffect(() => {
        const spread = spreadRef.current;
        if (!spread || !look) {
            setWires([]);
            return;
        }

        const measure = () => {
            if (!window.matchMedia(DESKTOP_QUERY).matches) {
                setWires([]);
                return;
            }

            const origin = spread.getBoundingClientRect();
            const spineX = origin.width / 2;
            const center = (el: Element) => {
                const rect = el.getBoundingClientRect();
                return { left: rect.left - origin.left, x: rect.left - origin.left + rect.width / 2, y: rect.top - origin.top + rect.height / 2 };
            };

            const next: Wire[] = [];
            for (const item of look.items) {
                const dot = spread.querySelector(`[data-dot="${item.id}"]`);
                const anchor = spread.querySelector(`[data-anchor="${item.id}"]`);
                if (!dot || !anchor) continue;

                const from = center(dot);
                const to = center(anchor);
                next.push({ id: item.id, points: `${from.x},${from.y} ${spineX},${to.y} ${to.left - 8},${to.y}` });
            }
            setWires(next);
        };

        measure();
        const observer = new ResizeObserver(measure);
        observer.observe(spread);
        document.fonts?.ready.then(measure);
        return () => observer.disconnect();
    }, [look]);

    return (
        <main className="relative h-dvh overflow-hidden break-keep bg-paper">
            <nav className="absolute right-10 top-4 z-30 flex gap-4 font-label text-xs text-white mix-blend-difference md:right-16">
                <Link href="/products">전체 상품</Link>
                <Link href="/cart">장바구니</Link>
                <Link href={isLoggedIn ? "/profile" : "/login"}>{isLoggedIn ? "내 정보" : "로그인"}</Link>
            </nav>

            <div key={page} ref={spreadRef} className="magazine-page relative flex h-full flex-col overflow-y-auto md:grid md:grid-cols-2 md:overflow-hidden">
                {look ? (
                    <LookSpread look={look} page={page} activeId={activeId} onActive={setActiveId} />
                ) : (
                    <CoverSpread />
                )}

                {wires.length > 0 && (
                    <svg aria-hidden="true" className="pointer-events-none absolute inset-0 z-[5] hidden h-full w-full mix-blend-difference md:block">
                        {wires.map((wire) => (
                            <polyline
                                key={wire.id}
                                points={wire.points}
                                fill="none"
                                stroke="#ffffff"
                                strokeWidth={activeId === wire.id ? 1.5 : 0.75}
                                opacity={activeId && activeId !== wire.id ? 0.25 : 1}
                            />
                        ))}
                    </svg>
                )}
            </div>

            {page > 0 && <EdgeButton side="left" onClick={() => turn(-1)} />}
            {page < LAST_PAGE && <EdgeButton side="right" onClick={() => turn(1)} />}
        </main>
    );
}
