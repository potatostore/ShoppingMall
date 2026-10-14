'use client';

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import FormField from "../components/FormField";
import { CLIENT_API_URL } from "@/lib/api";

const userApiUrl = `${CLIENT_API_URL}/users`;

const SIGN_UP_TABS = [
    { state: 'email', label: '이메일' },
    { state: 'kakao', label: '카카오' },
    { state: 'google', label: '구글' },
] as const;

export default function SignUpPage() {
    const router = useRouter();

    // signUpState : email, kakao, google
    const [signUpState, setSignUpState] = useState<string>('email');

    const [signUpName, setSignUpName] = useState<string>('');
    const [signUpEmail, setSignUpEmail] = useState<string>('');
    const [signUpPassword, setSignUpPassword] = useState<string>('');
    const [signUpRole, setSignUpRole] = useState<string>('USER');
    const [signUpPhoneNumber, setSignUpPhoneNumber] = useState<string>('');
    const [signUpBirthday, setSignUpBirthday] = useState<string>('');
    const [submitting, setSubmitting] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    const handleSignUp = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setSubmitting(true);
        setError(null);

        const signUpData = {
            signUpName,
            signUpEmail,
            signUpPassword,
            signUpRole,
            signUpPhoneNumber,
            signUpBirthday,
        };

        try {
            const response = await fetch(userApiUrl + "/signup", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(signUpData),
            });

            if (response.ok) {
                alert('회원가입이 완료되었습니다. 로그인해주세요.');
                router.push('/login');
            } else {
                setError('회원가입에 실패했습니다. 입력 정보를 확인하거나 이미 가입된 이메일인지 확인해주세요.');
            }
        } catch (error) {
            console.error('통신 에러 발생', error);
            setError('통신 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
        } finally {
            setSubmitting(false);
        }
    };

    const handleSignUp_Kakao = async () => {

    };

    const handleSignUP_Google = async () => {

    };

    return (
        <div className="mx-auto w-full max-w-md px-6 py-20">
            <h1 className="text-center text-2xl font-bold tracking-tight text-zinc-900">회원가입</h1>
            <p className="mt-2 text-center text-sm text-zinc-500">가입 방법을 선택하세요.</p>

            <div role="tablist" className="mt-10 grid grid-cols-3 border-b border-zinc-200">
                {SIGN_UP_TABS.map((tab) => {
                    const active = signUpState === tab.state;
                    return (
                        <button
                            key={tab.state}
                            type="button"
                            role="tab"
                            aria-selected={active}
                            onClick={() => setSignUpState(tab.state)}
                            className={`-mb-px border-b-2 py-3 text-sm transition ${
                                active
                                    ? 'border-zinc-900 font-semibold text-zinc-900'
                                    : 'border-transparent text-zinc-400 hover:text-zinc-700'
                            }`}
                        >
                            {tab.label}
                        </button>
                    );
                })}
            </div>

            {signUpState === 'email' && (
                <form onSubmit={handleSignUp} className="mt-8 flex flex-col gap-5">
                    <FormField
                        id="signUpEmail"
                        label="이메일"
                        type="email"
                        autoComplete="email"
                        placeholder="example@email.com"
                        required
                        value={signUpEmail}
                        onChange={(e) => setSignUpEmail(e.target.value)}
                    />
                    <FormField
                        id="signUpPassword"
                        label="비밀번호"
                        type="password"
                        autoComplete="new-password"
                        placeholder="비밀번호를 입력하세요"
                        required
                        value={signUpPassword}
                        onChange={(e) => setSignUpPassword(e.target.value)}
                    />
                    <FormField
                        id="signUpName"
                        label="이름"
                        type="text"
                        autoComplete="name"
                        placeholder="이름을 입력하세요"
                        required
                        value={signUpName}
                        onChange={(e) => setSignUpName(e.target.value)}
                    />
                    <FormField
                        id="signUpPhoneNumber"
                        label="휴대폰 번호"
                        type="tel"
                        autoComplete="tel"
                        placeholder="휴대폰 번호를 입력하세요"
                        required
                        value={signUpPhoneNumber}
                        onChange={(e) => setSignUpPhoneNumber(e.target.value)}
                    />
                    <FormField
                        id="signUpBirthday"
                        label="생년월일"
                        type="date"
                        required
                        value={signUpBirthday}
                        onChange={(e) => setSignUpBirthday(e.target.value)}
                    />

                    {/* TODO: 권한은 서버에서 결정해야 함 — 현재는 백엔드가 signUpRole을 그대로 저장하므로 개발용으로만 노출 */}
                    <div className="flex flex-col gap-2">
                        <label htmlFor="signUpRole" className="text-sm font-medium text-zinc-900">
                            회원 유형 <span className="font-normal text-zinc-400">(개발용)</span>
                        </label>
                        <select
                            id="signUpRole"
                            value={signUpRole}
                            onChange={(e) => setSignUpRole(e.target.value)}
                            className="h-12 rounded-md border border-zinc-300 bg-white px-4 text-sm text-zinc-900 transition focus:border-zinc-900 focus:outline-none"
                        >
                            <option value="USER">일반 회원</option>
                            <option value="ADMIN">관리자</option>
                        </select>
                    </div>

                    {error && <p role="alert" className="text-sm text-red-600">{error}</p>}

                    <button
                        type="submit"
                        disabled={submitting}
                        className="mt-1 h-12 rounded-md bg-zinc-900 text-sm font-semibold text-white transition hover:bg-zinc-700 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                        {submitting ? '가입 중...' : '가입하기'}
                    </button>
                </form>
            )}

            {/* 소셜 회원가입 구현 전까지 비활성화 — 구현 후 disabled 제거 */}
            {signUpState === 'kakao' && (
                <div className="mt-8 flex flex-col items-center gap-4 py-6 text-center">
                    <p className="text-sm text-zinc-500">카카오 회원가입은 준비 중입니다.</p>
                    <button
                        type="button"
                        onClick={handleSignUp_Kakao}
                        disabled
                        className="h-12 w-full rounded-md border border-zinc-300 text-sm font-medium text-zinc-900 disabled:cursor-not-allowed disabled:text-zinc-400"
                    >
                        카카오로 회원가입
                    </button>
                </div>
            )}

            {signUpState === 'google' && (
                <div className="mt-8 flex flex-col items-center gap-4 py-6 text-center">
                    <p className="text-sm text-zinc-500">구글 회원가입은 준비 중입니다.</p>
                    <button
                        type="button"
                        onClick={handleSignUP_Google}
                        disabled
                        className="h-12 w-full rounded-md border border-zinc-300 text-sm font-medium text-zinc-900 disabled:cursor-not-allowed disabled:text-zinc-400"
                    >
                        구글로 회원가입
                    </button>
                </div>
            )}

            <p className="mt-10 text-center text-sm text-zinc-500">
                이미 계정이 있으신가요?{' '}
                <Link href="/login" className="font-semibold text-zinc-900 underline underline-offset-4">
                    로그인
                </Link>
            </p>
        </div>
    );
}
