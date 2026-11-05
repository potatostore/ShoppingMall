import { LOOKS } from "./content";
import LookPage from "./LookPage";

// 세로 화면의 한 쪽. 모델 사진 한 장만 보여준다
export default function Sheet({ page }: { page: number }) {
    return (
        <div className="absolute inset-0 bg-paper">
            <LookPage look={LOOKS[page - 1]} />
        </div>
    );
}
