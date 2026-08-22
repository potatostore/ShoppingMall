'use client';

import Link from "next/link";
import { useState } from "react";

const findIdApiUrl = "http://localhost:8080/api/v1/users/find-id";

export default function FindIdPage() {
    const [name, setName] = useState<string>('');
    const [phoneNumber, setPhoneNumber] = useState<string>('');
    const [submitting, setSubmitting] = useState<boolean>(false);
    const [result, setResult] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async () => {
        setSubmitting(true);
        setResult(null);
        setError(null);

        try {
            const params = new URLSearchParams({ name, phoneNumber });
            const response = await fetch(`${findIdApiUrl}?${params.toString()}`);

            if (!response.ok) {
                setError('일치하는 회원 정보가 없습니다.');
                return;
            }

            const { data } = await response.json();
            setResult(data);
        } catch {
            setError('통신 오류가 발생했습니다.');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="mx-auto max-w-sm p-6">
            <h1 className="mb-6 text-2xl font-bold">아이디(이메일) 찾기</h1>

            <div className="flex flex-col gap-3">
                <input
                    type="text"
                    placeholder="이름"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    className="rounded border border-zinc-300 px-3 py-2"
                />
                <input
                    type="text"
                    placeholder="전화번호"
                    value={phoneNumber}
                    onChange={(e) => setPhoneNumber(e.target.value)}
                    className="rounded border border-zinc-300 px-3 py-2"
                />
                <button
                    onClick={handleSubmit}
                    disabled={submitting}
                    className="rounded bg-zinc-900 py-2 text-white disabled:opacity-50"
                >
                    {submitting ? '조회 중...' : '아이디 찾기'}
                </button>
            </div>

            {result && (
                <p className="mt-4 rounded bg-zinc-100 p-3 text-sm">
                    가입하신 이메일: <span className="font-medium">{result}</span>
                </p>
            )}
            {error && <p className="mt-4 text-sm text-red-600">{error}</p>}

            <Link href="/login" className="mt-6 inline-block text-sm text-zinc-500 hover:text-zinc-900">
                ← 로그인으로 돌아가기
            </Link>
        </div>
    );
}
