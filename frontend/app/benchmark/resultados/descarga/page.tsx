"use client";

import Header from "@/components/Header";
import Footer from "@/components/Footer";

export default function GenerandoPdfPage() {
  return (
    <div className="flex min-h-screen flex-col bg-base-natural font-display text-text-primary">
      <Header />

      <main className="relative flex flex-1 items-center justify-center overflow-hidden px-4 py-12 sm:px-8">
        <img
          src="/assets/backgrounds/vector.svg"
          alt=""
          aria-hidden="true"
          className="pointer-events-none absolute -left-[-36px] top-1/2 hidden h-[430px] w-[390px] -translate-y-1/2 opacity-90 md:block"
        />

        <div className="relative mx-auto flex w-full max-w-[500px] flex-col items-center justify-center rounded-lg border border-base-border/60 bg-white px-6 py-12 text-center shadow-sm sm:px-10">
          <div className="h-10 w-10 animate-spin rounded-full border-4 border-base-border border-t-forest" />

          <p className="mt-6 text-sm font-medium text-text-secondary">
            Preparando tu informe ejecutivo...
          </p>
        </div>
      </main>

      <Footer />
    </div>
  );
}