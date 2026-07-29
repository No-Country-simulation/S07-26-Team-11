import Image from "next/image";
import Link from "next/link";

const chartData = [
  { month: "Ene", height: 76, used: 43 },
  { month: "Feb", height: 78, used: 45 },
  { month: "Mar", height: 77, used: 42 },
  { month: "Abr", height: 80, used: 48 },
  { month: "May", height: 82, used: 50 },
  { month: "Jun", height: 81, used: 47 },
  { month: "Jul", height: 84, used: 51 },
  { month: "Ago", height: 86, used: 53 },
  { month: "Sep", height: 85, used: 51 },
  { month: "Oct", height: 88, used: 53 },
  { month: "Nov", height: 89, used: 56 },
  { month: "Dic", height: 90, used: 56, recoverable: true },
];

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

function Arrow() {
  return (
    <svg
      aria-hidden="true"
      viewBox="0 0 20 20"
      className="size-4"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.7"
    >
      <path d="M4 10h12m-4-4 4 4-4 4" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

function Chart() {
  return (
    <div className="mx-auto mt-16 w-full max-w-[720px] rounded-2xl border border-base-border bg-white p-6 shadow-sm sm:p-8">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <h2 className="text-sm font-display text-text-primary">
          Capacidad encendida vs. utilizada
        </h2>

        <div className="flex flex-wrap items-center gap-4 text-xs text-text-secondary font-display">
          <span className="flex items-center gap-1.5">
            <span className="size-2 rounded-sm bg-forest-light" />
            Encendida
          </span>
          <span className="flex items-center gap-1.5">
            <span className="size-2 rounded-sm bg-forest" />
            Utilizada
          </span>
          <span className="flex items-center gap-1.5">
            <span className="size-2 rounded-sm bg-gold" />
            Recuperable
          </span>
        </div>
      </div>

      <div className="mt-8 grid h-[176px] grid-cols-12 items-end gap-1.5 sm:gap-2">
        {chartData.map((item) => (
          <div key={item.month} className="flex h-full min-w-0 flex-col justify-end">
            <div
              className="flex w-full flex-col justify-end overflow-hidden rounded-t bg-forest-light"
              style={{ height: `${item.height}%` }}
              title={`${item.month}: ${item.used}% de capacidad utilizada`}
            >
              <div className="flex-1" />
              {item.recoverable && <div className="h-[43%] bg-gold" />}
              <div
                className="shrink-0 bg-forest"
                style={{ height: `${item.used}%` }}
              />
            </div>
            <span className="mt-2 text-center text-xs text-text-secondary">
              {item.month}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default function Home() {
  return (
    <div className="min-h-screen bg-base-natural text-text-primary">
      <header className="w-full bg-white shadow-sm">
        <div className="mx-auto flex h-16 w-full max-w-[1120px] items-center justify-between px-6 sm:px-8">
          <Logo />

          <Link
            href="/calculadora"
            className="inline-flex min-h-[45px] items-center justify-center gap-2.5 rounded-sm border border-forest px-6 py-3 text-base font-semibold leading-none tracking-normal text-forest-dark hover:bg-forest hover:text-white focus:outline-none focus:ring-2 focus:ring-forest focus:ring-offset-2"
          >
            Calculá tu capacidad
          </Link>
        </div>
      </header>

      <main>
        <section className="px-6 pb-16 pt-16 text-center sm:px-8 sm:pb-24 sm:pt-24">
          <div className="mx-auto max-w-[720px]">
            <p className="inline-flex h-7 items-center gap-2.5 rounded-[100px] bg-forest-soft px-[14px] py-1.5 font-display text-xs font-semibold leading-none tracking-normal text-forest">
              Inteligencia de capacidad para Data Centers
            </p>

            <h1 className="mx-auto mt-8 max-w-[720px] text-[40px] font-bold text-forest-dark sm:text-5xl">
              ¿Cuánta capacidad de tu
              <br className="hidden sm:block" /> Data Center está encendida
              <br className="hidden sm:block" /> pero subutilizada?
            </h1>

            <p className="mx-auto mt-6 max-w-[560px] font-display leading-6 text-text-secondary">
              Medí la brecha entre la energía que consumís y el cómputo que
              realmente entregás. En minutos, sin instalar nada.
            </p>

            <Link
              href="/calculadora"
              className="mt-8 inline-flex h-12 w-full max-w-[277px] items-center justify-center gap-2.5 rounded bg-forest-dark px-6 py-3 text-sm font-semibold text-white shadow-sm hover:bg-forest focus:outline-none focus:ring-2 focus:ring-forest focus:ring-offset-2 focus:ring-offset-base-natural"
            >
              Calculá tu capacidad ahora
              <Arrow />
            </Link>

            <p className="mt-3 text-xs text-text-secondary font-display">
              Gratis · 4 minutos · Resultado inmediato
            </p>
          </div>

          <Chart />

          <p className="mt-12 text-sm text-forest-dark">
            <strong className="text-base font-display text-forest-dark">+2.400 MW</strong>{" "}
            de capacidad analizada en más de 300 instalaciones
          </p>

          <div className="mx-auto mt-16 grid w-full max-w-[880px] gap-6 text-left md:grid-cols-2">
            <article className="rounded-2xl border border-base-border bg-white p-6 transition-all hover:-translate-y-0.5 hover:shadow-md sm:p-8">
              <span className="grid size-10 place-items-center rounded-lg bg-forest-light text-forest-dark">
                <svg
                  aria-hidden="true"
                  viewBox="0 0 24 24"
                  className="size-4"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="1.8"
                >
                  <path
                    d="M6 5h8a3 3 0 0 1 3 3v11H9a3 3 0 0 1-3-3V5Zm3 0v14"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  />
                </svg>
              </span>

              <h2 className="mt-6 text-xl font-semibold text-text-primary">
                Leé el Industry Report
              </h2>
              <p className="mt-2 text-sm leading-6 text-text-secondary">
                Datos de utilización, densidad y eficiencia de la industria en 2026.
                Dónde está parado tu Data Center frente al mercado.
              </p>
              <Link
                href="/reporte-industria"
                className="mt-4 inline-flex items-center gap-1 text-sm font-semibold text-forest hover:underline"
              >
                Descargar el report <span aria-hidden="true">→</span>
              </Link>
            </article>

            <article className="rounded-2xl border border-base-border bg-white p-6 transition-all hover:-translate-y-0.5 hover:shadow-md sm:p-8">
              <span className="grid size-10 place-items-center rounded-lg bg-base-internal text-gold-dark">
                <svg
                  aria-hidden="true"
                  viewBox="0 0 24 24"
                  className="size-4"
                  fill="currentColor"
                >
                  <path d="m12 3 2.1 4.9L19 10l-4.9 2.1L12 17l-2.1-4.9L5 10l4.9-2.1L12 3Z" />
                </svg>
              </span>

              <h2 className="mt-6 text-xl font-semibold text-text-primary">
                Hacé el Maturity Benchmark
              </h2>
              <p className="mt-2 text-sm leading-6 text-text-secondary">
                Evaluá la madurez operativa de tu infraestructura en cuatro
                dimensiones y compará contra pares de tu segmento.
              </p>
              <Link
                href="/benchmark"
                className="mt-4 inline-flex items-center gap-1 text-sm font-semibold text-forest hover:underline"
              >
                Empezar el benchmark <span aria-hidden="true">→</span>
              </Link>
            </article>
          </div>
        </section>
      </main>

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
    </div>
  );
}
