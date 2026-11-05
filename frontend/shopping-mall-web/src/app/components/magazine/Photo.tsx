import Image from "next/image";
import type { ReactNode } from "react";

// 사진은 8:5 비율로, 가운데 4:5 영역에 모델이 있고 양옆은 배경이다.
// 칸을 항상 꽉 채우고(110%는 위아래 여백을 줄이는 값), 옷 위치(%)는 가운데 4:5 영역을 기준으로 둔다.
// 실금 측정이 책장을 넘기는 중에도 맞도록 transform 없이 calc로 배치한다.
const WIDTH = "max(100cqw, 176cqh)";

export default function Photo({
    src,
    alt,
    centerX = 50,
    children,
}: {
    src: string;
    alt: string;
    // 모델을 둘 가로 위치(%). 사진이 칸 밖으로 벗어나지 않는 범위에서만 옮긴다
    centerX?: number;
    children?: ReactNode;
}) {
    const box = {
        width: WIDTH,
        left: `min(0px, max(calc(100cqw - ${WIDTH}), calc(${centerX}cqw - ${WIDTH} / 2)))`,
        top: `calc(50cqh - ${WIDTH} / 3.2)`,
    };

    return (
        <div data-photo className="relative h-full w-full overflow-hidden bg-zinc-200 [container-type:size]">
            <div className="absolute aspect-[8/5]" style={box}>
                <Image src={src} alt={alt} fill unoptimized priority />
                <div className="absolute inset-y-0 left-1/4 w-1/2">{children}</div>
            </div>
        </div>
    );
}
