import type { CSSProperties, ReactNode } from "react";
import BookSpread from "./BookSpread";
import Sheet from "./Sheet";

export const TURN_MS = 850;

export type Turn = { book: boolean; from: number; to: number; dir: 1 | -1 };

const stageStyle = { "--turn-ms": `${TURN_MS}ms` } as CSSProperties;

// 펼침면 전체를 그린 뒤 왼쪽 또는 오른쪽 절반만 보여준다
function HalfContent({ page, side }: { page: number; side: "left" | "right" }) {
    return (
        <div inert className={`absolute inset-y-0 w-[200%] ${side === "left" ? "left-0" : "right-0"}`}>
            <BookSpread page={page} />
        </div>
    );
}

// 책등 쪽이 어두워지는 그림자. side는 이 절반이 책등의 어느 쪽에 있는지를 뜻한다
function SpineShade({ side, strength }: { side: "left" | "right"; strength: number }) {
    const direction = side === "left" ? "to left" : "to right";
    return (
        <div
            className="turn-shade"
            style={{ background: `linear-gradient(${direction}, rgb(0 0 0 / ${strength}), transparent 55%)` }}
        />
    );
}

function StillHalf({ page, side, shade }: { page: number; side: "left" | "right"; shade: boolean }) {
    return (
        <div className={`absolute inset-y-0 w-1/2 overflow-hidden ${side === "left" ? "left-0" : "left-1/2"}`}>
            <HalfContent page={page} side={side} />
            {shade && <SpineShade side={side} strength={0.4} />}
        </div>
    );
}

export function BookTurn({ turn }: { turn: Turn }) {
    const { from, to, dir } = turn;
    const forward = dir === 1;

    return (
        <div aria-hidden="true" className="turn-stage absolute inset-0 overflow-hidden" style={stageStyle}>
            {/* 넘어가는 장 아래에 드러나는 면 */}
            <StillHalf page={forward ? from : to} side="left" shade={forward} />
            <StillHalf page={forward ? to : from} side="right" shade={!forward} />

            {/* 넘어가는 장. 앞면은 지금 쪽, 뒷면은 다음 쪽 */}
            <div className={`turn-leaf absolute inset-y-0 z-10 w-1/2 ${forward ? "turn-leaf-forward left-1/2" : "turn-leaf-backward left-0"}`}>
                <div className="turn-face">
                    <HalfContent page={from} side={forward ? "right" : "left"} />
                    <SpineShade side={forward ? "right" : "left"} strength={0.5} />
                </div>
                <div className="turn-face turn-face-back">
                    <HalfContent page={to} side={forward ? "left" : "right"} />
                    <SpineShade side={forward ? "left" : "right"} strength={0.5} />
                </div>
            </div>
        </div>
    );
}

function SheetLayer({ page, children }: { page: number; children?: ReactNode }) {
    return (
        <div inert className="absolute inset-0 overflow-hidden">
            <Sheet page={page} />
            {children}
        </div>
    );
}

// 세로 화면은 왼쪽 끝을 책등 삼아 한 장씩 넘긴다
export function SheetTurn({ turn }: { turn: Turn }) {
    const { from, to, dir } = turn;
    const forward = dir === 1;

    return (
        <div aria-hidden="true" className="turn-stage-sheet absolute inset-0 overflow-hidden" style={stageStyle}>
            <SheetLayer page={forward ? to : from}>
                <SpineShade side="right" strength={0.35} />
            </SheetLayer>
            <div className={`turn-leaf absolute inset-0 z-10 ${forward ? "turn-leaf-out" : "turn-leaf-in"}`}>
                <div className="turn-face">
                    <Sheet page={forward ? from : to} />
                    <SpineShade side="right" strength={0.5} />
                </div>
            </div>
        </div>
    );
}
