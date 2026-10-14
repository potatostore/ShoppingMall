'use client';

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import FormField from "../components/FormField";
import { CLIENT_API_URL } from "@/lib/api";

const userApiUrl = `${CLIENT_API_URL}/users`;

export default function SignInPage(){
    const router = useRouter();

    // state === login
    const [email, setEmail] = useState<string>('');
    const [logInPassword, setLogInPassword] = useState<string>('');
    const [submitting, setSubmitting] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    const handleSignIn_Kakao = async () => {
        const signIn_KakaoData = {

        };

        try {

        } catch(error){

        }
    }

    const handleSignIn_Google = async () => {
        const signIn_GoogleData = {

        };

        try {

        } catch(error){

        }
    }

    const handleLogIn = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setSubmitting(true);
        setError(null);

        const signInData = {
            email,
            logInPassword
        };

        try{
            const response = await fetch(userApiUrl + '/login',{
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(signInData)
            });

            if(response.ok){
                router.push('/');
                // Header(서버 컴포넌트)가 새 쿠키 기준으로 다시 렌더링되도록 갱신
                router.refresh();
            } else{
                setError('이메일 또는 비밀번호가 올바르지 않습니다.');
            }
        } catch{
            setError('통신 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
        } finally{
            setSubmitting(false);
        }
    }

    return(
        <div className="mx-auto w-full max-w-sm px-6 py-20">
            <h1 className="text-center text-2xl font-bold tracking-tight text-zinc-900">로그인</h1>
            <p className="mt-2 text-center text-sm text-zinc-500">감자몰 계정으로 로그인하세요.</p>

            <form onSubmit={handleLogIn} className="mt-10 flex flex-col gap-5">
                <FormField
                    id="email"
                    label="이메일"
                    type="email"
                    autoComplete="email"
                    placeholder="example@email.com"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <FormField
                    id="password"
                    label="비밀번호"
                    type="password"
                    autoComplete="current-password"
                    placeholder="비밀번호를 입력하세요"
                    required
                    value={logInPassword}
                    onChange={(e) => setLogInPassword(e.target.value)}
                />

                {error && <p role="alert" className="text-sm text-red-600">{error}</p>}

                <button
                    type="submit"
                    disabled={submitting}
                    className="mt-1 h-12 rounded-md bg-zinc-900 text-sm font-semibold text-white transition hover:bg-zinc-700 disabled:cursor-not-allowed disabled:opacity-50"
                >
                    {submitting ? '로그인 중...' : '로그인'}
                </button>
            </form>

            <div className="mt-6 flex items-center justify-center gap-3 text-sm text-zinc-500">
                <Link href="/find-id" className="hover:text-zinc-900">아이디 찾기</Link>
                <span className="h-3 w-px bg-zinc-300" />
                <Link href="/find-password" className="hover:text-zinc-900">비밀번호 찾기</Link>
                <span className="h-3 w-px bg-zinc-300" />
                <Link href="/signup" className="font-semibold text-zinc-900">회원가입</Link>
            </div>

            <div className="my-10 flex items-center gap-4 text-xs text-zinc-400">
                <span className="h-px flex-1 bg-zinc-200" />
                간편 로그인
                <span className="h-px flex-1 bg-zinc-200" />
            </div>

            {/* 소셜 로그인 구현 전까지 비활성화 — 구현 후 disabled 제거 */}
            <div className="flex flex-col gap-3">
                <button
                    type="button"
                    onClick={handleSignIn_Kakao}
                    disabled
                    className="flex h-12 items-center justify-center gap-2 rounded-md border border-zinc-300 text-sm font-medium text-zinc-900 disabled:cursor-not-allowed disabled:text-zinc-400"
                >
                    카카오로 로그인
                    <span className="text-xs text-zinc-400">(준비 중)</span>
                </button>
                <button
                    type="button"
                    onClick={handleSignIn_Google}
                    disabled
                    className="flex h-12 items-center justify-center gap-2 rounded-md border border-zinc-300 text-sm font-medium text-zinc-900 disabled:cursor-not-allowed disabled:text-zinc-400"
                >
                    구글로 로그인
                    <span className="text-xs text-zinc-400">(준비 중)</span>
                </button>
            </div>
        </div>
    );
}
