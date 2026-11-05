'use client';

import Link from "next/link";
import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { CLIENT_API_URL, authFetch } from "@/lib/api";
import { INQUIRY_PATH, categoryLabel, formatInquiryDate, type InquiryDetail } from "@/lib/inquiry";

type LoadState =
    | { kind: "loading" }
    | { kind: "loaded"; inquiry: InquiryDetail }
    | { kind: "error"; message: string; showLogin?: boolean };

// 본문과 답변은 작성자와 관리자만 볼 수 있다. 결과에 맞는 화면 상태만 돌려준다
async function loadInquiry(inquiryId: string): Promise<LoadState> {
    if (!/^\d+$/.test(inquiryId)) {
        return { kind: "error", message: "존재하지 않는 문의입니다." };
    }

    try {
        const response = await authFetch(`${CLIENT_API_URL}${INQUIRY_PATH}/${inquiryId}`);
        if (response.ok) {
            const { data } = await response.json();
            return { kind: "loaded", inquiry: data as InquiryDetail };
        }
        if (response.status === 401) {
            return { kind: "error", message: "로그인하면 내가 작성한 문의를 볼 수 있습니다.", showLogin: true };
        }
        if (response.status === 403) {
            return { kind: "error", message: "작성자 본인만 볼 수 있는 문의입니다." };
        }
        if (response.status === 404) {
            return { kind: "error", message: "존재하지 않는 문의입니다." };
        }
        return { kind: "error", message: "문의를 불러오지 못했습니다. 잠시 후 다시 시도해주세요." };
    } catch {
        return { kind: "error", message: "통신 오류가 발생했습니다. 잠시 후 다시 시도해주세요." };
    }
}

export default function InquiryDetailPage() {
    const { inquiryId } = useParams<{ inquiryId: string }>();
    const [state, setState] = useState<LoadState>({ kind: "loading" });

    useEffect(() => {
        let cancelled = false;
        loadInquiry(inquiryId).then((result) => {
            if (!cancelled) {
                setState(result);
            }
        });
        return () => {
            cancelled = true;
        };
    }, [inquiryId]);

    return (
        <div className="mx-auto w-full max-w-2xl px-6 py-20">
            <Link href="/support" className="text-sm text-zinc-500 hover:text-zinc-900">
                ← 문의 목록
            </Link>

            {state.kind === "loading" && <p className="mt-10 text-sm text-zinc-500">불러오는 중...</p>}

            {state.kind === "error" && (
                <div className="mt-10 border-y border-zinc-200 py-10 text-center">
                    <p role="alert" className="text-sm text-zinc-600">
                        {state.message}
                    </p>
                    {state.showLogin && (
                        <Link href="/login" className="mt-4 inline-block text-sm font-semibold text-zinc-900 underline underline-offset-4">
                            로그인
                        </Link>
                    )}
                </div>
            )}

            {state.kind === "loaded" && (
                <article className="mt-6">
                    <header className="border-b border-zinc-900 pb-4">
                        <p className="text-xs text-zinc-500">{categoryLabel(state.inquiry.category)}</p>
                        <h1 className="mt-1 text-xl font-bold text-zinc-900">{state.inquiry.title}</h1>
                        <p className="mt-2 text-xs text-zinc-500">
                            {state.inquiry.writerName} · {formatInquiryDate(state.inquiry.createdAt)} ·{" "}
                            <span className={state.inquiry.answered ? "text-zinc-900" : ""}>
                                {state.inquiry.answered ? "답변 완료" : "답변 대기"}
                            </span>
                        </p>
                    </header>

                    <p className="whitespace-pre-wrap border-b border-zinc-200 py-6 text-sm leading-7 text-zinc-800">
                        {state.inquiry.content}
                    </p>

                    <section className="mt-6 rounded-md bg-zinc-50 p-5" aria-label="답변">
                        <h2 className="text-sm font-semibold text-zinc-900">감자몰 답변</h2>
                        {state.inquiry.answer ? (
                            <>
                                <p className="mt-3 whitespace-pre-wrap text-sm leading-7 text-zinc-800">{state.inquiry.answer.content}</p>
                                <p className="mt-3 text-xs text-zinc-500">{formatInquiryDate(state.inquiry.answer.answeredAt)}</p>
                            </>
                        ) : (
                            <p className="mt-3 text-sm text-zinc-500">아직 답변이 등록되지 않았습니다. 확인 후 답변드리겠습니다.</p>
                        )}
                    </section>
                </article>
            )}
        </div>
    );
}
