"use client";

import Link from "next/link";
import Header from "@/components/Header";
import Footer from "@/components/Footer";

export default function ReunionConfirmacionPage() {
  const fechaConfirmada = "Jue 23 jul a las 09:00";
  const userEmail = "gerencia@northbridge.com";
  const nivelMadurez = "Gestionado";
  const costoOportunidad = "US$ 48.200";

  return (
    <div className="flex min-h-screen flex-col bg-base-natural font-display text-text-primary">
      <Header subtitle="/ Agendar una reunión" />

      <div className="w-full bg-forest-light px-4 py-4 text-center text-[14px] text-text-secondary">
        Reunión para revisar el resultado de{" "}
        <span className="font-bold text-text-primary">{userEmail}</span> — Nivel:{" "}
        <span className="font-bold text-text-primary">{nivelMadurez}</span>,{" "}
        <span className="text-gold">{costoOportunidad}</span> anuales en oportunidad.
      </div>

      <main className="relative flex flex-1 items-center justify-center overflow-hidden px-4 py-12 sm:px-8">
        <img
          src="/assets/backgrounds/vector.svg"
          alt=""
          aria-hidden="true"
          className="pointer-events-none absolute -left-[-36px] top-1/2 hidden h-[430px] w-[390px] -translate-y-1/2 opacity-90 md:block"
        />

        <div className="relative mx-auto w-full max-w-[592px] rounded-lg border border-base-border/60 bg-white px-6 py-10 text-center shadow-sm sm:px-10">
          <div className="mx-auto flex h-11 w-11 items-center justify-center rounded-full bg-forest text-white">
            <svg
              className="h-6 w-6"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2.5}
                d="M5 13l4 4L19 7"
              />
            </svg>
          </div>

          <h2 className="mt-6 text-xl font-extrabold text-text-primary sm:text-lg">
            Reunión confirmada para el {fechaConfirmada}
          </h2>

          <p className="mt-3 text-base leading-relaxed text-text-secondary">
            Te enviamos la confirmación a {userEmail}.
            <br />
            Si necesitás reprogramar, vas a poder hacerlo desde ese
            <br />
            mismo email.
          </p>

          <div className="mt-4 flex flex-col items-center gap-4">
            <Link
              href="/benchmark/resultados"
              className="inline-flex h-10 w-full max-w-[200px] items-center justify-center rounded border border-forest px-4 text-base font-semibold text-forest-dark"
            >
              Volver a mi resultado
            </Link>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
}