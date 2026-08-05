import React from "react";

export default function Recomendaciones() {
  const items = [
    "Implementá monitoreo por carga de trabajo para reducir el punto ciego de visibilidad.",
    "Establecé un proceso de baja de recursos ociosos con revisión trimestral.",
    "Automatizá el apagado programado en ambientes no productivos.",
    "Asigná ownership formal a cada carga para sostener la gobernanza en el tiempo."
  ];

  return (
    <div className="max-w-3xl mx-auto mt-12">
      <h2 className="text-xl font-extrabold text-text-primary mb-4">Recomendaciones personalizadas</h2>
      
      <div className="flex flex-col gap-3">
        {items.map((text, index) => (
          <div key={index} className="flex items-center gap-4 bg-white p-4 border border-base-border rounded-md shadow-sm">
            <div className="flex bg-forest-light text-forest-dark font-extrabold text-[18px] w-[35px] justify-center px-2 py-1 rounded">
              0{index + 1}
            </div>
            <p className="text-base text-text-primary">{text}</p>
          </div>
        ))}
      </div>
    </div>
  );
}