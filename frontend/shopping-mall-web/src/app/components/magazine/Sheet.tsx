import Link from "next/link";
import { BACK_COVER, BACK_PAGE, COVER, LOOKS, type Look } from "./content";
import Photo from "./Photo";

// 포스트잇을 손으로 붙인 듯 조금씩 기울인다
const NOTE_TILT = ["-1.4deg", "1deg", "-0.7deg", "1.5deg"];

function LookRow({ look, rowIndex }: { look: Look; rowIndex: number }) {
    return (
        <div className="grid min-h-0 grid-cols-[minmax(0,1.1fr)_minmax(0,1fr)] bg-paper">
            <div className="relative min-h-0">
                <Photo
                    src={look.image}
                    alt={look.alt}
                    overlay={
                        <h2 className="pointer-events-none absolute left-2 top-2 font-display text-[clamp(1rem,6.2cqh,2.5rem)] leading-[1.15] text-ink [writing-mode:vertical-rl]">
                            {look.title}
                        </h2>
                    }
                >
                    {look.items.map((item, index) => (
                        <Link
                            key={item.id}
                            href={`/products/${item.productId}`}
                            aria-label={`${item.name} 상세 보기`}
                            style={{ left: `${item.x}%`, top: `${item.y}%` }}
                            className="absolute z-10 -ml-4 -mt-4 flex h-8 w-8 items-center justify-center"
                        >
                            <span className="flex h-[18px] w-[18px] items-center justify-center rounded-full bg-ink font-label text-[10px] text-paper ring-1 ring-paper">
                                {index + 1}
                            </span>
                        </Link>
                    ))}
                </Photo>
            </div>

            <div className={`z-10 -ml-3 flex min-h-0 flex-col justify-evenly pr-2 ${rowIndex === 0 ? "pb-1 pt-10" : "pb-12 pt-1 landscape:pt-10"}`}>
                {look.items.map((item, index) => (
                    <Link
                        key={item.id}
                        href={`/products/${item.productId}`}
                        style={{ rotate: NOTE_TILT[index % NOTE_TILT.length] }}
                        className={`note block w-[94%] max-w-[17rem] bg-note px-2.5 py-1.5 font-pen leading-tight text-ink ${index % 2 === 0 ? "self-start" : "self-end"}`}
                    >
                        <span className="block text-[clamp(1.05rem,2.5dvh,1.6rem)]">
                            {index + 1}. {item.name}
                        </span>
                        <span className="note-detail block text-[clamp(0.95rem,2.1dvh,1.3rem)] opacity-75">{item.description}</span>
                        <span className="block text-[clamp(0.95rem,2.1dvh,1.3rem)]">{item.price.toLocaleString("ko-KR")}원</span>
                    </Link>
                ))}
                <p className="note-intro max-w-[17rem] px-2 font-batang text-sm leading-[1.7] text-ink">{look.intro}</p>
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
            {looks.map((look, index) => (
                <LookRow key={look.image} look={look} rowIndex={index} />
            ))}
        </div>
    );
}
