import React from "react";

export default function ChartPosicion() {
  const data = [
    { label: "Hyperscale", score: 71 },
    { label: "Colocation", score: 58 },
    { label: "Enterprise (promedio)", score: 46 },
  ];

  return (
    <div className="max-w-3xl mx-auto bg-white rounded-md shadow-sm border border-base-border p-6 sm:p-8 mt-10">
      <h3 className="font-extrabold text-xl text-text-primary mb-1">Tu posición frente a la industria</h3>
      <p className="text-base text-text-secondary mb-8">Score de madurez (0-100) por segmento</p>

      <div className="flex flex-col gap-5">
        <div>
          <p className="text-bases text-gold-dark font-medium mb-1.5">Tu resultado</p>
          <div className="flex items-center gap-3 sm:gap-4">
            <div className="flex-grow h-2.5 sm:h-3 bg-base-natural rounded-full overflow-hidden">
              <div className="h-full bg-gold-dark rounded-full transition-all duration-1000" style={{ width: '54%' }}></div>
            </div>
            <span className="text-xs sm:text-sm font-bold text-gold-dark w-5 text-right">54</span>
          </div>
        </div>

        {data.map((item, index) => (
          <div key={index}>
            <p className="text-base text-text-primary font-medium mb-1.5">{item.label}</p>
            <div className="flex items-center gap-3 sm:gap-4">
              <div className="flex-grow h-2.5 sm:h-3 bg-base-natural rounded-full overflow-hidden">
                <div className="h-full bg-forest-dark rounded-full transition-all duration-1000" style={{ width: `${item.score}%` }}></div>
              </div>
              <span className="text-xs sm:text-sm font-medium text-text-primary w-5 text-right">{item.score}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}