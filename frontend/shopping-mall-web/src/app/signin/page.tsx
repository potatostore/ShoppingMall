'use client';

import { useState } from "react";

export default function signUpPage(){
    const [name, setName] = useState<string>('');
    const [email, setEmail] = useState<string>('');
    const [logInId, setId] = useState<string>('');
    const [password, setPassword] = useState<string>('')
    const [phoneNumber, setPhoneNumber] = useState<string>('');
    const [birthday, setBirthday] = useState<string>('');

    const handleSignUp = async () => {
        const createdAt = new Date().toISOString();

        const signUpData = {
            name,
            email,
            logInId,
            password,
            phoneNumber,
            birthday,
            createdAt
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
    
    return(
        <div>
            <h1>회원가입</h1>
            <input 
                type="text" 
                placeholder="ID"
                value={logInId} 
                onChange={(e) => {setId(e.target.value)}}></input>
            <input 
                type="text" 
                placeholder="Password"
                value={password}
                onChange={(e) => {setPassword(e.target.value)}}></input> 
            <input
                type="text"
                placeholder="email"
                value={email}
                onChange={(e) => {setEmail(e.target.value)}}></input>
            <input
                type="text"
                placeholder="name"
                value={name}
                onChange={(e) => {setName(e.target.value)}}></input>
            <input
                type="text"
                placeholder="PhoneNumber"
                value={phoneNumber}
                onChange={(e) => {setPhoneNumber(e.target.value)}}></input>
             <input
                type="date"
                placeholder="Birthday"
                value={birthday}
                onChange={(e) => {setBirthday(e.target.value)}}></input>

            <button onClick={handleSignUp}>회원가입</button>
        </div>
    );
}