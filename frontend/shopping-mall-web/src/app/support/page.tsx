import Link from "next/link";
import { COMPANY } from "../components/magazine/content";
import { SERVER_API_URL } from "@/lib/api";
import {
    INQUIRY_PAGE_SIZE,
    INQUIRY_PATH,
    categoryLabel,
    formatInquiryDate,
    type InquiryPage,
} from "@/lib/inquiry";

const HELP_LINKS = [
    { href: "/orders", label: "주문 내역" },
    { href: "/find-id", label: "아이디 찾기" },
    { href: "/find-password", label: "비밀번호 찾기" },
];

// 목록은 공개 API라 서버에서 바로 조회한다. 실패하면 null을 돌려 화면에 안내만 띄운다
async function fetchInquiries(page: number): Promise<InquiryPage | null> {
    const params = new URLSearchParams({ page: String(page), size: String(INQUIRY_PAGE_SIZE) });
    try {
        const res = await fetch(`${SERVER_API_URL}${INQUIRY_PATH}?${params}`, { cache: "no-store" });
        if (!res.ok) {
            return null;
        }
        const { data } = await res.json();
        return data as InquiryPage;
    } catch {
        return null;
    }
}

function parsePage(value: string | undefined): number {
    const page = Number(value ?? 0);
    return Number.isInteger(page) && page >= 0 ? page : 0;
}

export default async function SupportPage({ searchParams }: { searchParams: Promise<{ page?: string }> }) {
    const page = parsePage((await searchParams).page);
    const inquiryPage = await fetchInquiries(page);
    const contacts = COMPANY.contacts.filter((contact) => contact.label !== "회사명");

    return (
        <div className="mx-auto w-full max-w-3xl px-6 py-20">
            <div className="flex flex-wrap items-end justify-between gap-4">
                <h1 className="text-2xl font-bold tracking-tight text-zinc-900">고객센터</h1>
                <Link
                    href="/support/inquiries/new"
                    className="flex h-10 items-center rounded-md bg-zinc-900 px-4 text-sm font-semibold text-white transition hover:bg-zinc-700"
                >
                    문의하기
                </Link>
            </div>

            <dl className="mt-6 flex flex-wrap gap-x-6 gap-y-1 text-sm">
                {contacts.map((contact) => (
                    <div key={contact.label} className="flex gap-2">
                        <dt className="text-zinc-500">{contact.label}</dt>
                        <dd className="text-zinc-900">{contact.value}</dd>
                    </div>
                ))}
            </dl>

            <ul className="mt-4 flex flex-wrap gap-x-4 text-sm">
                {HELP_LINKS.map((link) => (
                    <li key={link.href}>
                        <Link href={link.href} className="text-zinc-500 underline underline-offset-4 hover:text-zinc-900">
                            {link.label}
                        </Link>
                    </li>
                ))}
            </ul>

            <section className="mt-12" aria-labelledby="inquiry-title">
                <h2 id="inquiry-title" className="text-lg font-semibold text-zinc-900">
                    문의 게시판
                </h2>
                <p className="mt-1 text-sm text-zinc-500">문의 내용과 답변은 작성자 본인만 볼 수 있습니다.</p>

                {inquiryPage === null ? (
                    <p className="mt-6 border-t border-zinc-200 py-10 text-center text-sm text-zinc-500">
                        문의 목록을 불러오지 못했습니다. 잠시 후 다시 시도해주세요.
                    </p>
                ) : inquiryPage.content.length === 0 ? (
                    <p className="mt-6 border-t border-zinc-200 py-10 text-center text-sm text-zinc-500">
                        아직 등록된 문의가 없습니다.
                    </p>
                ) : (
                    <>
                        <div className="mt-6 hidden grid-cols-[4rem_6rem_1fr_6rem_6rem_6rem] gap-3 border-y border-zinc-900 py-3 text-center text-xs font-medium text-zinc-500 sm:grid">
                            <span>번호</span>
                            <span>분류</span>
                            <span>제목</span>
                            <span>작성자</span>
                            <span>작성일</span>
                            <span>상태</span>
                        </div>
                        <ul className="mt-6 border-t border-zinc-900 sm:mt-0 sm:border-t-0">
                            {inquiryPage.content.map((inquiry, index) => {
                                const number = inquiryPage.totalElements - (inquiryPage.page * inquiryPage.size + index);
                                return (
                                    <li key={inquiry.inquiryId} className="border-b border-zinc-200">
                                        <Link
                                            href={`/support/inquiries/${inquiry.inquiryId}`}
                                            className="grid grid-cols-[1fr_auto] gap-x-3 gap-y-1 py-4 text-sm transition hover:bg-zinc-50 sm:grid-cols-[4rem_6rem_1fr_6rem_6rem_6rem] sm:items-center sm:py-3 sm:text-center"
                                        >
                                            <span className="hidden text-zinc-400 sm:block">{number}</span>
                                            <span className="hidden text-zinc-500 sm:block">{categoryLabel(inquiry.category)}</span>
                                            <span className="min-w-0 truncate text-left text-zinc-900">
                                                <span className="mr-2 text-xs text-zinc-400 sm:hidden">{categoryLabel(inquiry.category)}</span>
                                                {inquiry.title}
                                            </span>
                                            <span className="hidden text-zinc-500 sm:block">{inquiry.writerName}</span>
                                            <span className="col-start-1 text-xs text-zinc-400 sm:col-auto sm:text-sm">
                                                <span className="sm:hidden">{inquiry.writerName} · </span>
                                                {formatInquiryDate(inquiry.createdAt)}
                                            </span>
                                            <span
                                                className={`col-start-2 row-span-2 row-start-1 self-center text-xs font-medium sm:col-auto sm:row-auto ${
                                                    inquiry.answered ? "text-zinc-900" : "text-zinc-400"
                                                }`}
                                            >
                                                {inquiry.answered ? "답변 완료" : "답변 대기"}
                                            </span>
                                        </Link>
                                    </li>
                                );
                            })}
                        </ul>

                        {inquiryPage.totalPages > 1 && (
                            <nav aria-label="문의 목록 페이지" className="mt-8 flex items-center justify-center gap-4 text-sm">
                                {page > 0 ? (
                                    <Link href={`/support?page=${page - 1}`} className="text-zinc-900 hover:underline">
                                        이전
                                    </Link>
                                ) : (
                                    <span className="text-zinc-300">이전</span>
                                )}
                                <span className="text-zinc-500">
                                    {page + 1} / {inquiryPage.totalPages}
                                </span>
                                {inquiryPage.hasNext ? (
                                    <Link href={`/support?page=${page + 1}`} className="text-zinc-900 hover:underline">
                                        다음
                                    </Link>
                                ) : (
                                    <span className="text-zinc-300">다음</span>
                                )}
                            </nav>
                        )}
                    </>
                )}
            </section>
        </div>
    );
}
