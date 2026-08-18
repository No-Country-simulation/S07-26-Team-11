"use client";
import { useEffect, useState } from "react";
import Footer from "@/components/Footer";
import ChartPosicion from "@/components/ChartPosicion";
import Recomendaciones from "@/components/Recomendaciones";
import KpiCards, { type KpiCardData } from "@/components/KpiCards";
import Header from "@/components/Header";
import { useRouter } from "next/navigation";
import RequireAuth from "@/components/RequireAuth";
import { useAuth } from "@/components/AuthProvider";
import { BENCHMARK_ANSWERS_KEY } from "@/lib/benchmarkStorage";
import { isComplete, type BenchmarkAnswers } from "@/lib/benchmarkScoring";
import { buildCapacityReport, CAPACITY_REPORT_KEY, type CapacityReport } from "@/lib/capacityReport";

type Estado = "cargando" | "listo" | "error" | "incompleto";

function Resultados() {
  const router = useRouter();
  const { user } = useAuth();
  const [estado, setEstado] = useState<Estado>("cargando");
  const [reporte, setReporte] = useState<CapacityReport | null>(null);

  useEffect(() => {
    const raw = sessionStorage.getItem(BENCHMARK_ANSWERS_KEY);
    const answers = raw ? (JSON.parse(raw) as BenchmarkAnswers) : {};

    if (!isComplete(answers)) {
      setEstado("incompleto");
      return;
    }

    // El nombre de la empresa no se pide en este flujo (no hay paso de captura de lead
    // antes del benchmark, a diferencia del Figma original): se usa el email como
    // identificador legible en el titulo del informe.
    buildCapacityReport(answers, user?.email ?? "")
      .then((result) => {
        sessionStorage.setItem(CAPACITY_REPORT_KEY, JSON.stringify(result));
        setReporte(result);
        setEstado("listo");
      })
      .catch(() => setEstado("error"));
  }, [user]);

  if (estado === "incompleto") {
    return (
      <main className="flex flex-1 items-center justify-center px-4 py-20">
        <div className="mx-auto flex w-full max-w-[500px] flex-col items-center rounded-lg border border-base-border/60 bg-white px-6 py-12 text-center shadow-sm">
          <h2 className="text-lg font-bold text-text-primary">Falta un paso para tu comparación completa</h2>
          <p className="mt-2 text-xs text-text-secondary">
            No completaste el Benchmark. Completalo para ver tu comparación completa.
          </p>
          <button
            onClick={() => router.push("/benchmark")}
            className="mt-6 inline-flex h-11 items-center justify-center rounded-md bg-forest px-6 text-sm font-medium text-white hover:bg-forest/90"
          >
            Completar el Benchmark ↗
          </button>
        </div>
      </main>
    );
  }

  if (estado === "error") {
    return (
      <main className="flex flex-1 items-center justify-center px-4 py-20">
        <div className="mx-auto flex w-full max-w-[500px] flex-col items-center rounded-lg border border-base-border/60 bg-white px-6 py-12 text-center shadow-sm">
          <h2 className="text-lg font-bold text-text-primary">No pudimos cargar esto</h2>
          <p className="mt-2 text-xs text-text-secondary">
            Puede ser un problema temporal de conexión. Probá de nuevo en unos segundos.
          </p>
          <button
            onClick={() => window.location.reload()}
            className="mt-6 inline-flex h-11 items-center justify-center rounded-md bg-forest px-6 text-sm font-medium text-white hover:bg-forest/90"
          >
            Reintentar ↗
          </button>
        </div>
      </main>
    );
  }

  if (estado === "cargando" || !reporte) {
    return (
      <main className="flex flex-1 items-center justify-center px-4 py-20">
        <div className="h-10 w-10 animate-spin rounded-full border-4 border-base-border border-t-forest" />
      </main>
    );
  }

  const kpis: KpiCardData[] = [
    { label: "kW subutilizados", value: reporte.document.kwUnderutilized, isGold: true },
    { label: "Costo anual", value: reporte.document.annualCost, isGold: true },
    { label: "% de utilización", value: reporte.document.utilizationPercent, isGold: false },
    { label: "Costo por rack", value: reporte.document.costPerRack, isGold: false },
  ];

  return (
    <main className="flex-grow">
      <section className="bg-forest-dark w-full py-16 flex flex-col items-center text-center px-4 relative z-0">
        <p className="text-white/80 uppercase font-semibold mb-3">Tu nivel de madurez</p>
        <h1 className="text-gold-dark text-[52px] sm:text-5xl font-extrabold mb-2">
          {reporte.maturityLevel}
        </h1>
        <p className="text-white/90 text-sm mb-6">Score {reporte.globalScore}/100</p>

        <div className="w-48 h-px bg-white/20 mb-6"></div>
        <p className="text-white/80 text-[14px] mb-1">Costo anual desperdiciado</p>
        <p className="text-white text-[28px] sm:text-3xl font-bold">
          {reporte.document.annualCost}
        </p>
      </section>

      <div className="px-4 py-20">
        <KpiCards data={kpis} />
        <ChartPosicion
          data={reporte.document.industryScores.map((item) => ({
            label: item.label,
            score: item.value,
            own: item.own,
          }))}
        />
        <Recomendaciones items={reporte.document.recommendations} />

        <div className="max-w-3xl mx-auto flex flex-col sm:flex-row gap-4 mt-8">
          <button
            className="flex-1 bg-white border border-forest text-forest-dark font-semibold text-base py-3 rounded-md hover:bg-base-natural transition-colors"
            onClick={() => router.push("/benchmark/resultados/descarga")}
          >
            Descargar informe PDF
          </button>
          <button
            onClick={() => router.push("/benchmark/reunion")}
            className="flex-1 bg-gold-dark text-white font-medium text-sm py-3 rounded-md hover:opacity-90 transition-opacity"
          >
            Agendar una reunión
          </button>
        </div>
      </div>
    </main>
  );
}

export default function BenchmarkResultadosPage() {
  return (
    <div className="min-h-screen bg-base-natural flex flex-col font-display">
      <Header subtitle="/ Resultados" />

      <RequireAuth>
        <Resultados />
      </RequireAuth>

      <Footer />
    </div>
  );
}
