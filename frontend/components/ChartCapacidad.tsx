import React from "react";

export default function ChartCapacidad() {
  // Arreglo de datos con las alturas simuladas en clases de Tailwind
  const data = [
    { year: "2022", encendida: "h-[55%]", utilizada: "h-[30%]" },
    { year: "2023", encendida: "h-[65%]", utilizada: "h-[35%]" },
    { year: "2024", encendida: "h-[70%]", utilizada: "h-[35%]" },
    { year: "2025", encendida: "h-[75%]", utilizada: "h-[40%]" },
    { year: "2026", encendida: "h-[90%]", utilizada: "h-[45%]" },
  ];

  return (
    <div className="w-full rounded-xl border border-base-border bg-white p-6 shadow-sm sm:p-8">
      
      {/* Header del gráfico y Leyenda */}
      <div className="mb-10 flex flex-col items-start justify-between gap-4 sm:flex-row sm:items-center">
        <h3 className="font-display text-base font-semibold text-text-primary">
          Capacidad instalada vs. utilizada (MW promedio por instalación)
        </h3>

        <div className="flex items-center gap-5 text-sm text-text-secondary">
          <div className="flex items-center gap-2">
            <span className="h-3 w-3 rounded-sm bg-forest-light"></span>
            <span>Encendida</span>
          </div>
          <div className="flex items-center gap-2">
            <span className="h-3 w-3 rounded-sm bg-forest-dark"></span>
            <span>Utilizada</span>
          </div>
        </div>
      </div>

      {/* Área de las barras */}
      {/* Usamos items-end para que las barras "crezcan" desde abajo hacia arriba */}
      <div className="flex h-[240px] w-full items-end justify-between border-b border-base-border pb-3 sm:justify-around">
        {data.map((item) => (
          <div key={item.year} className="flex h-full flex-col justify-end gap-3">
            
            {/* Contenedor del par de barras */}
            <div className="flex h-full items-end justify-center gap-1 sm:gap-2">
              <div 
                className={`w-8 rounded-t-sm bg-forest-light transition-all hover:opacity-80 sm:w-12 ${item.encendida}`}
              ></div>
              <div 
                className={`w-8 rounded-t-sm bg-forest-dark transition-all hover:opacity-80 sm:w-12 ${item.utilizada}`}
              ></div>
            </div>
            
            {/* Etiqueta del año */}
            <span className="text-center font-display text-sm text-text-secondary">
              {item.year}
            </span>
            
          </div>
        ))}
      </div>
    </div>
  );
}