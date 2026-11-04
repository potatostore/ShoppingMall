import Link from "next/link";
import { COMPANY } from "../components/magazine/content";

const HELP_LINKS = [
    { href: "/orders", label: "주문 내역 보기" },
    { href: "/find-id", label: "아이디 찾기" },
    { href: "/find-password", label: "비밀번호 찾기" },
];

export default function SupportPage() {
    const contacts = COMPANY.contacts.filter((contact) => contact.label !== "회사명");

    return (
        <div className="mx-auto w-full max-w-md px-6 py-20">
            <h1 className="text-2xl font-bold tracking-tight text-zinc-900">고객센터</h1>

            <dl className="mt-10 border-t border-zinc-200 text-sm">
                {contacts.map((contact) => (
                    <div key={contact.label} className="flex justify-between gap-6 border-b border-zinc-200 py-4">
                        <dt className="text-zinc-500">{contact.label}</dt>
                        <dd className="text-right text-zinc-900">{contact.value}</dd>
                    </div>
                ))}
            </dl>

            <ul className="mt-10 flex flex-col gap-3 text-sm">
                {HELP_LINKS.map((link) => (
                    <li key={link.href}>
                        <Link href={link.href} className="text-zinc-900 underline underline-offset-4">
                            {link.label}
                        </Link>
                    </li>
                ))}
            </ul>
        </div>
    );
}
