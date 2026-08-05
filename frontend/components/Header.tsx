import Image from "next/image";
import Link from "next/link";
import type { ReactNode } from "react";

type HeaderProps = {
  subtitle?: ReactNode;
  children?: ReactNode;
};

function Logo() {
  return (
    <Link
      href="/"
      aria-label="Ir al inicio"
      className="inline-flex shrink-0 items-center"
    >
      <Image
        src="/assets/brand/logotipo.svg"
        alt="Capacia"
        width={407}
        height={108}
        priority
        className="h-auto w-[102px]"
      />
    </Link>
  );
}

export default function Header({ subtitle, children }: HeaderProps) {
  return (
    <header className="w-full bg-white shadow-sm">
      <div className="mx-auto flex h-16 w-full max-w-[1120px] items-center justify-between px-6 sm:px-8">
        <div className="flex items-center gap-3">
          <Logo />
          {subtitle && (
            <div className="flex items-center pl-3 text-text-secondary text-sm font-medium">
              {subtitle}
            </div>
          )}
        </div>
        {children}
      </div>
    </header>
  );
}
