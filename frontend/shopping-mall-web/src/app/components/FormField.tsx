import type { InputHTMLAttributes } from "react";

type FormFieldProps = InputHTMLAttributes<HTMLInputElement> & {
    id: string;
    label: string;
};

export default function FormField({ id, label, ...inputProps }: FormFieldProps) {
    return (
        <div className="flex flex-col gap-2">
            <label htmlFor={id} className="text-sm font-medium text-zinc-900">
                {label}
            </label>
            <input
                id={id}
                className="h-12 rounded-md border border-zinc-300 bg-white px-4 text-sm text-zinc-900 transition placeholder:text-zinc-400 focus:border-zinc-900 focus:outline-none"
                {...inputProps}
            />
        </div>
    );
}
