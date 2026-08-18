"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import Sidebar from "@/components/Sidebar";

export default function LeadDetailPage() {
  const params = useParams();
  const id = params.id as string;

  const lead = {
    email: "gerencia@northbridge.com",
    status: "Parcial",
    source: "Outreach",
    calculatorResult: {
      subutilizedKw: "142 kW",
      annualCost: "US$ 48.200",
      costPerRack: "US$ 1.004",
    },
    activityLogs: [
      { date: "29 jul 2026", description: "Completó Calculator" },
      { date: "29 jul 2026", description: "Vio Results parciales" },
      { date: "28 jul 2026", description: "Abrió el email de seguimiento de Outreach" },
    ],
  };

  return (
    <div className="relative flex min-h-screen bg-[#F7F7F4] text-[#1E1E1E]">
      <Sidebar />

      <main className="relative flex-1 overflow-y-auto px-6 py-8 sm:px-10 lg:py-10">
        <img
          src="/assets/backgrounds/vector.svg"
          alt=""
          aria-hidden="true"
          className="pointer-events-none fixed -bottom-16 -right-16 z-0 hidden w-[420px] opacity-30 select-none lg:block"
        />

        <div className="relative z-10 w-full space-y-8">
          {/* Breadcrumb */}
          <nav className="flex items-center gap-1.5 text-xs text-neutral-500">
            <Link href="/admin/leads" className="hover:underline">Leads</Link>
            <span>/</span>
            <span className="text-neutral-800 font-medium">{lead.email}</span>
          </nav>

          {/* Header del lead */}
          <div>
            <div className="flex flex-wrap items-center gap-3">
              <h1 className="text-2xl font-bold tracking-tight text-neutral-900 sm:text-3xl">
                {lead.email}
              </h1>
              <span
                className={`inline-block rounded-full px-3 py-0.5 text-xs font-medium ${
                  lead.status === "Completado"
                    ? "bg-[#DDECE4] text-[#1E3A2F]"
                    : "bg-[#FDF3D8] text-[#8C6B1C]"
                }`}
              >
                {lead.status}
              </span>
            </div>
            <p className="mt-1 text-xs text-neutral-500">
              Origen: {lead.source}
            </p>
          </div>

          {/* 1. Resultado de Calculator */}
          <div className="space-y-3">
            <h2 className="text-lg font-bold text-neutral-900">
              Resultado de Calculator
            </h2>
            <div className="grid w-full grid-cols-1 gap-4 sm:grid-cols-3">
              <div className="rounded-xl border border-neutral-200/60 bg-white p-6 shadow-sm">
                <p className="text-xs font-medium text-neutral-500">kW subutilizados</p>
                <p className="mt-3 text-2xl font-bold text-neutral-900">{lead.calculatorResult.subutilizedKw}</p>
              </div>
              <div className="rounded-xl border border-neutral-200/60 bg-white p-6 shadow-sm">
                <p className="text-xs font-medium text-neutral-500">Costo anual</p>
                <p className="mt-3 text-2xl font-bold text-neutral-900">{lead.calculatorResult.annualCost}</p>
              </div>
              <div className="rounded-xl border border-neutral-200/60 bg-white p-6 shadow-sm">
                <p className="text-xs font-medium text-neutral-500">Costo por rack</p>
                <p className="mt-3 text-2xl font-bold text-neutral-900">{lead.calculatorResult.costPerRack}</p>
              </div>
            </div>
          </div>

          {/* 2. Resultado de Benchmark (Estado vacío / en progreso) */}
          <div className="space-y-3">
            <h2 className="text-lg font-bold text-neutral-900">
              Resultado de Benchmark
            </h2>
            <div className="w-full rounded-xl border border-neutral-200/60 bg-white p-6 text-center text-xs font-medium text-neutral-500 shadow-sm">
              Este lead todavía no completó el Benchmark.
            </div>
          </div>

          {/* 3. Timeline de Actividad del Lead */}
          <div className="space-y-3">
            <h2 className="text-lg font-bold text-neutral-900">
              Historial de actividad
            </h2>
            <div className="w-full rounded-xl border border-neutral-200/60 bg-white p-6 shadow-sm">
              <div className="divide-y divide-neutral-100 text-xs">
                {lead.activityLogs.map((log, idx) => (
                  <div key={idx} className="flex items-center justify-between py-3 first:pt-0 last:pb-0">
                    <span className="font-semibold text-neutral-800">{log.date}</span>
                    <span className="text-neutral-600">{log.description}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Botón de Acción Principal */}
          <div className="flex justify-end pb-8">
            <button
              onClick={() => console.log("Marcar lead como contactado")}
              className="inline-flex items-center gap-2 rounded-lg bg-[#0F291E] px-6 py-3 text-xs font-semibold text-white shadow-sm transition hover:bg-[#183327]"
            >
              Marcar como contactado
              <svg className="h-4 w-4 stroke-current" fill="none" viewBox="0 0 24 24" strokeWidth="2.5">
                <path strokeLinecap="round" strokeLinejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
            </button>
          </div>
        </div>
      </main>
    </div>
  );
}