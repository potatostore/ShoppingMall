import type { Metadata } from "next";
import { Geist, Geist_Mono, Gowun_Batang, IBM_Plex_Sans_KR, Nanum_Pen_Script, Song_Myung } from "next/font/google";
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

// 잡지 제목용 옛 인쇄체, 본문용 바탕체, 작은 안내 글자용 고딕
const songMyung = Song_Myung({
  variable: "--font-song-myung",
  weight: "400",
});

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

// 세로 화면의 포스트잇 메모용 손글씨
const nanumPen = Nanum_Pen_Script({
  variable: "--font-nanum-pen",
  weight: "400",
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
        className={`${geistSans.variable} ${geistMono.variable} ${songMyung.variable} ${gowunBatang.variable} ${ibmPlexSansKr.variable} ${nanumPen.variable} antialiased`}
      >
        <HeaderGate>
          <Header />
        </HeaderGate>
        {children}
      </body>
    </html>
  );
}
