'use client';

import Link from "next/link";
import { useLayoutEffect, useRef, useState } from "react";
import { MODEL_SPAN, type Look } from "./content";
import Photo from "./Photo";

type MemoBox = { top: number; left: number; width: number };
type Wire = { x1: number; y1: number; x2: number; y2: number };
type Layout = { memos: Record<string, MemoBox>; wires: Record<string, Wire> };

const EDGE = 14; // 쪽 가장자리와 메모 사이
const GAP = 10; // 메모와 모델 사이
const STACK = 8; // 같은 쪽 메모끼리의 간격
const TOP_RIGHT = 52; // 오른쪽 위 메뉴 버튼을 피하는 높이
const TOP_LEFT = 16;
const BOTTOM = 16;
const NAME_LINE = 18; // 메모 첫 줄 높이. 실금은 이 줄의 가운데에서 나간다
const MIN_WIDTH = 64;

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

// 모델 사진 한 쪽. 옷마다 제품명과 가격 메모를 모델 옆 배경에 두고, 가는 실금으로 옷의 점과 잇는다
export default function LookPage({ look }: { look: Look }) {
    const rootRef = useRef<HTMLDivElement>(null);
    const [layout, setLayout] = useState<Layout | null>(null);
    const [activeId, setActiveId] = useState<string | null>(null);

    const hoverProps = (id: string) => ({
        onMouseEnter: () => setActiveId(id),
        onMouseLeave: () => setActiveId(null),
        onFocus: () => setActiveId(id),
        onBlur: () => setActiveId(null),
    });

    useLayoutEffect(() => {
        const root = rootRef.current;
        if (!root) return;

        const measure = () => {
            const region = root.querySelector<HTMLElement>("[data-region]");
            if (!region) return;

            const pageWidth = root.offsetWidth;
            const pageHeight = root.offsetHeight;
            const regionX = offsetWithin(region, root).x;
            const span = look.span ?? MODEL_SPAN;
            const modelLeft = regionX + region.offsetWidth * span.left;
            const modelRight = regionX + region.offsetWidth * span.right;

            // 모델 옆 배경의 폭. 이 폭이 너무 좁으면 메모를 옷 옆이 아니라 옷 아래 배경에 둔다
            const room = {
                left: modelLeft - EDGE - GAP,
                right: pageWidth - modelRight - EDGE - GAP,
            };

            const memos: Record<string, MemoBox> = {};
            const wires: Record<string, Wire> = {};

            for (const side of ["left", "right"] as const) {
                const below = room[side] < MIN_WIDTH;
                const entries = look.items
                    .filter((item) => item.side === side)
                    .map((item) => {
                        const dot = root.querySelector<HTMLElement>(`[data-dot="${item.id}"]`);
                        const memo = root.querySelector<HTMLElement>(`[data-memo="${item.id}"]`);
                        if (!dot || !memo) return null;

                        // 높이는 메모 폭에 따라 달라지므로 폭을 먼저 맞춘 뒤 잰다
                        memo.style.width = "max-content";
                        // offsetWidth는 소수점을 버리므로 한 줄에 들어가도록 여유를 둔다
                        const width = below ? Math.min(memo.offsetWidth + 2, pageWidth - EDGE * 2) : room[side];
                        memo.style.width = `${width}px`;

                        const at = offsetWithin(dot, root);
                        const dotX = at.x + dot.offsetWidth / 2;
                        const dotY = at.y + dot.offsetHeight / 2;
                        const left = below
                            ? side === "left"
                                ? Math.max(EDGE, dotX + GAP - width)
                                : Math.min(pageWidth - EDGE - width, dotX - GAP)
                            : side === "left"
                              ? EDGE
                              : pageWidth - EDGE - width;

                        return {
                            id: item.id,
                            dotX,
                            dotY,
                            left,
                            width,
                            height: memo.offsetHeight,
                            wanted: below ? dotY + GAP * 2 : dotY - NAME_LINE / 2,
                        };
                    })
                    .filter((entry) => entry !== null)
                    .sort((a, b) => a.wanted - b.wanted);

                // 메모 첫 줄을 옷의 높이에 맞추고, 겹치면 아래로 민다. 바닥을 넘으면 위로 되민다
                const minTop = side === "right" ? TOP_RIGHT : TOP_LEFT;
                const tops: number[] = [];
                entries.forEach((entry, index) => {
                    const floor = index === 0 ? minTop : tops[index - 1] + entries[index - 1].height + STACK;
                    tops.push(Math.max(entry.wanted, floor));
                });
                for (let index = entries.length - 1; index >= 0; index--) {
                    const ceiling = index === entries.length - 1 ? pageHeight - BOTTOM : tops[index + 1] - STACK;
                    tops[index] = Math.max(minTop, Math.min(tops[index], ceiling - entries[index].height));
                }

                entries.forEach((entry, index) => {
                    const { left, width, dotX, dotY } = entry;
                    const top = tops[index];
                    memos[entry.id] = { top, left, width };

                    if (below) {
                        // 메모 위 가장자리에서 옷의 점으로 올라간다
                        const x1 = Math.min(Math.max(dotX, left + 4), left + width - 4);
                        wires[entry.id] = { x1, y1: top - 3, x2: dotX, y2: dotY };
                    } else {
                        // 왼쪽 메모는 오른쪽 정렬이라 글줄 끝이 모델 쪽에 붙는다
                        const x1 = side === "left" ? left + width + 4 : left - 4;
                        wires[entry.id] = { x1, y1: top + NAME_LINE / 2, x2: dotX, y2: dotY };
                    }
                });
            }

            setLayout({ memos, wires });
        };

        measure();
        const observer = new ResizeObserver(measure);
        observer.observe(root);
        document.fonts?.ready.then(measure);
        return () => observer.disconnect();
    }, [look]);

    return (
        <div ref={rootRef} className="relative h-full w-full overflow-hidden">
            <Photo src={look.image} alt={look.alt}>
                {look.items.map((item) => (
                    <Link
                        key={item.id}
                        href={`/products/${item.productId}`}
                        data-dot={item.id}
                        aria-label={`${item.name} 상세 보기`}
                        {...hoverProps(item.id)}
                        style={{ left: `${item.x}%`, top: `${item.y}%` }}
                        className="absolute z-10 -ml-3 -mt-3 flex h-6 w-6 items-center justify-center"
                    >
                        <span
                            className={`h-[5px] w-[5px] rounded-full bg-ink ring-1 ring-paper/70 transition-transform ${
                                activeId === item.id ? "scale-150" : ""
                            }`}
                        />
                    </Link>
                ))}
            </Photo>

            {layout && (
                <svg aria-hidden="true" className="pointer-events-none absolute inset-0 z-[5] h-full w-full">
                    {look.items.map((item) => {
                        const wire = layout.wires[item.id];
                        if (!wire) return null;
                        const active = activeId === item.id;
                        return (
                            <g key={item.id} fill="none" opacity={active ? 1 : 0.55}>
                                {/* 어두운 옷 위에서도 실금이 보이도록 아주 옅은 밝은 테두리를 깐다 */}
                                <line {...wire} stroke="#f6f3ec" strokeOpacity={0.35} strokeWidth={1.5} />
                                <line {...wire} stroke="#1d1b18" strokeWidth={active ? 0.9 : 0.6} />
                            </g>
                        );
                    })}
                </svg>
            )}

            {look.items.map((item) => {
                const box = layout?.memos[item.id];
                return (
                    <Link
                        key={item.id}
                        href={`/products/${item.productId}`}
                        data-memo={item.id}
                        {...hoverProps(item.id)}
                        style={box ? { top: box.top, left: box.left, width: box.width } : undefined}
                        className={`absolute z-10 block text-ink ${item.side === "left" ? "text-right" : "text-left"} ${
                            box ? "" : "invisible"
                        }`}
                    >
                        <span className="inline-block max-w-full">
                            <span
                                className={`block font-batang text-xs font-bold leading-[18px] sm:text-[13px] ${
                                    activeId === item.id ? "underline decoration-1 underline-offset-[3px]" : ""
                                }`}
                            >
                                {item.name}
                            </span>
                            <span className="block font-label text-[11px] leading-4 text-ink-soft">
                                {item.price.toLocaleString("ko-KR")}원
                            </span>
                        </span>
                    </Link>
                );
            })}
        </div>
    );
}
