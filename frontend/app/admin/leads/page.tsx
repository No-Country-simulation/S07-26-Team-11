"use client";

import { useState } from "react";
import Link from "next/link";
import Sidebar from "@/components/Sidebar";

export const mockLeads = [
  { id: "1", email: "gerencia@northbridge.com", date: "29 jul 2026", status: "Parcial", source: "Landing" },
  { id: "2", email: "ops@velarion.io", date: "28 jul 2026", status: "Completado", source: "Outreach" },
  { id: "3", email: "it@metrocore.com", date: "25 jul 2026", status: "Parcial", source: "Landing" },
  { id: "4", email: "infra@haldis.net", date: "24 jul 2026", status: "Completado", source: "Landing" },
  { id: "5", email: "ops@cordillerahost.com", date: "23 jul 2026", status: "Parcial", source: "Landing" },
  { id: "6", email: "it@nimbustack.io", date: "19 jul 2026", status: "Parcial", source: "Outreach" },
  { id: "7", email: "infraestructura@vaultrix.net", date: "18 jul 2026", status: "Completado", source: "Outreach" },
  { id: "8", email: "gerencia@parallaxdc.com", date: "18 jul 2026", status: "Parcial", source: "Outreach" },
  { id: "9", email: "facilities@ironmeshcolo.com", date: "15 jul 2026", status: "Completado", source: "Outreach" },
  { id: "10", email: "contacto@quorumracks.io", date: "14 jul 2026", status: "Completado", source: "Landing" },
];

export default function LeadsPage() {
  const [activeFilter, setActiveFilter] = useState("Todos");
  const [searchTerm, setSearchTerm] = useState("");

  const filteredLeads = mockLeads.filter((lead) => {
    const matchesFilter =
      activeFilter === "Todos" ||
      (activeFilter === "Parcial" && lead.status === "Parcial") ||
      (activeFilter === "Completo" && lead.status === "Completado");

    const matchesSearch = lead.email.toLowerCase().includes(searchTerm.toLowerCase());
    return matchesFilter && matchesSearch;
  });

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
          <h1 className="text-2xl font-bold tracking-tight text-neutral-900 sm:text-3xl">
            Leads
          </h1>

          {/* Barra de Filtros y Búsqueda */}
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div className="flex flex-wrap items-center gap-3">
              {/* Segmented control */}
              <div className="inline-flex rounded-lg bg-[#EAEAE6] p-1 text-xs font-medium">
                {["Todos", "Parcial", "Completo"].map((tab) => (
                  <button
                    key={tab}
                    onClick={() => setActiveFilter(tab)}
                    className={`rounded-md px-4 py-2 transition-all ${
                      activeFilter === tab
                        ? "bg-[#183327] text-white shadow-sm"
                        : "text-neutral-600 hover:text-neutral-900"
                    }`}
                  >
                    {tab}
                  </button>
                ))}
              </div>

              {/* Selector temporal */}
              <select className="rounded-lg border border-neutral-200 bg-white px-3.5 py-2 text-xs font-medium text-neutral-700 shadow-sm outline-none focus:border-[#183327]">
                <option>Últimos 7 días</option>
                <option>Últimos 30 días</option>
                <option>Todo el año</option>
              </select>
            </div>

            {/* Input de búsqueda */}
            <div className="relative w-full sm:w-72">
              <span className="pointer-events-none absolute inset-y-0 left-3 flex items-center text-neutral-400">
                <svg className="h-4 w-4 stroke-current" fill="none" viewBox="0 0 24 24" strokeWidth="2">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z" />
                </svg>
              </span>
              <input
                type="text"
                placeholder="Buscar por email..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full rounded-lg border border-neutral-200 bg-white py-2 pl-9 pr-4 text-xs shadow-sm outline-none placeholder:text-neutral-400 focus:border-[#183327]"
              />
            </div>
          </div>

          {/* Tarjetas de Métricas */}
          <div className="grid w-full grid-cols-1 gap-4 sm:grid-cols-3">
            {[
              { label: "Leads totales", value: "142" },
              { label: "Completados", value: "142" },
              { label: "Parciales", value: "142" },
            ].map((kpi, idx) => (
              <div
                key={idx}
                className="flex flex-col justify-between rounded-xl border border-neutral-200/60 bg-white p-6 shadow-sm"
              >
                <p className="text-xs font-medium text-neutral-500">{kpi.label}</p>
                <p className="mt-4 text-2xl font-bold text-neutral-900 sm:text-3xl">{kpi.value}</p>
              </div>
            ))}
          </div>

          {/* Tabla de Leads con Scroll horizontal */}
          <div className="w-full overflow-hidden rounded-xl border border-neutral-200/60 bg-white shadow-sm">
            <div className="overflow-x-auto">
              <table className="w-full min-w-[700px] text-left text-sm">
                <thead className="border-b border-neutral-100 bg-[#FBFBFA] text-xs font-bold text-neutral-700">
                  <tr>
                    <th className="px-6 py-4">Email</th>
                    <th className="px-6 py-4">Fecha</th>
                    <th className="px-6 py-4">Estado</th>
                    <th className="px-6 py-4">Origen</th>
                    <th className="px-6 py-4 text-right"></th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-neutral-100 text-neutral-600">
                  {filteredLeads.map((row) => (
                    <tr key={row.id} className="hover:bg-neutral-50/50 transition-colors">
                      <td className="px-6 py-4 font-medium text-neutral-800">{row.email}</td>
                      <td className="px-6 py-4 text-neutral-500">{row.date}</td>
                      <td className="px-6 py-4">
                        <span
                          className={`inline-block rounded-full px-3 py-0.5 text-xs font-medium ${
                            row.status === "Completado"
                              ? "bg-[#DDECE4] text-[#1E3A2F]"
                              : "bg-[#FDF3D8] text-[#8C6B1C]"
                          }`}
                        >
                          {row.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-neutral-500">{row.source}</td>
                      <td className="px-6 py-4 text-right">
                        <Link
                          href={`/admin/leads/${row.id}`}
                          className="inline-flex items-center gap-1 text-xs font-semibold text-[#183327] hover:underline"
                        >
                          Ver detalle <span>&rarr;</span>
                        </Link>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Paginación */}
          <div className="flex items-center justify-between text-xs text-neutral-500 pb-6">
            <span>1–10 de 142</span>
            <div className="flex gap-4 font-medium text-neutral-700">
              <button className="hover:text-neutral-900 disabled:opacity-40" disabled>&larr; Anterior</button>
              <button className="hover:text-neutral-900">Siguiente &rarr;</button>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}