'use client';

import { usePathname } from "next/navigation";
import type { ReactNode } from "react";

// 메인(잡지)은 화면 전체를 쓰므로 공통 헤더를 숨긴다
export default function HeaderGate({ children }: { children: ReactNode }) {
    return usePathname() === "/" ? null : children;
}
