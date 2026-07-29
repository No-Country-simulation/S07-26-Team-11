"use client";
import { useState } from "react";

const mockPreguntas = [
  {
    id: 1,
    pasoBarra: 1, 
    categoria: "VISIBILIDAD",
    pregunta: "¿Con qué granularidad podés ver la utilización real de cómputo por carga de trabajo?",
    opciones: [
      "No tenemos visibilidad más allá del consumo eléctrico total del sitio.",
      "Vemos utilización agregada por rack o fila, sin detalle por carga.",
      "Tenemos dashboards por servidor, actualizados diariamente.",
      "Monitoreo en tiempo real por carga de trabajo, con alertas automáticas."
    ]
  },
  {
    id: 2,
    pasoBarra: 4,
    categoria: "AUTOMATIZACIÓN",
    pregunta: "¿Existen procesos automáticos para identificar y recuperar capacidad fantasma?",
    opciones: [
      "Todo el escalado es manual.",
      "Hay reglas básicas de alertas, pero la acción es manual.",
      "Auto escalado parcial en algunos ambientes críticos",
      "Auto escalado extendido a la mayoría de las cargas, con políticas definidas."
    ]
  }
];

const pasosProgreso = [
  { num: 1, label: "Visibilidad" },
  { num: 2, label: "Eficiencia energética" },
  { num: 3, label: "Gobernanza de datos" },
  { num: 4, label: "Automatización" }
];

export default function BenchmarkPage() {
  const [indiceActual, setIndiceActual] = useState(0);
  const preguntaActiva = mockPreguntas[indiceActual];

  const irSiguiente = () => {
    if (indiceActual < mockPreguntas.length - 1) {
      setIndiceActual(indiceActual + 1);
    }
  };

  const irAtras = () => {
    if (indiceActual > 0) {
      setIndiceActual(indiceActual - 1);
    }
  };

  return (
    <div className="min-h-screen bg-base-natural font-display flex flex-col relative overflow-hidden">
      
      <header className="w-full bg-white flex items-center px-40 py-5 gap-6 shadow-sm z-20 relative">
        <img src="/assets/brand/logotipo.svg" alt="Capacia Logo" className="h-9" />
        <span className="text-text-secondary font-semibold">/ Maturity Benchmark</span>
      </header>

      <section className="w-full flex flex-col px-[408px] py-[26px] gap-[10px] bg-white border-b border-base-border z-20 relative">
        <div className="flex flex-row items-center justify-between w-full">
          
          {pasosProgreso.map((paso, index) => {
            const isCompleted = paso.num < preguntaActiva.pasoBarra;
            const isActive = paso.num === preguntaActiva.pasoBarra;
            const isInactive = paso.num > preguntaActiva.pasoBarra;

            const stepNode = (
              <div key={`step-${paso.num}`} className="flex flex-col items-center gap-2">
                
                {isCompleted && (
                  <div className="w-10 h-10 rounded-full bg-forest text-white flex items-center justify-center">
                    <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="3">
                      <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                )}
                
                {isActive && (
                  <div className="w-10 h-10 rounded-full bg-gold text-white flex items-center justify-center font-bold">
                    {paso.num}
                  </div>
                )}
                
                {isInactive && (
                  <div className="w-10 h-10 rounded-full bg-white border-2 border-base-border text-text-secondary flex items-center justify-center font-bold">
                    {paso.num}
                  </div>
                )}
                
                <span className={`text-sm ${isActive ? 'text-gold font-semibold' : isCompleted ? 'text-forest' : 'text-text-secondary'}`}>
                  {paso.label}
                </span>
              </div>
            );

            if (index === pasosProgreso.length - 1) {
              return stepNode;
            }

            const lineNode = (
              <div key={`line-${paso.num}`} className="flex-1 h-[2px] bg-base-border mx-4"></div>
            );

            return [stepNode, lineNode];
          })}

        </div>
      </section>

      <div className="absolute left-[5%] top-[55%] -translate-y-1/2 opacity-80 pointer-events-none z-0">
        <img src="/assets/backgrounds/vector.svg" alt="Fondo" className="w-[387.27px] h-[433.97px]" />
      </div>

      <main className="flex justify-center p-8 z-10 relative mt-20 mb-20">
        <div className="bg-white rounded-xl p-8 shadow-sm max-w-2xl w-full border border-base-border">
          
          <h4 className="text-gold font-bold text-sm uppercase tracking-wide mb-2">
            {preguntaActiva.categoria}
          </h4>
          <h1 className="text-text-primary text-xl font-extrabold mb-3">
            {preguntaActiva.pregunta}
          </h1>
          <p className="text-text-secondary text-sm mb-6">
            Pregunta {indiceActual + 1} de {mockPreguntas.length}
          </p>

          <div className="flex flex-col gap-3">
            {preguntaActiva.opciones.map((opcion, index) => (
              <label key={index} className="flex items-center p-4 border border-base-border rounded-lg cursor-pointer hover:bg-base-internal transition-colors">
                <input type="radio" name={`pregunta-${preguntaActiva.id}`} className="w-5 h-5 mr-4" />
                <span className="text-text-primary">{opcion}</span>
              </label>
            ))}
          </div>

          <div className="flex justify-between items-center mt-8">
            <button 
              onClick={irAtras}
              disabled={indiceActual === 0}
              className={`font-bold text-xs flex items-center ${indiceActual === 0 ? 'text-base-border cursor-not-allowed' : 'text-forest hover:text-forest-dark'}`}
            >
              ← Atrás
            </button>
            
            <button 
              onClick={irSiguiente}
              disabled={indiceActual === mockPreguntas.length - 1}
              className={`font-bold text-xs flex items-center ${indiceActual === mockPreguntas.length - 1 ? 'text-base-border cursor-not-allowed' : 'text-forest hover:text-forest-dark'}`}
            >
              Siguiente →
            </button>
          </div>
        </div>
      </main>

      <footer className="w-full bg-white flex items-center justify-between px-40 py-5 border-t border-base-border mt-auto z-20 relative">
        <img src="/Logotipo.svg" alt="Capacia Logo" className="h-10" />
        
        <div className="flex items-center gap-6 text-xs text-text-secondary">
          <a href="#" className="hover:text-text-primary transition-colors">Privacidad</a>
          <a href="#" className="hover:text-text-primary transition-colors">Términos y Condiciones</a>
          <a href="#" className="hover:text-text-primary transition-colors">Contacto</a>
        </div>
        
        <span className="text-xs text-text-secondary">
          © 2026 Capacia, Inc.
        </span>
      </footer>
      
    </div>
  );
}