// Server Components/서버 사이드 fetch용 — 같은 Docker 네트워크 안에서 backend 컨테이너를 호출할 때 사용
export const SERVER_API_URL = process.env.API_INTERNAL_URL ?? "http://localhost:8080/api/v1";

// Client Components(브라우저)용 — 사용자의 브라우저가 직접 호출할 때 사용, 반드시 NEXT_PUBLIC_ 접두사 필요
export const CLIENT_API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api/v1";
