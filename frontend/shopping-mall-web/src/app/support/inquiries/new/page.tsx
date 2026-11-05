'use client';

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import FormField from "../../../components/FormField";
import { CLIENT_API_URL, authFetch } from "@/lib/api";
import {
    INQUIRY_CATEGORIES,
    INQUIRY_CONTENT_MAX,
    INQUIRY_PATH,
    INQUIRY_TITLE_MAX,
    type InquiryCategory,
} from "@/lib/inquiry";

export default function NewInquiryPage() {
    const router = useRouter();
    const [category, setCategory] = useState<InquiryCategory>("ORDER");
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [needLogin, setNeedLogin] = useState(false);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!title.trim() || !content.trim()) {
            setError("제목과 내용을 입력해주세요.");
            return;
        }

        setSubmitting(true);
        setError(null);
        setNeedLogin(false);

        try {
            const response = await authFetch(`${CLIENT_API_URL}${INQUIRY_PATH}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ category, title: title.trim(), content: content.trim() }),
            });

            if (response.ok) {
                const { data } = await response.json();
                router.push(data?.inquiryId ? `/support/inquiries/${data.inquiryId}` : "/support");
                router.refresh();
                return;
            }
            if (response.status === 401) {
                setNeedLogin(true);
                setError("로그인한 사용자만 문의를 남길 수 있습니다.");
            } else {
                setError("문의를 등록하지 못했습니다. 입력 내용을 확인해주세요.");
            }
        } catch {
            setError("통신 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="mx-auto w-full max-w-2xl px-6 py-20">
            <Link href="/support" className="text-sm text-zinc-500 hover:text-zinc-900">
                ← 고객센터
            </Link>
            <h1 className="mt-4 text-2xl font-bold tracking-tight text-zinc-900">문의하기</h1>
            <p className="mt-2 text-sm text-zinc-500">문의 내용과 답변은 작성한 본인만 볼 수 있습니다. 목록에는 제목과 가려진 이름만 보입니다.</p>

            <form onSubmit={handleSubmit} className="mt-10 flex flex-col gap-5">
                <div className="flex flex-col gap-2">
                    <label htmlFor="category" className="text-sm font-medium text-zinc-900">
                        분류
                    </label>
                    <select
                        id="category"
                        value={category}
                        onChange={(e) => setCategory(e.target.value as InquiryCategory)}
                        className="h-12 rounded-md border border-zinc-300 bg-white px-4 text-sm text-zinc-900 focus:border-zinc-900 focus:outline-none"
                    >
                        {INQUIRY_CATEGORIES.map((item) => (
                            <option key={item.value} value={item.value}>
                                {item.label}
                            </option>
                        ))}
                    </select>
                </div>

                <FormField
                    id="title"
                    label="제목"
                    type="text"
                    placeholder="문의 제목을 입력하세요"
                    maxLength={INQUIRY_TITLE_MAX}
                    required
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                />

                <div className="flex flex-col gap-2">
                    <label htmlFor="content" className="text-sm font-medium text-zinc-900">
                        내용
                    </label>
                    <textarea
                        id="content"
                        rows={10}
                        maxLength={INQUIRY_CONTENT_MAX}
                        required
                        placeholder="주문번호, 상품명 등을 함께 적어주시면 빠르게 확인할 수 있습니다."
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                        className="resize-y rounded-md border border-zinc-300 bg-white px-4 py-3 text-sm leading-6 text-zinc-900 placeholder:text-zinc-400 focus:border-zinc-900 focus:outline-none"
                    />
                    <span className="text-right text-xs text-zinc-400">
                        {content.length} / {INQUIRY_CONTENT_MAX}
                    </span>
                </div>

                {error && (
                    <p role="alert" className="text-sm text-red-600">
                        {error}
                        {needLogin && (
                            <Link href="/login" className="ml-2 font-semibold text-zinc-900 underline underline-offset-4">
                                로그인
                            </Link>
                        )}
                    </p>
                )}

                <button
                    type="submit"
                    disabled={submitting}
                    className="mt-1 h-12 rounded-md bg-zinc-900 text-sm font-semibold text-white transition hover:bg-zinc-700 disabled:cursor-not-allowed disabled:opacity-50"
                >
                    {submitting ? "등록 중..." : "등록하기"}
                </button>
            </form>
        </div>
    );
}
