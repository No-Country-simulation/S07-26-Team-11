import Image from "next/image";
import Link from "next/link";

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

export default function Footer() {
  return (
    <footer className="w-full border-t border-base-border bg-white">
      <div className="mx-auto flex w-full max-w-[1120px] flex-col items-center gap-6 px-6 py-8 text-xs text-text-secondary sm:flex-row sm:justify-between sm:px-8">
        <Logo />

        <nav aria-label="Enlaces legales" className="flex items-center gap-7">
          <Link href="/privacidad" className="text-forest-dark">
            Privacidad
          </Link>
          <Link href="/terminos" className="text-forest-dark">
            Términos y Condiciones
          </Link>
          <Link href="/contacto" className="text-forest-dark">
            Contacto
          </Link>
        </nav>

        <p className="text-forest-dark">© 2026 Capacia, Inc.</p>
      </div>
    </footer>
  );
}
