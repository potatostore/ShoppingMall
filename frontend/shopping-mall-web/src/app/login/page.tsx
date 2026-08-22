'use client';

import React, { useState } from "react";

const userApiUrl = 'http://localhost:8080/api/v1/users';

export default function signinPage(){
    // state : logIn, signUp, findId, findPassword
    const [state, setState] = useState<string>('login');

    // state === login
    const [email, setEmail] = useState<string>('');
    const [logInPassword, setLogInPassword] = useState<string>('');

    // state === signUp
    const [signUpState, setSignUpState] = useState<string>('email');

    const [signUpName, setSignUpName] = useState<string>('');
    const [signUpEmail, setSignUpEmail] = useState<string>('');
    const [signUpPassword, setSignUpPassword] = useState<string>('')
    const [signUpRole, setSignUpRole] = useState<string>('');
    const [signUpPhoneNumber, setSignUpPhoneNumber] = useState<string>('');
    const [signUpBirthday, setSignUpBirthday] = useState<string>('');

    // state === findId
    const [findIdName, setFindIdName] = useState<string>('');
    const [findIdPhoneNumber, setFindIdPhoneNumber] = useState<string>('');

    // state === findPassword
    const [findPasswordId, setFindPasswordId] = useState<string>('');

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
                body: JSON.stringify(signInData)
            });

            if(response.ok){
                const result = await response.json();
                alert('로그인 성공!');
                console.log('서버응답 : ' + result);
                //메인 페이지로 돌아가기
            } else{
                alert('실패');
            }
        } catch(error){
            console.log('통신 오류');
        }
    }

    const handleSignUp = async () => {
        const signUpCreatedAt = new Date().toISOString();

        const signUpData = {
            signUpName,
            signUpEmail,
            signUpPassword,
            signUpRole,
            signUpPhoneNumber,
            signUpBirthday,
        };

        console.log(signUpData);
        try{
            const response = await fetch(userApiUrl + "/signup", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(signUpData),
            });

            if(response.ok){
                const result = await response.json();
                alert('회원가입 성공! ID: ' + result.id);
                console.log('서버응답 : ', result);
            } else{
                alert('실패');
            }
        } catch(error){
            console.error('통신 에러 발생', error);
        }
    }

    const handleSignUp_Kakao = async () => {

    }

    const handleSignUP_Google = async () => {
        
    }

    const handleFindId = async () => {
        const findIdData = {
            findIdName, 
            findIdPhoneNumber
        };

        console.log(findIdData);

        try{
            const response = await fetch(userApiUrl,{
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(findIdData)
            });

            if(response.ok){
                const result = await response.json();
                alert('ID : ' + result.id);
                console.log('ID : ' + result.id);
            }
        } catch(error){
            alert('통신 오류 발생');
        }
    }

    const handleFindPassword = async () => {
        const findPasswordData = {
            findPasswordId
        };

        console.log(findPasswordData);

        try{
            
        } catch(error){
            alert('통신 오류');
        }
    }

    const convertState2SignUp = () => {
        setState('signUp');
    }

    const convertState2FindId = () => {
        setState('findId');
    }

    const convertState2FindPassword = () => {
        setState('findPassword');
    }

    const convertSignUpState2Default = () => {
        setSignUpState('email');
    }

    const convertSignUpState2Kakao = () => {
        setSignUpState('kakao');
    }

    const convertSignUpState2Google = () => {
        setSignUpState('google');
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
                <button onClick={convertState2SignUp}>회원가입</button>
                <button onClick={convertState2FindId}>ID찾기</button>
                <button onClick={convertState2FindPassword}>Password찾기</button>
                <div className="divider"></div>
            </div>
            {(state === 'signUp') && (
                <div>
                    <button 
                        onClick={convertSignUpState2Default}>이메일로 회원가입하기</button>
                    <button
                        onClick={convertSignUpState2Kakao}>카카오로 회원가입하기</button>
                    <button
                        onClick={convertSignUpState2Google}>구글로 회원가입하기</button>
                </div>
            )}
            {(state === 'signUp' && signUpState === 'email') && (
                <div>
                    <input
                        type="text"
                        placeholder="email"
                        value={signUpEmail}
                        onChange={(e) => {setSignUpEmail(e.target.value)}}></input>
                    <input 
                        type="text" 
                        placeholder="Password"
                        value={signUpPassword}
                        onChange={(e) => {setSignUpPassword(e.target.value)}}></input> 
                    <input 
                        type="text" 
                        placeholder="Role"
                        value={signUpRole}
                        onChange={(e) => {setSignUpRole(e.target.value)}}></input> 
                    <input
                        type="text"
                        placeholder="name"
                        value={signUpName}
                        onChange={(e) => {setSignUpName(e.target.value)}}></input>
                    <input
                        type="text"
                        placeholder="PhoneNumber"
                        value={signUpPhoneNumber}
                        onChange={(e) => {setSignUpPhoneNumber(e.target.value)}}></input>
                    <input
                        type="date"
                        placeholder="Birthday"
                        value={signUpBirthday}
                        onChange={(e) => {setSignUpBirthday(e.target.value)}}></input>

                    <button onClick={handleSignUp}>회원가입</button>
                </div>
            )}
            {(state === 'signUp' && signUpState === 'kakao') && (
                <div>
                    
                </div>
            )}
            {(state === 'signUp' && signUpState === 'google') && (
                <div>
                </div>
            )}
            {(state === 'findId') && (
                <div>

                </div>
            )}
            {(state === 'findPassword') && (
                <div>   

                </div>
            )}
        </div>
    );
}

