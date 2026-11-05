// 고객센터 문의 게시판. 백엔드 API는 예정이며 계약은 Obsidian API 명세서 "Inquiry (문의)" 참고
// 목록은 누구나 보고(제목, 마스킹된 작성자, 날짜, 답변 상태), 본문과 답변은 작성자와 관리자만 본다

export const INQUIRY_PATH = "/inquiries";

export const INQUIRY_CATEGORIES = [
    { value: "ORDER", label: "주문·결제" },
    { value: "DELIVERY", label: "배송" },
    { value: "PRODUCT", label: "상품" },
    { value: "ACCOUNT", label: "회원 정보" },
    { value: "ETC", label: "기타" },
] as const;

export type InquiryCategory = (typeof INQUIRY_CATEGORIES)[number]["value"];

export const INQUIRY_PAGE_SIZE = 10;
export const INQUIRY_TITLE_MAX = 100;
export const INQUIRY_CONTENT_MAX = 2000;

export type InquirySummary = {
    inquiryId: number;
    category: InquiryCategory;
    title: string;
    // 서버가 마스킹한 이름 (예: 김*자)
    writerName: string;
    createdAt: string;
    answered: boolean;
};

export type InquiryPage = {
    content: InquirySummary[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    hasNext: boolean;
};

export type InquiryDetail = InquirySummary & {
    content: string;
    answer: { content: string; answeredAt: string } | null;
};

export function categoryLabel(category: string): string {
    return INQUIRY_CATEGORIES.find((item) => item.value === category)?.label ?? category;
}

// LocalDateTime 문자열 "2026-11-05T10:30:00" → "2026.11.05"
export function formatInquiryDate(value: string): string {
    const [date] = value.split("T");
    return date ? date.replaceAll("-", ".") : value;
}
