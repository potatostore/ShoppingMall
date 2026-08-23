import Link from "next/link";
import { cookies } from "next/headers";
import ProfileEditor from "./ProfileEditor";
import { SERVER_API_URL } from "@/lib/api";

const userApiUrl = `${SERVER_API_URL}/users`;

type UserProfile = {
    userId: number;
    name: string;
    email: string;
    role: string;
    phoneNumber: string;
    birthday: string;
};

async function getMyProfile(): Promise<UserProfile | null> {
    const cookieStore = await cookies();

    const res = await fetch(`${userApiUrl}/me`, {
        cache: "no-store",
        headers: { Cookie: cookieStore.toString() },
    });

    if (!res.ok) {
        return null;
    }

    const { data } = await res.json();
    return data;
}

export default async function ProfilePage() {
    const profile = await getMyProfile();

    if (!profile) {
        return (
            <div className="mx-auto max-w-xl p-6">
                <p className="text-red-600">로그인이 필요합니다.</p>
                <Link href="/login" className="mt-4 inline-block text-sm text-zinc-500 hover:text-zinc-900">
                    로그인하러 가기
                </Link>
            </div>
        );
    }

    return (
        <div className="mx-auto max-w-xl p-6">
            <div className="mb-6 flex items-center justify-between">
                <h1 className="text-2xl font-bold">내 정보</h1>
                <Link href="/orders" className="text-sm text-zinc-500 hover:text-zinc-900">
                    주문 내역 보기 →
                </Link>
            </div>
            <ProfileEditor profile={profile} />
        </div>
    );
}
