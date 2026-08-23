'use client';

import { useState } from "react";
import { CLIENT_API_URL } from "@/lib/api";

const userApiUrl = `${CLIENT_API_URL}/users`;

type UserProfile = {
    userId: number;
    name: string;
    email: string;
    role: string;
    phoneNumber: string;
    birthday: string;
};

type EditableField = "name" | "email" | "phoneNumber" | "birthday";

export default function ProfileEditor({ profile }: { profile: UserProfile }) {
    const [editing, setEditing] = useState(false);
    const [saving, setSaving] = useState(false);
    const [current, setCurrent] = useState(profile);
    const [form, setForm] = useState(profile);

    const handleFieldChange = (field: EditableField, value: string) => {
        setForm((prev) => ({ ...prev, [field]: value }));
    };

    const handleClick = async () => {
        if (!editing) {
            setForm(current);
            setEditing(true);
            return;
        }

        setSaving(true);
        try {
            const response = await fetch(`${userApiUrl}/me`, {
                method: "PATCH",
                credentials: "include",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    name: form.name,
                    email: form.email,
                    phoneNumber: form.phoneNumber,
                    birthday: form.birthday,
                }),
            });

            if (!response.ok) {
                alert("프로필 변경에 실패했습니다.");
                return;
            }

            const { data } = await response.json();
            setCurrent(data);
            setEditing(false);
        } catch {
            alert("통신 오류가 발생했습니다.");
        } finally {
            setSaving(false);
        }
    };

    const editableRows: [EditableField, string][] = [
        ["name", "이름"],
        ["email", "이메일"],
        ["phoneNumber", "전화번호"],
        ["birthday", "생년월일"],
    ];

    return (
        <div className="rounded border border-zinc-200 p-6">
            <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-zinc-900 text-3xl text-white">
                👤
            </div>

            <dl className="divide-y divide-zinc-100 text-sm">
                {editableRows.map(([field, label]) => (
                    <div key={field} className="flex items-center justify-between py-3">
                        <dt className="text-zinc-500">{label}</dt>
                        {editing ? (
                            <input
                                type={field === "birthday" ? "date" : "text"}
                                value={form[field]}
                                onChange={(e) => handleFieldChange(field, e.target.value)}
                                className="w-48 rounded border border-zinc-300 px-2 py-1 text-right"
                            />
                        ) : (
                            <dd className="font-medium text-zinc-900">{current[field]}</dd>
                        )}
                    </div>
                ))}

                <div className="flex items-center justify-between py-3">
                    <dt className="text-zinc-500">권한</dt>
                    <dd className="font-medium text-zinc-900">{current.role}</dd>
                </div>
            </dl>

            <button
                onClick={handleClick}
                disabled={saving}
                className="mt-6 w-full rounded bg-zinc-900 py-2 text-sm text-white disabled:opacity-50"
            >
                {saving ? "저장 중..." : "프로필 변경하기"}
            </button>
        </div>
    );
}
