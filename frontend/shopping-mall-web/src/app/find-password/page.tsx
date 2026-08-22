'use client';

import Link from "next/link";
import { useState } from "react";

const findPasswordApiUrl = "http://localhost:8080/api/v1/users/find-password";

export default function FindPasswordPage() {
    const [email, setEmail] = useState<string>('');
    const [name, setName] = useState<string>('');
    const [phoneNumber, setPhoneNumber] = useState<string>('');
    const [newPassword, setNewPassword] = useState<string>('');
    const [newPasswordCheck, setNewPasswordCheck] = useState<string>('');
    const [submitting, setSubmitting] = useState<boolean>(false);
    const [success, setSuccess] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async () => {
        if (newPassword !== newPasswordCheck) {
            setError('새 비밀번호가 서로 일치하지 않습니다.');
            return;
        }

        setSubmitting(true);
        setError(null);

        try {
            const response = await fetch(findPasswordApiUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, name, phoneNumber, newPassword }),
            });

            if (!response.ok) {
                setError('일치하는 회원 정보가 없습니다.');
                return;
            }

            setSuccess(true);
        } catch {
            setError('통신 오류가 발생했습니다.');
        } finally {
            setSubmitting(false);
        }
    };

    if (success) {
        return (
            <div className="mx-auto max-w-sm p-6">
                <h1 className="mb-4 text-2xl font-bold">비밀번호 변경 완료</h1>
                <p className="mb-4 text-sm text-zinc-600">새 비밀번호로 다시 로그인해주세요.</p>
                <Link href="/login" className="text-sm text-zinc-500 hover:text-zinc-900">
                    로그인하러 가기 →
                </Link>
            </div>
        );
    }

    return (
        <div className="mx-auto max-w-sm p-6">
            <h1 className="mb-6 text-2xl font-bold">비밀번호 찾기</h1>

            <div className="flex flex-col gap-3">
                <input
                    type="text"
                    placeholder="이메일"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="rounded border border-zinc-300 px-3 py-2"
                />
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
                <hr className="my-1 border-zinc-200" />
                <input
                    type="password"
                    placeholder="새 비밀번호"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    className="rounded border border-zinc-300 px-3 py-2"
                />
                <input
                    type="password"
                    placeholder="새 비밀번호 확인"
                    value={newPasswordCheck}
                    onChange={(e) => setNewPasswordCheck(e.target.value)}
                    className="rounded border border-zinc-300 px-3 py-2"
                />
                <button
                    onClick={handleSubmit}
                    disabled={submitting}
                    className="rounded bg-zinc-900 py-2 text-white disabled:opacity-50"
                >
                    {submitting ? '처리 중...' : '비밀번호 변경'}
                </button>
            </div>

            {error && <p className="mt-4 text-sm text-red-600">{error}</p>}

            <Link href="/login" className="mt-6 inline-block text-sm text-zinc-500 hover:text-zinc-900">
                ← 로그인으로 돌아가기
            </Link>
        </div>
    );
}
