import Link from "next/link";
import { BACK_COVER, BACK_PAGE, COVER, LOOKS, type Look } from "./content";
import Photo from "./Photo";

// 모델을 오른쪽으로 옮겨 왼쪽 빈 공간에 옷 정보를 둔다
const MODEL_CENTER_X = 64;

function LookRow({ look }: { look: Look }) {
    return (
        <div className="relative min-h-0 min-w-0 overflow-hidden">
            <h2 className="sr-only">{look.title}</h2>
            <Photo src={look.image} alt={look.alt} centerX={MODEL_CENTER_X}>
                {look.items.map((item, index) => (
                    <Link
                        key={item.id}
                        href={`/products/${item.productId}`}
                        aria-label={`${item.name} 상세 보기`}
                        style={{ left: `${item.x}%`, top: `${item.y}%` }}
                        className="absolute z-10 -ml-4 -mt-4 flex h-8 w-8 items-center justify-center"
                    >
                        <span className="flex h-4 w-4 items-center justify-center rounded-full bg-ink font-label text-[9px] text-paper ring-1 ring-paper">
                            {index + 1}
                        </span>
                    </Link>
                ))}
            </Photo>

            <div className="absolute inset-y-0 left-0 z-10 flex w-[42%] max-w-[17rem] flex-col justify-center gap-[clamp(0.5rem,2dvh,1.25rem)] pb-9 pl-4 pt-3 text-ink">
                {look.items.map((item, index) => (
                    <Link key={item.id} href={`/products/${item.productId}`} className="memo flex gap-1.5">
                        <span className="font-label text-[11px] leading-5 text-ink-soft">{index + 1}</span>
                        <span className="min-w-0">
                            <span className="block font-batang text-[13px] font-bold leading-5 sm:text-base sm:leading-6">{item.name}</span>
                            <span className="memo-detail block font-label text-[11px] leading-4 text-ink-soft sm:text-xs sm:leading-5">{item.description}</span>
                            <span className="block font-label text-[11px] leading-4 sm:text-xs sm:leading-5">{item.price.toLocaleString("ko-KR")}원</span>
                        </span>
                    </Link>
                ))}
                <p className="memo-intro font-batang text-sm leading-[1.7]">{look.intro}</p>
            </div>
        </div>
    );
}

function CoverSheet() {
    return (
        <section className="absolute inset-0">
            <Photo src={COVER.image} alt={COVER.alt} />
            <div className="pointer-events-none absolute inset-0 flex flex-col justify-between p-6 pt-14 text-paper">
                <h1 className="font-display text-[clamp(4rem,min(24vw,30dvh),11rem)] leading-none">감자몰</h1>
                <div className="flex items-end justify-between gap-6 pr-8">
                    <p className="font-batang text-xl leading-snug">{COVER.headline}</p>
                    <p className="shrink-0 pb-1 font-label text-xs">{COVER.issue}, {COVER.season}</p>
                </div>
            </div>
        </section>
    );
}

function BackSheet() {
    return (
        <section className="absolute inset-0 flex flex-col justify-between bg-paper p-8 pb-16 pt-16 text-ink">
            <p className="max-w-[23rem] font-batang text-xl leading-[1.75]">{BACK_COVER.purpose}</p>
            <div className="font-label text-xs leading-6 text-ink-soft">
                <p className="mb-5 max-w-[20rem]">
                    {BACK_COVER.guideSheet} {BACK_COVER.note}
                </p>
                {BACK_COVER.contacts.map((contact) => (
                    <p key={contact.label}>
                        <span className="inline-block w-16">{contact.label}</span>
                        <span className="text-ink">{contact.value}</span>
                    </p>
                ))}
            </div>
        </section>
    );
}

export default function Sheet({ page }: { page: number }) {
    if (page === 0) return <CoverSheet />;
    if (page === BACK_PAGE) return <BackSheet />;

    const looks = LOOKS.slice(page - 1, page + 1);
    return (
        <div className="absolute inset-0 grid grid-rows-2 gap-px bg-fold landscape:grid-cols-2 landscape:grid-rows-1">
            {looks.map((look) => (
                <LookRow key={look.image} look={look} />
            ))}
        </div>
    );
}
