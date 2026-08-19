"use client";

import Link from "next/link";
import Footer from "@/components/Footer";
import Header from "@/components/Header";
import RequireAuth from "@/components/RequireAuth";
import { useAuth } from "@/components/AuthProvider";

function BenchmarkGate() {
  const { user } = useAuth();

  return (
    <>

      <main className="relative flex flex-1 items-center justify-center overflow-hidden px-4 py-12 sm:px-8">
        <img
          src="/assets/backgrounds/vector.svg"
          alt=""
          aria-hidden="true"
          className="pointer-events-none absolute -left-[-36px] top-1/2 hidden h-[433px] w-[387px] -translate-y-1/2 opacity-90 md:block"
        />

        <div className="relative mx-auto w-full max-w-[540px] text-center">
          <p className="text-base font-bold uppercase tracking-wider text-gold">
            MATURITY BENCHMARK
          </p>

          <p className="mt-3 text-base">
            Fuiste invitado a evaluar la madurez operativa de tu Data Center.
            Este benchmark de 5 minutos te muestra cómo te comparás con otros
            operadores de tu segmento.
          </p>

          <div className="mt-8 rounded-lg border border-base-border/60 bg-white px-8 py-9 shadow-sm">
            <p className="text-[14px] text-text-secondary">
              Vas a completar el benchmark como
            </p>

            <div className="mx-auto mt-3 max-w-fit rounded border border-text-secondary bg-base-internal px-4 py-2.5 text-[12.45px] text-text-primary">
              {user?.email}
            </div>

            <Link
              href="/benchmark/cuestionario"
              className="mx-auto mt-5 inline-flex h-11 w-full max-w-[280px] items-center justify-center gap-2 rounded bg-forest-dark px-5 text-base font-semibold text-white transition-colors hover:bg-forest focus:outline-none focus:ring-2 focus:ring-forest focus:ring-offset-2"
            >
              Empezar el Benchmark <span aria-hidden="true">↗</span>
            </Link>

            <p className="mt-4 text-xs text-text-secondary">
              4 dimensiones · 8 preguntas · 5 minutos
            </p>
          </div>
        </div>
      </main>
    </>
  );
}

export default function BenchmarkWelcomePage() {
  return (
    <div className="flex min-h-screen flex-col bg-base-natural font-display text-text-primary">
      <Header />

      <RequireAuth>
        <BenchmarkGate />
      </RequireAuth>

      <Footer />
    </div>
  );
}