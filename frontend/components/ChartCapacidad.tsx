import React from "react";

export default function ChartCapacidad() {
  const data = [
    { year: "2022", encendida: "55%", utilizada: "30%" },
    { year: "2023", encendida: "65%", utilizada: "35%" },
    { year: "2024", encendida: "70%", utilizada: "35%" },
    { year: "2025", encendida: "75%", utilizada: "40%" },
    { year: "2026", encendida: "90%", utilizada: "45%" },
  ];

  return (
    <div className="w-full overflow-hidden rounded-xl border border-base-border bg-white p-4 shadow-sm sm:p-8">
      {/* Header del gráfico y Leyenda */}
      <div className="mb-6 flex flex-col items-start justify-between gap-3 sm:mb-10 sm:flex-row sm:items-center sm:gap-4">
        <h3 className="font-display text-sm font-semibold text-text-primary sm:text-base">
          Capacidad instalada vs. utilizada (MW promedio por instalación)
        </h3>

        <div className="flex items-center gap-4 text-xs text-text-secondary sm:gap-5 sm:text-sm">
          <div className="flex items-center gap-1.5 sm:gap-2">
            <span className="h-2.5 w-2.5 rounded-sm bg-forest-light sm:h-3 sm:w-3"></span>
            <span>Encendida</span>
          </div>
          <div className="flex items-center gap-1.5 sm:gap-2">
            <span className="h-2.5 w-2.5 rounded-sm bg-forest-dark sm:h-3 sm:w-3"></span>
            <span>Utilizada</span>
          </div>
        </div>
      </div>

      {/* Área de las barras + Eje x */}
      <div className="flex h-[200px] w-full items-end justify-between border-b border-base-border pb-2 sm:h-[240px] sm:pb-3">
        {data.map((item) => (
          <div
            key={item.year}
            className="flex h-full flex-1 flex-col items-center justify-end gap-2"
          >
            {/* Contenedor del par de barras */}
            <div className="flex h-full items-end justify-center gap-0.5 min-[380px]:gap-1 sm:gap-2">
              <div
                style={{ height: item.encendida }}
                className="w-2.5 rounded-t-sm bg-forest-light transition-all hover:opacity-80 min-[380px]:w-3.5 sm:w-10"
                title={`Encendida: ${item.encendida}`}
              ></div>
              <div
                style={{ height: item.utilizada }}
                className="w-2.5 rounded-t-sm bg-forest-dark transition-all hover:opacity-80 min-[380px]:w-3.5 sm:w-10"
                title={`Utilizada: ${item.utilizada}`}
              ></div>
            </div>

            {/* Etiqueta del año */}
            <span className="text-center font-display text-[11px] text-text-secondary min-[380px]:text-xs sm:text-sm">
              {item.year}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}