// 잡지 내용. 사진은 8:5 비율이고 가운데 4:5 영역에 모델이 있다. 양옆 배경에 메모가 놓인다.
// 사진을 바꾸면 image와 각 옷의 x, y(가운데 4:5 영역 기준 %), side(메모를 둘 쪽)만 수정한다.
// productId, 이름, 가격은 실제 상품 데이터가 정해지면 맞춰야 하는 예시 값이다.

export type LookItem = {
    id: string;
    productId: number;
    name: string;
    price: number;
    // 실금이 닿는 옷 위의 점. 메모 쪽 가장자리에 두면 실금이 옷을 덜 가린다
    x: number;
    y: number;
    side: "left" | "right";
};

// 가운데 4:5 영역 안에서 모델(들고 있는 가방 등 포함)이 차지하는 가로 범위(비율). 메모는 이 바깥에 둔다
export type ModelSpan = { left: number; right: number };

export type Look = {
    image: string;
    alt: string;
    span?: ModelSpan;
    items: LookItem[];
};

export const MODEL_SPAN: ModelSpan = { left: 0.32, right: 0.68 };

// 고객센터 화면에서 쓰는 회사 정보. 실제 사업자 정보로 교체해야 하는 임시 값
export const COMPANY = {
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
        items: [
            { id: "knit", productId: 1, name: "라운드 울 니트", price: 69000, x: 51, y: 30, side: "right" },
            { id: "coat", productId: 2, name: "싱글 롱 코트", price: 189000, x: 39, y: 45, side: "left" },
            { id: "slacks", productId: 3, name: "스트레이트 슬랙스", price: 59000, x: 54, y: 72, side: "right" },
            { id: "boots", productId: 4, name: "레더 앵클 부츠", price: 129000, x: 45, y: 89, side: "left" },
        ],
    },
    {
        image: "/magazine/look-2.svg",
        alt: "흰 셔츠와 회색 플리츠 스커트, 검정 로퍼, 숄더백을 든 모델",
        span: { left: 0.32, right: 0.74 },
        items: [
            { id: "shirt", productId: 5, name: "코튼 오버 셔츠", price: 49000, x: 42, y: 36, side: "left" },
            { id: "bag", productId: 6, name: "미니 숄더백", price: 79000, x: 68, y: 58, side: "right" },
            { id: "skirt", productId: 7, name: "플리츠 롱 스커트", price: 65000, x: 42, y: 68, side: "left" },
            { id: "loafers", productId: 8, name: "페니 로퍼", price: 99000, x: 56, y: 89, side: "right" },
        ],
    },
    {
        image: "/magazine/look-3.svg",
        alt: "짙은 회색 재킷과 흰 티셔츠, 연회색 데님, 흰 스니커즈를 입은 모델",
        items: [
            { id: "tee", productId: 9, name: "헤비 코튼 티셔츠", price: 29000, x: 50, y: 31, side: "right" },
            { id: "jacket", productId: 10, name: "포켓 워크 재킷", price: 119000, x: 40, y: 42, side: "left" },
            { id: "denim", productId: 11, name: "스트레이트 데님", price: 72000, x: 55, y: 70, side: "right" },
            { id: "sneakers", productId: 12, name: "로우 스니커즈", price: 59000, x: 43, y: 89, side: "left" },
        ],
    },
    {
        image: "/magazine/look-4.svg",
        alt: "베이지 트렌치코트와 줄무늬 티셔츠, 검정 와이드 팬츠, 갈색 더비 슈즈를 입은 모델",
        items: [
            { id: "stripe", productId: 13, name: "스트라이프 티셔츠", price: 35000, x: 51, y: 33, side: "right" },
            { id: "trench", productId: 14, name: "더블 트렌치코트", price: 219000, x: 39, y: 45, side: "left" },
            { id: "wide", productId: 15, name: "와이드 치노 팬츠", price: 79000, x: 56, y: 74, side: "right" },
            { id: "derby", productId: 16, name: "더비 슈즈", price: 139000, x: 45, y: 89, side: "left" },
        ],
    },
];

// 주소의 page 값은 1부터 LOOKS 순서. 화면 방향이 바뀌어도 같은 사진이 이어진다
export const LAST_PAGE = LOOKS.length;

// 가로 화면은 두 쪽씩(왼쪽이 홀수 쪽) 넘긴다. LOOKS를 짝수로 두어야 마지막 펼침면이 비지 않는다
export const BOOK_PAGES = LOOKS.map((_, index) => index + 1).filter((page) => page % 2 === 1);

// 세로 화면은 한 쪽씩 넘긴다
export const SHEET_PAGES = LOOKS.map((_, index) => index + 1);

export function clampPage(page: number): number {
    if (!Number.isInteger(page)) return 1;
    return Math.min(Math.max(page, 1), LAST_PAGE);
}

export function toBookPage(page: number): number {
    return page % 2 === 1 ? page : page - 1;
}
