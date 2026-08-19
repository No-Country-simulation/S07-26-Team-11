"use client";

import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import Header from "@/components/Header";
import Footer from "@/components/Footer";
import RequireAuth from "@/components/RequireAuth";
import { documentsApi } from "@/lib/api";
import { CAPACITY_REPORT_KEY, type CapacityReport } from "@/lib/capacityReport";
import { formatTimestampCompact } from "@/lib/format";

type PdfStatus = "loading" | "success" | "retry";

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
  }, []);

  return (
    <main className="relative flex flex-1 items-center justify-center overflow-hidden px-4 py-12 sm:px-8">
      <img
        src="/assets/backgrounds/vector.svg"
        alt=""
        aria-hidden="true"
        className="pointer-events-none absolute -left-[-36px] top-1/2 hidden h-[430px] w-[390px] -translate-y-1/2 opacity-90 md:block select-none"
      />

      <div className="relative mx-auto flex w-full max-w-[480px] flex-col items-center justify-center rounded-2xl border border-neutral-200/60 bg-white px-8 py-14 text-center shadow-sm">
        {status === "loading" && (
          <div className="flex flex-col items-center py-4">
            <div className="h-12 w-12 animate-spin rounded-full border-4 border-neutral-200 border-t-neutral-600" />
            <p className="mt-8 text-sm font-medium text-neutral-600">
              Preparando tu informe ejecutivo...
            </p>
          </div>
        )}

        {status === "success" && (
          <div className="flex flex-col items-center">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-[#0F291E]">
              <svg
                className="h-8 w-8 text-[#C59B27]"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                strokeWidth="3"
              >
                <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
              </svg>
            </div>

            <h2 className="mt-6 text-xl font-bold tracking-tight text-neutral-900">
              Tu informe ejecutivo esta listo.
            </h2>

            <p className="mt-2 text-xs text-neutral-500">
              Si NO se descargó, vuelve a{" "}
              <button
                type="button"
                onClick={generarYDescargar}
                className="font-medium text-[#1E3A2F] hover:opacity-80 transition-opacity"
              >
                reintentarlo
              </button>
            </p>

            <Link
              href="/benchmark/resultados"
              className="mt-2 text-xs font-semibold text-[#1E3A2F] hover:underline"
            >
              Volver a tus resultados ↗
            </Link>
          </div>
        )}

        {status === "retry" && (
          <div className="flex flex-col items-center">
            <div className="flex h-12 w-12 items-center justify-center rounded-full border border-red-200 text-red-500">
              <span className="text-xl font-medium">!</span>
            </div>

            <h2 className="mt-5 text-base font-bold text-neutral-900">
              No pudimos cargar esto
            </h2>

            <p className="mt-2 text-xs text-neutral-500 max-w-[280px]">
              Puede ser un problema temporal de conexión. Probá de nuevo en unos segundos.
            </p>

            <button
              type="button"
              onClick={generarYDescargar}
              className="mt-6 inline-flex items-center gap-1.5 rounded-lg bg-[#0F291E] px-6 py-2.5 text-xs font-medium text-white transition hover:bg-[#183327]"
            >
              Reintentar ↗
            </button>

            <Link
              href="/benchmark/resultados"
              className="mt-4 text-xs font-semibold text-[#1E3A2F] hover:underline"
            >
              Volver a tus resultados ↗
            </Link>
          </div>
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
