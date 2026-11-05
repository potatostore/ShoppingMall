import { cookies } from "next/headers";
import Magazine from "./components/magazine/Magazine";

export default async function Home() {
  const cookieStore = await cookies();

  return <Magazine isLoggedIn={cookieStore.has("accessToken")} />;
}
