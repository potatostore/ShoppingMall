// 잡지 내용. 사진을 바꾸면 image와 각 옷의 x, y(사진 기준 %)만 수정한다.
// productId, 이름, 가격은 실제 상품 데이터가 정해지면 맞춰야 하는 예시 값이다.

export type LookItem = {
    id: string;
    productId: number;
    name: string;
    description: string;
    price: number;
    x: number;
    y: number;
};

export type Look = {
    image: string;
    alt: string;
    title: string;
    intro: string;
    items: LookItem[];
};

export const COVER = {
    image: "/magazine/cover.svg",
    alt: "밝은색 롱 코트를 입은 모델",
    issue: "제 1호",
    season: "2026 겨울",
    headline: "코트, 셔츠, 그리고 재킷",
};

export const BACK_COVER = {
    company: "감자몰",
    purpose: "감자몰은 옷을 잡지처럼 파는 가게입니다. 사진을 보다가 마음에 드는 옷이 있으면 그 옷을 눌러보세요.",
    guide: "스크롤을 내리거나 화면 가장자리를 누르면 쪽이 넘어갑니다.",
    note: "사진의 색은 화면에 따라 실제 옷과 조금 다를 수 있습니다.",
    // 실제 사업자 정보로 교체해야 하는 임시 값
    contacts: [
        { label: "회사명", value: "감자몰" },
        { label: "대표전화", value: "02-000-0000" },
        { label: "이메일", value: "help@example.com" },
        { label: "주소", value: "서울특별시 (주소 입력 예정)" },
    ],
};

export const LOOKS: Look[] = [
    {
        image: "/magazine/look-1.svg",
        alt: "검정 롱 코트와 밝은 니트, 회색 슬랙스, 검정 부츠를 입은 모델",
        title: "코트 한 벌이면 되는 날",
        intro: "바람이 찬 날에는 코트 안에 밝은 니트를 입었다. 얼굴이 덜 어두워 보인다.",
        items: [
            { id: "knit", productId: 1, name: "라운드 울 니트", description: "울 혼방, 밝은 베이지", price: 69000, x: 50, y: 31 },
            { id: "coat", productId: 2, name: "싱글 롱 코트", description: "울 혼방, 차콜 블랙", price: 189000, x: 41, y: 47 },
            { id: "slacks", productId: 3, name: "스트레이트 슬랙스", description: "폴리 혼방, 웜 그레이", price: 59000, x: 54, y: 76 },
            { id: "boots", productId: 4, name: "레더 앵클 부츠", description: "소가죽, 블랙", price: 129000, x: 54, y: 87 },
        ],
    },
    {
        image: "/magazine/look-2.svg",
        alt: "흰 셔츠와 회색 플리츠 스커트, 검정 로퍼, 숄더백을 든 모델",
        title: "셔츠는 구겨져도 좋다",
        intro: "빳빳한 셔츠도 하루 입으면 구겨진다. 그때쯤 스커트 주름과 잘 어울린다.",
        items: [
            { id: "shirt", productId: 5, name: "코튼 오버 셔츠", description: "면 100%, 화이트", price: 49000, x: 48, y: 36 },
            { id: "bag", productId: 6, name: "미니 숄더백", description: "인조 가죽, 블랙", price: 79000, x: 69, y: 55 },
            { id: "skirt", productId: 7, name: "플리츠 롱 스커트", description: "폴리 100%, 그레이", price: 65000, x: 54, y: 65 },
            { id: "loafers", productId: 8, name: "페니 로퍼", description: "소가죽, 블랙", price: 99000, x: 54, y: 88 },
        ],
    },
    {
        image: "/magazine/look-3.svg",
        alt: "짙은 회색 재킷과 흰 티셔츠, 연회색 데님, 흰 스니커즈를 입은 모델",
        title: "토요일의 재킷",
        intro: "멀리 갈 일 없는 날 입는다. 주머니가 많아서 가방은 두고 나왔다.",
        items: [
            { id: "tee", productId: 9, name: "헤비 코튼 티셔츠", description: "면 100%, 오프 화이트", price: 29000, x: 50, y: 33 },
            { id: "jacket", productId: 10, name: "포켓 워크 재킷", description: "면 혼방, 다크 그레이", price: 119000, x: 55, y: 42 },
            { id: "denim", productId: 11, name: "스트레이트 데님", description: "면 100%, 라이트 그레이", price: 72000, x: 55, y: 70 },
            { id: "sneakers", productId: 12, name: "로우 스니커즈", description: "캔버스, 화이트", price: 59000, x: 55, y: 87 },
        ],
    },
];

export const LAST_PAGE = LOOKS.length;

export function clampPage(page: number): number {
    if (!Number.isInteger(page)) return 0;
    return Math.min(Math.max(page, 0), LAST_PAGE);
}
