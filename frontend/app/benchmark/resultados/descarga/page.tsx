"use client";

import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Header from "@/components/Header";
import Footer from "@/components/Footer";
import RequireAuth from "@/components/RequireAuth";
import { documentsApi } from "@/lib/api";
import { CAPACITY_REPORT_KEY, type CapacityReport } from "@/lib/capacityReport";
import { formatTimestampCompact } from "@/lib/format";

type PdfStatus = "loading" | "success" | "retry";

/** Dispara el guardado del Blob como si fuera un <a download> nativo. */
function saveBlob(blob: Blob, fileName: string) {
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = fileName;
  document.body.appendChild(link);
  link.click();
  link.remove();
  URL.revokeObjectURL(url);
}

function GenerandoPdf() {
  const router = useRouter();
  const [status, setStatus] = useState<PdfStatus>("loading");

  const generarYDescargar = useCallback(async () => {
    setStatus("loading");

    const raw = sessionStorage.getItem(CAPACITY_REPORT_KEY);
    if (!raw) {
      router.replace("/benchmark/resultados");
      return;
    }

    try {
      const { document: report } = JSON.parse(raw) as CapacityReport;
      // Nombre con timestamp al momento de generar, no al armar el reporte: cada
      // descarga queda como su propio documento en vez de reemplazar la anterior
      // (POST /documents reemplaza si metadata.name coincide con uno existente).
      const input = {
        ...report,
        metadata: {
          name: `${report.metadata.name}-${formatTimestampCompact(new Date())}`,
        },
      };
      const summary = await documentsApi.generate(input);
      const blob = await documentsApi.downloadBlob(summary.name);
      saveBlob(blob, summary.fileName);
      setStatus("success");
    } catch {
      setStatus("retry");
    }
  }, [router]);

  useEffect(() => {
    generarYDescargar();
    // Solo al montar: reintentar es una accion explicita del usuario, no del efecto.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <main className="relative flex flex-1 items-center justify-center overflow-hidden px-4 py-12 sm:px-8">
      <img
        src="/assets/backgrounds/vector.svg"
        alt=""
        aria-hidden="true"
        className="pointer-events-none absolute -left-[-36px] top-1/2 hidden h-[430px] w-[390px] -translate-y-1/2 opacity-90 md:block"
      />

      <div className="relative mx-auto flex w-full max-w-[500px] flex-col items-center justify-center rounded-lg border border-base-border/60 bg-white px-6 py-12 text-center shadow-sm sm:px-10">
        {status === "loading" && (
          <>
            <div className="h-10 w-10 animate-spin rounded-full border-4 border-base-border border-t-forest" />
            <p className="mt-6 text-sm font-medium text-text-secondary">
              Preparando tu informe ejecutivo...
            </p>
          </>
        )}

        {status === "success" && (
          <>
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-forest text-amber-400">
              <svg className="h-6 w-6 stroke-current" fill="none" viewBox="0 0 24 24" strokeWidth="2.5">
                <path strokeLinecap="round" strokeLinejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
            </div>
            <p className="mt-6 text-base font-semibold text-text-primary">
              Tu informe ejecutivo esta listo.
            </p>
            <button
              onClick={() => router.push("/benchmark/resultados")}
              className="mt-6 rounded-md border border-forest px-5 py-2 text-sm font-semibold text-forest-dark transition-colors hover:bg-base-natural"
            >
              ← Volver
            </button>
          </>
        )}

        {status === "retry" && (
          <>
            <div className="flex h-12 w-12 items-center justify-center rounded-full border border-red-200 text-red-500">
              <span className="text-xl font-medium">!</span>
            </div>
            <p className="mt-6 text-base font-semibold text-text-primary">
              No pudimos generar tu informe.
            </p>
            <p className="mt-2 text-xs text-text-secondary">
              Si NO se descargó, vuelve a{" "}
              <button
                onClick={generarYDescargar}
                className="font-medium text-forest underline hover:opacity-80"
              >
                reintentarlo
              </button>
            </p>
          </>
        )}
      </div>
    </main>
  );
}

export default function GenerandoPdfPage() {
  return (
    <div className="flex min-h-screen flex-col bg-base-natural font-display text-text-primary">
      <Header />

      <RequireAuth>
        <GenerandoPdf />
      </RequireAuth>

      <Footer />
    </div>
  );
}
