// 잡지 내용. 사진은 8:5 비율에 모델을 가운데 둔다. 양옆 빈 배경에 세로 화면의 옷 정보가 놓인다.
// 사진을 바꾸면 image와 각 옷의 x, y(사진 가운데 4:5 영역 기준 %)만 수정한다.
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
    guideSheet: "스크롤을 내리거나 화면을 밀거나 아래 모서리를 누르면 쪽이 넘어갑니다.",
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
    {
        image: "/magazine/look-4.svg",
        alt: "베이지 트렌치코트와 줄무늬 티셔츠, 검정 와이드 팬츠, 갈색 더비 슈즈를 입은 모델",
        title: "비 예보가 있는 아침",
        intro: "우산은 챙겼고 코트 깃은 세웠다. 바지 밑단은 조금 젖어도 괜찮다.",
        items: [
            { id: "stripe", productId: 13, name: "스트라이프 티셔츠", description: "면 100%, 화이트 블랙", price: 35000, x: 50, y: 33 },
            { id: "trench", productId: 14, name: "더블 트렌치코트", description: "면 혼방, 베이지", price: 219000, x: 41, y: 47 },
            { id: "wide", productId: 15, name: "와이드 치노 팬츠", description: "면 100%, 차콜", price: 79000, x: 56, y: 76 },
            { id: "derby", productId: 16, name: "더비 슈즈", description: "소가죽, 다크 브라운", price: 139000, x: 55, y: 88 },
        ],
    },
];

// 주소의 page 값: 0은 표지, 1부터는 LOOKS 순서, 마지막은 뒤표지. 화면 방향이 바뀌어도 같은 사진이 이어진다
export const BACK_PAGE = LOOKS.length + 1;

// 가로 화면은 펼침면 한 쌍씩 넘긴다. 뒤표지는 표지 펼침면의 왼쪽에 있다
export const BOOK_PAGES = [0, ...LOOKS.map((_, index) => index + 1)];

// 세로 화면은 사진 두 장을 한 장으로 묶어 넘긴다. LOOKS를 짝수로 두어야 마지막 장이 비지 않는다
export const SHEET_PAGES = [
    0,
    ...LOOKS.map((_, index) => index + 1).filter((page) => page % 2 === 1),
    BACK_PAGE,
];

export function clampPage(page: number): number {
    if (!Number.isInteger(page)) return 0;
    return Math.min(Math.max(page, 0), BACK_PAGE);
}

export function toBookPage(page: number): number {
    return page === BACK_PAGE ? 0 : page;
}

export function toSheetPage(page: number): number {
    if (page === 0 || page === BACK_PAGE) return page;
    return page % 2 === 1 ? page : page - 1;
}
