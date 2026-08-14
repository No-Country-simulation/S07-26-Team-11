"use client";

import { useState } from "react";
import Header from "@/components/Header";
import Footer from "@/components/Footer";

type PdfStatus = "loading" | "success" | "retry";

export default function GenerandoPdfPage() {
  // Estado inicial en 'loading'. Podés cambiarlo a 'success' o 'retry' para probarlos.
  const [status, setStatus] = useState<PdfStatus>("loading");

  const handleRetry = () => {
    // Lógica para forzar la descarga manual del PDF cuando el usuario hace clic en "reintentarlo"
    console.log("Reintentando descarga manual...");
  };

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
          
          {/* 1. ESTADO: LOADING */}
          {status === "loading" && (
            <>
              <div className="h-10 w-10 animate-spin rounded-full border-4 border-base-border border-t-forest" />
              <p className="mt-6 text-sm font-medium text-text-secondary">
                Preparando tu informe ejecutivo...
              </p>
            </>
          )}

          {/* 2. ESTADO: SUCCESS */}
          {status === "success" && (
            <>
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-forest text-amber-400">
                <svg
                  className="h-6 w-6 stroke-current"
                  fill="none"
                  viewBox="0 0 24 24"
                  strokeWidth="2.5"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" d="M4.5 12.75l6 6 9-13.5" />
                </svg>
              </div>
              <p className="mt-6 text-base font-semibold text-text-primary">
                Tu informe ejecutivo esta listo.
              </p>
            </>
          )}

          {/* 3. ESTADO: RETRY / FALLBACK */}
          {status === "retry" && (
            <>
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-forest text-amber-400">
                <svg
                  className="h-6 w-6 stroke-current"
                  fill="none"
                  viewBox="0 0 24 24"
                  strokeWidth="2.5"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" d="M4.5 12.75l6 6 9-13.5" />
                </svg>
              </div>
              <p className="mt-6 text-base font-semibold text-text-primary">
                Tu informe ejecutivo esta listo.
              </p>
              <p className="mt-2 text-xs text-text-secondary">
                Si NO se descargó, vuelve a{" "}
                <button
                  onClick={handleRetry}
                  className="font-medium text-forest underline hover:opacity-80"
                >
                  reintentarlo
                </button>
              </p>
            </>
          )}

        </div>
      </main>

      <Footer />
    </div>
  );
}