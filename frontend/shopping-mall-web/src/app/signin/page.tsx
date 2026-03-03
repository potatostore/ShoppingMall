'use client';

import { useState } from "react";

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

    // state === findId
    const [findIdName, setFindIdName] = useState<string>('');
    const [findIdPhoneNumber, setFindIdPhoneNumber] = useState<string>('');

    // state === findPassword
    const [findPasswordId, setFindPasswordId] = useState<string>('');

    const handleSignIn = async () => {
        const signInData = {
            signInId,
            signInPassword
        };

        console.log(signInData);

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

    const convert2SignUp = () => {
        setState('signUp');
    }

    const convert2FindId = () => {
        setState('findId');
    }

    const convert2FindPassword = () => {
        setState('findPassword');
    }
    
    return(
        <div>
            <h1>로그인</h1>
            <div>
                <input 
                    type="text"
                    placeholder="ID"
                    value={signInId}
                    onChange={(e) => {setSignInId(e.target.value)}}></input>
                <input 
                    type="text"
                    placeholder="Password"
                    value={signInPassword}
                    onChange={(e) => {setSignInPassword(e.target.value)}}></input>
                <button onClick={handleSignIn}>로그인</button>
                <button onClick={convert2SignUp}>회원가입</button>
                <button onClick={convert2FindId}>ID찾기</button>
                <button onClick={convert2FindPassword}>비밀번호찾기</button>
            </div>
            {(state === 'signUp') && (
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
            {(state === 'findId') && (
                <div>
                    <input 
                        type="text"
                        placeholder="이름"
                        value={findIdName}
                        onChange={(e) => {setFindIdName(e.target.value)}}></input>
                    <input
                        type="text"
                        placeholder="전화번호"
                        value={findIdPhoneNumber}
                        onChange={(e) => {setFindIdPhoneNumber(e.target.value)}}></input>
                </div>
            )}
            {(state === 'findPassword') && (
                <div>
                    <input
                        type="text"
                        placeholder="Id"
                        value={findPasswordId}
                        onChange={(e) => {setFindPasswordId(e.target.value)}}></input>
                </div>
            )}
        </div>
    );
}

