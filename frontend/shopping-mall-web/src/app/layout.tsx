import type { Metadata } from "next";
import { Geist, Geist_Mono, Gowun_Batang, IBM_Plex_Sans_KR } from "next/font/google";
import "./globals.css";
import Header from "./components/Header";
import HeaderGate from "./components/HeaderGate";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

// 메모의 제품명용 바탕체, 가격 등 작은 글자용 고딕

const gowunBatang = Gowun_Batang({
  variable: "--font-gowun-batang",
  weight: ["400", "700"],
  preload: false,
});

const ibmPlexSansKr = IBM_Plex_Sans_KR({
  variable: "--font-ibm-plex-kr",
  weight: ["400", "500"],
  preload: false,
});

export const metadata: Metadata = {
  title: "감자몰",
  description: "옷을 잡지처럼 읽고 고르는 쇼핑몰",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <body
        className={`${geistSans.variable} ${geistMono.variable} ${gowunBatang.variable} ${ibmPlexSansKr.variable} antialiased`}
      >
        <HeaderGate>
          <Header />
        </HeaderGate>
        {children}
      </body>
    </html>
  );
}
