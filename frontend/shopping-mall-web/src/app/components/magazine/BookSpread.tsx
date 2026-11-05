import { LOOKS } from "./content";
import LookPage from "./LookPage";

// 펼침면. page는 왼쪽 쪽 번호(홀수)이고, 왼쪽과 오른쪽에 모델 사진이 한 장씩 놓인다
export default function BookSpread({ page }: { page: number }) {
    const looks = LOOKS.slice(page - 1, page + 1);

    return (
        <div className="absolute inset-0 grid grid-cols-2 bg-paper">
            {looks.map((look) => (
                <LookPage key={look.image} look={look} />
            ))}
            <div aria-hidden="true" className="book-spine pointer-events-none absolute inset-y-0 left-1/2 z-[6] w-20 -translate-x-1/2" />
        </div>
    );
}
