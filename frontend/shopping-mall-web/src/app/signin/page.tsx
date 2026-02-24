'use client';

import { useState } from "react";

export default function signUpPage(){
    const [id, setId] = useState<string>();
    const [password, setPassword] = useState<string>();
    const [email, setEmail] = useState<string>();
    
    return(
        <div>
            <h1>회원가입</h1>
            <input 
                type="text" 
                placeholder="ID"
                value={id} 
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
        </div>
    );
}