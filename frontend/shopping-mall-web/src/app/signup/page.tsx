'use client';

import { useState } from "react";
import { useRouter } from "next/navigation";
import { CLIENT_API_URL } from "@/lib/api";

const userApiUrl = `${CLIENT_API_URL}/users`;

export default function SignUpPage() {
    const router = useRouter();

    // signUpState : email, kakao, google
    const [signUpState, setSignUpState] = useState<string>('email');

    const [signUpName, setSignUpName] = useState<string>('');
    const [signUpEmail, setSignUpEmail] = useState<string>('');
    const [signUpPassword, setSignUpPassword] = useState<string>('');
    const [signUpRole, setSignUpRole] = useState<string>('');
    const [signUpPhoneNumber, setSignUpPhoneNumber] = useState<string>('');
    const [signUpBirthday, setSignUpBirthday] = useState<string>('');

    const handleSignUp = async () => {
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
                const result = await response.json();
                alert('회원가입 성공! ID: ' + result.id);
                router.push('/login');
            } else {
                alert('실패');
            }
        } catch (error) {
            console.error('통신 에러 발생', error);
        }
    };

    const handleSignUp_Kakao = async () => {

    };

    const handleSignUP_Google = async () => {

    };

    const convertSignUpState2Default = () => {
        setSignUpState('email');
    };

    const convertSignUpState2Kakao = () => {
        setSignUpState('kakao');
    };

    const convertSignUpState2Google = () => {
        setSignUpState('google');
    };

    return (
        <div>
            <div className="header">회원가입</div>
            <div>
                <button onClick={convertSignUpState2Default}>이메일로 회원가입하기</button>
                <button onClick={convertSignUpState2Kakao}>카카오로 회원가입하기</button>
                <button onClick={convertSignUpState2Google}>구글로 회원가입하기</button>
            </div>

            {signUpState === 'email' && (
                <div>
                    <input
                        type="text"
                        placeholder="email"
                        value={signUpEmail}
                        onChange={(e) => setSignUpEmail(e.target.value)}></input>
                    <input
                        type="text"
                        placeholder="Password"
                        value={signUpPassword}
                        onChange={(e) => setSignUpPassword(e.target.value)}></input>
                    <input
                        type="text"
                        placeholder="Role"
                        value={signUpRole}
                        onChange={(e) => setSignUpRole(e.target.value)}></input>
                    <input
                        type="text"
                        placeholder="name"
                        value={signUpName}
                        onChange={(e) => setSignUpName(e.target.value)}></input>
                    <input
                        type="text"
                        placeholder="PhoneNumber"
                        value={signUpPhoneNumber}
                        onChange={(e) => setSignUpPhoneNumber(e.target.value)}></input>
                    <input
                        type="date"
                        placeholder="Birthday"
                        value={signUpBirthday}
                        onChange={(e) => setSignUpBirthday(e.target.value)}></input>

                    <button onClick={handleSignUp}>회원가입</button>
                </div>
            )}

            {signUpState === 'kakao' && (
                <div>

                </div>
            )}

            {signUpState === 'google' && (
                <div>

                </div>
            )}
        </div>
    );
}
