import React from "react";

export interface ChartPosicionItem {
  label: string;
  score: number;
  own?: boolean;
}

export default function ChartPosicion({ data }: { data: ChartPosicionItem[] }) {
  return (
    <div className="max-w-3xl mx-auto bg-white rounded-md shadow-sm border border-base-border p-6 sm:p-8 mt-10">
      <h3 className="font-extrabold text-xl text-text-primary mb-1">Tu posición frente a la industria</h3>
      <p className="text-base text-text-secondary mb-8">Score de madurez (0-100) por segmento</p>

      <div className="flex flex-col gap-5">
        {data.map((item, index) => (
          <div key={index}>
            <div className="flex items-center justify-between mb-1.5">
              <span className={`text-sm font-medium ${item.own ? 'text-gold-dark' : 'text-text-primary'}`}>
                {item.label}
              </span>
              <span className="text-sm font-bold text-text-primary">{item.score}</span>
            </div>
            <div className="h-3 w-full rounded-full bg-base-internal overflow-hidden">
              <div
                className={`h-full rounded-full ${item.own ? 'bg-gold' : 'bg-forest-dark'}`}
                style={{ width: `${item.score}%` }}
              />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
