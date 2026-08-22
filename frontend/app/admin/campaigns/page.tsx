"use client";

import { useState, type FormEvent } from "react";
import Sidebar from "@/components/Sidebar";

type CampaignType = "Outreach" | "Contenido" | "Paga";

type Campaign = {
  id: number;
  name: string;
  type: CampaignType;
  leads: number;
  conversion: string;
};

const initialCampaigns: Campaign[] = [
  {
    id: 1,
    name: "Data Center Summit 2026",
    type: "Outreach",
    leads: 34,
    conversion: "26%",
  },
  {
    id: 2,
    name: "Report – descargas orgánicas",
    type: "Contenido",
    leads: 51,
    conversion: "41%",
  },
  {
    id: 3,
    name: "LinkedIn Ads Q3",
    type: "Paga",
    leads: 19,
    conversion: "12%",
  },
];

export default function CampaignsPage() {
  const [name, setName] = useState("");
  const [type, setType] = useState<CampaignType>("Outreach");
  const [campaigns, setCampaigns] = useState<Campaign[]>(initialCampaigns);
  const [message, setMessage] = useState("");

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const normalizedName = name.trim();
    if (!normalizedName) return;

    setCampaigns((current) => [
      ...current,
      {
        id: Date.now(),
        name: normalizedName,
        type,
        leads: 0,
        conversion: "0%",
      },
    ]);
    setName("");
    setMessage(`Campaña “${normalizedName}” creada correctamente`);
  }

  return (
    <div className="relative flex min-h-screen bg-[#F7F7F4] text-[#1E1E1E]">
      <Sidebar />

      <main className="relative flex-1 overflow-y-auto px-6 py-8 sm:px-10 lg:py-10">
        <img
          src="/assets/backgrounds/vector.svg"
          alt=""
          aria-hidden="true"
          className="pointer-events-none fixed -bottom-20 -right-16 z-0 hidden w-[420px] opacity-30 select-none lg:block"
        />

        <div className="relative z-10 w-full space-y-8">
          <h1 className="text-2xl font-bold tracking-tight text-neutral-900 sm:text-3xl">
            Campaigns
          </h1>

          <section>
            <h2 className="text-lg font-bold text-neutral-900 sm:text-xl">
              Nueva campaña
            </h2>

            <form
              onSubmit={handleSubmit}
              className="mt-4 rounded-xl border border-neutral-200/60 bg-white p-5 shadow-sm"
            >
              <label
                htmlFor="campaign-name"
                className="mb-2 block text-xs font-medium text-neutral-700"
              >
                Nombre
              </label>

              <div className="grid grid-cols-1 gap-3 sm:grid-cols-[minmax(0,1fr)_220px_auto]">
                <input
                  id="campaign-name"
                  type="text"
                  required
                  value={name}
                  onChange={(event) => {
                    setName(event.target.value);
                    setMessage("");
                  }}
                  placeholder="Data Center 2026"
                  className="h-11 min-w-0 rounded-md border border-neutral-300 bg-[#F7F7F4] px-4 text-sm text-neutral-800 outline-none transition-colors placeholder:text-neutral-500 focus:border-[#183327] focus:ring-2 focus:ring-[#183327]/10"
                />

                <label htmlFor="campaign-type" className="sr-only">
                  Tipo de campaña
                </label>
                <select
                  id="campaign-type"
                  value={type}
                  onChange={(event) =>
                    setType(event.target.value as CampaignType)
                  }
                  className="h-11 rounded-md border border-neutral-300 bg-white px-4 text-sm text-neutral-600 outline-none transition-colors focus:border-[#183327] focus:ring-2 focus:ring-[#183327]/10"
                >
                  <option value="Outreach">Outreach</option>
                  <option value="Contenido">Contenido</option>
                  <option value="Paga">Paga</option>
                </select>

                <button
                  type="submit"
                  className="h-11 shrink-0 rounded-md bg-[#0F291E] px-6 text-sm font-semibold text-white transition-colors hover:bg-[#183D2E] focus:outline-none focus:ring-2 focus:ring-[#183327] focus:ring-offset-2"
                >
                  Crear campaña
                </button>
              </div>

              <p className="mt-2 min-h-4 text-xs text-[#1D6E52]" role="status">
                {message}
              </p>
            </form>
          </section>

          <section className="pb-8">
            <h2 className="text-lg font-bold text-neutral-900 sm:text-xl">
              Campañas
            </h2>

            <div className="mt-4 overflow-hidden rounded-xl border border-neutral-200/60 bg-white shadow-sm">
              <div className="overflow-x-auto">
                <table className="w-full min-w-[780px] text-left text-sm">
                  <thead className="border-b border-neutral-200 bg-[#F0F0EE] text-xs font-bold text-neutral-700">
                    <tr>
                      <th className="px-6 py-4">Nombre</th>
                      <th className="px-6 py-4">Tipo</th>
                      <th className="px-6 py-4">Leads generados</th>
                      <th className="px-6 py-4">
                        Conversión a Benchmark completo
                      </th>
                      <th className="px-6 py-4" aria-label="Acciones" />
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-neutral-100 text-neutral-600">
                    {campaigns.map((campaign) => (
                      <tr
                        key={campaign.id}
                        className="transition-colors hover:bg-neutral-50/60"
                      >
                        <td className="px-6 py-3.5 font-medium text-neutral-700">
                          {campaign.name}
                        </td>
                        <td className="px-6 py-3.5 text-neutral-500">
                          {campaign.type}
                        </td>
                        <td className="px-6 py-3.5 text-neutral-500">
                          {campaign.leads}
                        </td>
                        <td className="px-6 py-3.5 text-neutral-500">
                          {campaign.conversion}
                        </td>
                        <td className="px-6 py-3.5 text-right">
                          <button
                            type="button"
                            className="inline-flex items-center gap-1 whitespace-nowrap text-xs font-semibold text-[#1D6E52] transition-colors hover:text-[#0F291E] hover:underline"
                            aria-label={`Ver detalle de ${campaign.name}`}
                          >
                            Ver detalle <span aria-hidden="true">→</span>
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </section>
        </div>
      </main>
    </div>
  );
}
