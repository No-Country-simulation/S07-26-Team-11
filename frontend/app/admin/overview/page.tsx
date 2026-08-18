import Sidebar from "@/components/Sidebar";

export default function OverviewPage() {
  const kpis = [
    {
      icon: (
        <svg className="h-5 w-5 stroke-amber-600" fill="none" viewBox="0 0 24 24" strokeWidth="2">
          <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 18L9 11.25l4.306 4.307a11.95 11.95 0 015.814-5.519l2.74-1.22m0 0l-5.94-2.28m5.94 2.28l-2.28 5.941" />
        </svg>
      ),
      iconBg: "bg-amber-100/70",
      title: "Leads nuevos esta semana",
      value: "5",
    },
    {
      icon: (
        <svg className="h-5 w-5 stroke-teal-600" fill="none" viewBox="0 0 24 24" strokeWidth="2">
          <path strokeLinecap="round" strokeLinejoin="round" d="M9 14.25l6-6m4.5-3.75h-15a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 004.5 21h15a2.25 2.25 0 002.25-2.25V5.25A2.25 2.25 0 0019.5 3z" />
        </svg>
      ),
      iconBg: "bg-teal-100/70",
      title: "Completitud Calculator + Benchmark",
      value: "41%",
    },
    {
      icon: (
        <svg className="h-5 w-5 stroke-red-500" fill="none" viewBox="0 0 24 24" strokeWidth="2">
          <path strokeLinecap="round" strokeLinejoin="round" d="M6.75 3v2.25M17.25 3v2.25M3 18.75V7.5a2.25 2.25 0 012.25-2.25h13.5A2.25 2.25 0 0121 7.5v11.25m-18 0A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75m-18 0v-7.5A2.25 2.25 0 015.25 9h13.5A2.25 2.25 0 0121 11.25v7.5" />
        </svg>
      ),
      iconBg: "bg-red-100/70",
      title: "Reuniones agendadas",
      value: "12",
    },
    {
      icon: (
        <svg className="h-5 w-5 stroke-neutral-500" fill="none" viewBox="0 0 24 24" strokeWidth="2">
          <path strokeLinecap="round" strokeLinejoin="round" d="M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      ),
      iconBg: "bg-neutral-200/70",
      title: "Score de madurez promedio",
      value: "52 / 100",
    },
  ];

  const recentActivity = [
    { email: "gerencia@northbridge.com", date: "29 jul 2026", status: "Parcial" },
    { email: "ops@velarion.io", date: "28 jul 2026", status: "Completado" },
    { email: "it@metrocore.com", date: "25 jul 2026", status: "Parcial" },
    { email: "infra@haldis.net", date: "24 jul 2026", status: "Completado" },
    { email: "contacto@solvex.com", date: "23 jul 2026", status: "Completado" },
  ];

  const funnelSteps = [
    { label: "Calculator iniciado", value: 64, percentageWidth: "100%", subText: null },
    { label: "Calculator completado", value: 52, percentageWidth: "81%", subText: "- 19%" },
    { label: "Benchmark completado", value: 27, percentageWidth: "42%", subText: "- 48%" },
    { label: "Results visto", value: 24, percentageWidth: "37%", subText: "- 11%" },
    { label: "PDF descargado", value: 18, percentageWidth: "28%", subText: "- 25%" },
    { label: "Reunión agendada", value: 5, percentageWidth: "8%", subText: "- 33%" },
  ];

  return (
    <div className="flex min-h-screen bg-[#F7F7F4] text-[#1E1E1E]">
      <Sidebar />

      <main className="flex-1 overflow-y-auto p-10">
        <h1 className="text-3xl font-bold tracking-tight text-neutral-900">Overview</h1>

        <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {kpis.map((kpi, idx) => (
            <div key={idx} className="flex flex-col justify-between rounded-xl bg-white p-5 shadow-sm border border-neutral-200/50">
              <div className="flex flex-col gap-3">
                <div className={`flex h-8 w-8 items-center justify-center rounded-md ${kpi.iconBg}`}>
                  {kpi.icon}
                </div>
                <p className="text-xs font-medium text-neutral-500">{kpi.title}</p>
              </div>
              <p className="mt-4 text-2xl font-bold text-neutral-900">{kpi.value}</p>
            </div>
          ))}
        </div>

        <div className="mt-10">
          <h2 className="text-xl font-extrabold text-neutral-900">Actividad reciente</h2>
          <div className="mt-4 overflow-x-auto rounded-xl border border-neutral-200/50 bg-white shadow-sm">
            <table className="w-full min-w-[600px] text-left text-sm">
            <thead className="border-b border-neutral-200 bg-[#B8B7B733] text-[14px] font-bold text-neutral-700">
                <tr>
                <th className="px-6 py-4 whitespace-nowrap">Email</th>
                <th className="px-6 py-4 whitespace-nowrap">Fecha</th>
                <th className="px-6 py-4 whitespace-nowrap">Estado</th>
                <th className="px-6 py-4 text-right whitespace-nowrap"></th>
                </tr>
            </thead>
            <tbody className="divide-y divide-neutral-100 text-neutral-600">
                {recentActivity.map((row, idx) => (
                <tr key={idx} className="hover:bg-neutral-50/50 transition-colors">
                    <td className="px-6 py-3.5 font-medium text-neutral-800 whitespace-nowrap">
                    {row.email}
                    </td>
                    <td className="px-6 py-3.5 text-neutral-500 whitespace-nowrap">
                    {row.date}
                    </td>
                    <td className="px-6 py-3.5 whitespace-nowrap">
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
                    <td className="px-6 py-3.5 text-right whitespace-nowrap">
                    <button className="inline-flex items-center gap-1 text-xs font-semibold text-neutral-700 hover:text-neutral-900">
                        Ver más <span>&rarr;</span>
                    </button>
                    </td>
                </tr>
                ))}
            </tbody>
            </table>
          </div>
        </div>

        <div className="mt-10 mb-8">
          <h2 className="text-xl font-extrabold text-neutral-900">Funnel de conversión</h2>
          <div className="mt-4 space-y-4 rounded-xl border border-neutral-200/50 bg-white p-6 shadow-sm">
            {funnelSteps.map((step, idx) => (
              <div key={idx} className="space-y-1.5">
                <span className="text-xs font-medium text-neutral-700">{step.label}</span>
                <div className="flex items-center gap-3">
                  <div className="relative h-2.5 flex-1 rounded-full bg-neutral-100">
                    <div
                      className="h-full rounded-full bg-[#183327]"
                      style={{ width: step.percentageWidth }}
                    />
                  </div>
                  <div className="flex w-16 items-center justify-end gap-1.5 text-xs font-semibold text-neutral-900">
                    <span>{step.value}</span>
                    {step.subText && (
                      <span className="text-[11px] font-normal text-emerald-600">
                        {step.subText}
                      </span>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </main>
    </div>
  );
}