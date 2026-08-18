import React from "react";

export default function Recomendaciones({ items }: { items: string[] }) {
  return (
    <div className="max-w-3xl mx-auto mt-12">
      <h2 className="text-xl font-extrabold text-text-primary mb-4">Recomendaciones personalizadas</h2>

      <div className="flex flex-col gap-3">
        {items.map((item, index) => (
          <div key={index} className="flex items-start gap-3 rounded-md border border-base-border bg-white p-4">
            <span className="flex h-7 w-7 shrink-0 items-center justify-center rounded-md bg-base-internal text-xs font-bold text-text-secondary">
              {String(index + 1).padStart(2, "0")}
            </span>
            <p className="text-sm text-text-primary">{item}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
