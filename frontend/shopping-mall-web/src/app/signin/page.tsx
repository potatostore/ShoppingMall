'use client';

import React, { useState } from "react";

export default function signinPage(){
    // state : signIn, signUp, findId, findPassword
    const [state, setState] = useState<string>('signIn');

    // state === signIn(default)
    const [signInId, setSignInId] = useState<string>('');
    const [signInPassword, setSignInPassword] = useState<string>('');

    // state === signUp
    const [signUpName, setSignUpName] = useState<string>('');
    const [signUpEmail, setSignUpEmail] = useState<string>('');
    const [signUpId, setSignUpId] = useState<string>('');
    const [signUpPassword, setSignUpPassword] = useState<string>('')
    const [signUpPhoneNumber, setSignUpPhoneNumber] = useState<string>('');
    const [signUpBirthday, setSignUpBirthday] = useState<string>('');
    const [signUpState, setSignUpState] = useState<string>('default');

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

    const handleSignIn = async () => {
        const signInData = {
            signInId,
            signInPassword
        };

        try{
            const response = await fetch('http://localhost:8080/users/signin',{
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
            signUpId,
            signUpPassword,
            signUpPhoneNumber,
            signUpBirthday,
            signUpCreatedAt
        };

        console.log(signUpData);
        try{
            const response = await fetch('http://localhost:8080/users',{
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
            const response = await fetch('http://localhost:8080/users',{
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
        setSignUpState('default');
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
            <div className="signin-page-container">
                <div className="header">로그인하기</div>
                <input 
                    type="text" 
                    placeholder="ID"
                    className="input-signinid"
                    value={signInId}
                    onChange={(e) => {setSignInId(e.target.value)}}></input>
                <input 
                    type="text" 
                    placeholder="password"
                    className="input-signinpassword"
                    value={signInPassword}
                    onChange={(e) => {setSignInPassword(e.target.value)}}></input>
                <button onClick={handleSignIn}>로그인</button>
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
            {(state === 'signUp' && signUpState === 'default') && (
                <div>
                    <input 
                        type="text" 
                        placeholder="ID"
                        value={signUpId} 
                        onChange={(e) => {setSignUpId(e.target.value)}}></input>
                    <input 
                        type="text" 
                        placeholder="Password"
                        value={signUpPassword}
                        onChange={(e) => {setSignUpPassword(e.target.value)}}></input> 
                    <input
                        type="text"
                        placeholder="email"
                        value={signUpEmail}
                        onChange={(e) => {setSignUpEmail(e.target.value)}}></input>
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

