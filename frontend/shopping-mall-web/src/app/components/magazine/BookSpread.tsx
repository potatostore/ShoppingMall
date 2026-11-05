'use client';

import Link from "next/link";
import { useLayoutEffect, useRef, useState } from "react";
import { BACK_COVER, COVER, LOOKS, type Look } from "./content";
import Photo from "./Photo";

type Wire = { id: string; points: string };

// 책장을 넘기는 중에는 화면 좌표가 회전하므로, 회전의 영향을 받지 않는 offset 값으로 위치를 잰다
function offsetWithin(el: HTMLElement, root: HTMLElement) {
    let x = 0;
    let y = 0;
    let current: HTMLElement | null = el;
    while (current && current !== root) {
        x += current.offsetLeft;
        y += current.offsetTop;
        current = current.offsetParent as HTMLElement | null;
    }
    return { x, y };
}

function CoverSpread() {
    return (
        <>
            <section className="flex min-h-0 flex-col justify-between bg-paper py-12 pl-20 pr-16 text-ink">
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

            <section className="relative min-h-0">
                <Photo src={COVER.image} alt={COVER.alt} />
                <div className="pointer-events-none absolute inset-0 flex flex-col justify-between p-6 pr-16 text-paper">
                    <h1 className="font-display text-[clamp(5rem,14vw,13rem)] leading-none">감자몰</h1>
                    <div className="flex items-end justify-between gap-6">
                        <p className="font-batang text-2xl leading-snug xl:text-3xl">{COVER.headline}</p>
                        <p className="shrink-0 pb-1 font-label text-xs">{COVER.issue}, {COVER.season}</p>
                    </div>
                </div>
            </section>
        </>
    );
}

function LookSpread({ look, page }: { look: Look; page: number }) {
    const [wires, setWires] = useState<Wire[]>([]);
    const [activeId, setActiveId] = useState<string | null>(null);
    const rootRef = useRef<HTMLDivElement>(null);
    const leftFolio = page * 2;

    const hoverProps = (id: string) => ({
        onMouseEnter: () => setActiveId(id),
        onMouseLeave: () => setActiveId(null),
        onFocus: () => setActiveId(id),
        onBlur: () => setActiveId(null),
    });

    // 사진 속 옷의 점과 오른쪽 옷 이름의 실제 위치를 재서 실금을 잇는다
    useLayoutEffect(() => {
        const root = rootRef.current;
        if (!root) return;

        const measure = () => {
            const spineX = root.offsetWidth / 2;
            const next: Wire[] = [];
            for (const item of look.items) {
                const dot = root.querySelector<HTMLElement>(`[data-dot="${item.id}"]`);
                const anchor = root.querySelector<HTMLElement>(`[data-anchor="${item.id}"]`);
                if (!dot || !anchor) continue;

                const from = offsetWithin(dot, root);
                const to = offsetWithin(anchor, root);
                const fromX = from.x + dot.offsetWidth / 2;
                const fromY = from.y + dot.offsetHeight / 2;
                const toY = to.y + anchor.offsetHeight / 2;
                next.push({ id: item.id, points: `${fromX},${fromY} ${spineX},${toY} ${to.x - 8},${toY}` });
            }
            setWires(next);
        };

        measure();
        // 글꼴이 늦게 적용되어 글줄 높이가 바뀌어도 다시 잰다
        const observer = new ResizeObserver(measure);
        observer.observe(root);
        root.querySelectorAll("[data-anchor], [data-photo]").forEach((el) => observer.observe(el));
        document.fonts?.ready.then(measure);
        return () => observer.disconnect();
    }, [look]);

    return (
        <div ref={rootRef} className="absolute inset-0 grid grid-cols-2">
            <section className="relative min-h-0">
                <h2 className="sr-only">{look.title}</h2>
                <Photo src={look.image} alt={look.alt}>
                    {look.items.map((item) => (
                        <Link
                            key={item.id}
                            href={`/products/${item.productId}`}
                            data-dot={item.id}
                            aria-label={`${item.name} 상세 보기`}
                            {...hoverProps(item.id)}
                            style={{ left: `${item.x}%`, top: `${item.y}%` }}
                            className="absolute z-10 -ml-4 -mt-4 flex h-8 w-8 items-center justify-center"
                        >
                            <span className={`h-2 w-2 rounded-full bg-ink ring-1 ring-paper transition-transform ${activeId === item.id ? "scale-[1.8]" : ""}`} />
                        </Link>
                    ))}
                </Photo>
                <span className="absolute bottom-3 left-14 z-10 font-label text-[11px] text-ink">{leftFolio}</span>
            </section>

            <section className="relative flex min-h-0 min-w-0 flex-col bg-paper py-14 pl-16 pr-16 text-ink xl:pl-20">
                <ul className="flex flex-1 flex-col justify-around gap-2">
                    {look.items.map((item) => (
                        <li key={item.id}>
                            <Link href={`/products/${item.productId}`} {...hoverProps(item.id)} className="block">
                                <span data-anchor={item.id} className="block">
                                    <span className={`block font-batang text-xl font-bold leading-8 ${activeId === item.id ? "underline decoration-1 underline-offset-4" : ""}`}>
                                        {item.name}
                                    </span>
                                    <span className="block font-label text-[13px] leading-6 text-ink-soft">
                                        {item.description}, {item.price.toLocaleString("ko-KR")}원
                                    </span>
                                </span>
                            </Link>
                        </li>
                    ))}
                </ul>
                <p className="mt-8 max-w-[22rem] font-batang text-[15px] leading-[1.8]">{look.intro}</p>
                <span className="absolute bottom-3 right-14 font-label text-[11px]">{leftFolio + 1}</span>
            </section>

            {wires.length > 0 && (
                <svg aria-hidden="true" className="pointer-events-none absolute inset-0 z-[5] h-full w-full">
                    {wires.map((wire) => (
                        <g key={wire.id} fill="none" opacity={activeId && activeId !== wire.id ? 0.25 : 1}>
                            {/* 어두운 옷 위에서도 보이도록 밝은 테두리를 깐다 */}
                            <polyline points={wire.points} stroke="#f6f3ec" strokeOpacity={0.6} strokeWidth={2.25} />
                            <polyline points={wire.points} stroke="#1d1b18" strokeWidth={activeId === wire.id ? 1.5 : 0.75} />
                        </g>
                    ))}
                </svg>
            )}
        </div>
    );
}

export default function BookSpread({ page }: { page: number }) {
    return (
        <div className="absolute inset-0 bg-paper">
            {page === 0 ? (
                <div className="absolute inset-0 grid grid-cols-2">
                    <CoverSpread />
                </div>
            ) : (
                <LookSpread key={page} look={LOOKS[page - 1]} page={page} />
            )}
            <div aria-hidden="true" className="book-spine pointer-events-none absolute inset-y-0 left-1/2 z-[6] w-20 -translate-x-1/2" />
        </div>
    );
}
