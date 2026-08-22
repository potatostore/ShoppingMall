'use client';

import React, { useState } from "react";
import { useRouter } from "next/navigation";

const userApiUrl = 'http://localhost:8080/api/v1/users';

export default function signinPage(){
    const router = useRouter();

    // state === login
    const [email, setEmail] = useState<string>('');
    const [logInPassword, setLogInPassword] = useState<string>('');

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

    const handleLogIn = async () => {
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
                const result = await response.json();
                alert('로그인 성공!');
                console.log('서버응답 : ' + result);
                router.push('/');
            } else{
                alert('실패');
            }
        } catch(error){
            console.log('통신 오류');
        }
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        return e.target.value;
    }
    
    return(
        <div>
            <div className="login-page-container">
                <div className="header">로그인하기</div>
                <input 
                    type="text" 
                    placeholder="email"
                    className="input-email"
                    value={email}
                    onChange={(e) => {setEmail(e.target.value)}}></input>
                <input 
                    type="text" 
                    placeholder="password"
                    className="input-signinpassword"
                    value={logInPassword}
                    onChange={(e) => {setLogInPassword(e.target.value)}}></input>
                <button onClick={handleLogIn}>로그인</button>
                <button 
                    className="signin-kakao-button"
                    onClick={handleSignIn_Kakao}>카카오로 로그인하기</button>
                <button 
                    className="signin-google-button"
                    onClick={handleSignIn_Google}>구글로 로그인하기</button>
                <br></br>
                <button onClick={() => router.push('/signup')}>회원가입</button>
                <button onClick={() => router.push('/find-id')}>ID찾기</button>
                <button onClick={() => router.push('/find-password')}>Password찾기</button>
                <div className="divider"></div>
            </div>
        </div>
    );
}

