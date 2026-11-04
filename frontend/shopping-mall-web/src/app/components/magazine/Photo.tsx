import Image from "next/image";
import type { ReactNode } from "react";

// 사진 비율(4:5)을 유지한 채 칸을 꽉 채운다. 옷 위치(%)는 이 안쪽 상자를 기준으로 하므로 화면 크기와 상관없이 유지된다.
// 실금 측정이 책장을 넘기는 중에도 맞도록 transform 없이 calc로 가운데에 둔다.
const COVER_BOX = {
    width: "max(100cqw, 80cqh)",
    left: "calc(50cqw - max(50cqw, 40cqh))",
    top: "calc(50cqh - max(62.5cqw, 50cqh))",
};

export default function Photo({
    src,
    alt,
    overlay,
    children,
}: {
    src: string;
    alt: string;
    overlay?: ReactNode;
    children?: ReactNode;
}) {
    return (
        <div data-photo className="relative h-full w-full overflow-hidden bg-zinc-200 [container-type:size]">
            <div className="absolute aspect-[4/5]" style={COVER_BOX}>
                <Image src={src} alt={alt} fill unoptimized priority />
                {children}
            </div>
            {overlay}
        </div>
    );
}
