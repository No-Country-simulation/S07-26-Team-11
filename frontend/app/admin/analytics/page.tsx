import Sidebar from "@/components/Sidebar";

export default function BenchmarkAnalyticsPage() {
  const topMetrics = [
    {
      label: "Score promedio (agregado)",
      value: "52 / 100",
    },
    {
      label: "Benchmarks completados",
      value: "87",
    },
    {
      label: "Nivel más común",
      value: "Gestionado",
    },
  ];

  const maturityLevels = [
    { label: "Reactivo", count: 19, heightClass: "h-24 sm:h-28" },
    { label: "Gestionado", count: 34, heightClass: "h-40 sm:h-48" },
    { label: "Optimizado", count: 24, heightClass: "h-32 sm:h-36" },
    { label: "Predictivo", count: 10, heightClass: "h-14 sm:h-16" },
  ];

  const segments = [
    { label: "Hyperscale", score: 71, width: "71%" },
    { label: "Colocation", score: 58, width: "58%" },
    { label: "Enterprise (promedio)", score: 46, width: "46%" },
  ];

  return (
    <div className="relative flex min-h-screen bg-[#F7F7F4] text-[#1E1E1E]">
      <Sidebar />

      <main className="relative flex-1 overflow-y-auto px-6 py-8 sm:px-10 lg:py-10">
        {/* Marca de agua decorativa en la esquina inferior */}
        <img
          src="/assets/backgrounds/vector.svg"
          alt=""
          aria-hidden="true"
          className="pointer-events-none fixed -bottom-16 -right-16 z-0 hidden w-[420px] opacity-30 select-none lg:block"
        />

        <div className="relative z-10 w-full space-y-8">
          <h1 className="text-2xl font-bold tracking-tight text-neutral-900 sm:text-3xl">
            Benchmark Analytics
          </h1>

          {/* 1. Métricas superiores */}
          <div className="grid w-full grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {topMetrics.map((metric, idx) => (
              <div
                key={idx}
                className="flex flex-col justify-between rounded-xl border border-neutral-200/60 bg-white p-6 shadow-sm"
              >
                <p className="text-xs font-medium text-neutral-500">{metric.label}</p>
                <p className="mt-4 text-2xl font-bold text-neutral-900 sm:text-3xl">
                  {metric.value}
                </p>
              </div>
            ))}
          </div>

          {/* 2. Distribución por nivel de madurez */}
          <div className="w-full">
            <h2 className="text-lg font-bold text-neutral-900 sm:text-xl">
              Distribución por nivel de madurez
            </h2>
            <p className="mt-1 text-xs text-neutral-500">
              Cantidad de leads por nivel, sobre 87 benchmarks completados
            </p>

            <div className="mt-4 w-full overflow-x-auto rounded-xl border border-neutral-200/60 bg-white p-6 shadow-sm">
              <div className="flex min-w-[360px] items-end justify-around gap-2 pt-10 pb-4 sm:gap-6 sm:px-12">
                {maturityLevels.map((item, idx) => (
                  <div key={idx} className="flex flex-col items-center gap-2">
                    <span className="text-xs font-semibold text-neutral-700">
                      {item.count}
                    </span>
                    <div className="flex h-48 sm:h-56 w-16 sm:w-24 items-end justify-center">
                      <div
                        className={`w-full rounded-t-md bg-[#0F291E] ${item.heightClass} transition-all duration-300`}
                      />
                    </div>
                    <span className="mt-2 text-center text-xs font-medium text-neutral-600">
                      {item.label}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* 3. Comparación por segmento */}
          <div className="w-full pb-8">
            <h2 className="text-lg font-bold text-neutral-900 sm:text-xl">
              Comparación por segmento
            </h2>
            <p className="mt-1 text-xs text-neutral-500">
              Score promedio (0–100)
            </p>

            <div className="mt-4 w-full space-y-6 rounded-xl border border-neutral-200/60 bg-white p-6 shadow-sm">
              {segments.map((segment, idx) => (
                <div key={idx} className="space-y-2">
                  <span className="text-xs font-medium text-neutral-700">
                    {segment.label}
                  </span>
                  <div className="flex items-center gap-4">
                    <div className="relative h-2.5 flex-1 rounded-full bg-neutral-100">
                      <div
                        className="h-full rounded-full bg-[#0F291E]"
                        style={{ width: segment.width }}
                      />
                    </div>
                    <span className="w-8 text-right text-xs font-semibold text-neutral-900">
                      {segment.score}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}