"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import Footer from "@/components/Footer";
import Header from "@/components/Header";
import RequireAuth from "@/components/RequireAuth";
import { BENCHMARK_QUESTIONS, DIMENSIONS } from "@/lib/benchmarkQuestions";
import { BENCHMARK_ANSWERS_KEY } from "@/lib/benchmarkStorage";
import type { BenchmarkAnswers } from "@/lib/benchmarkScoring";

const pasosProgreso = DIMENSIONS.map((dimension, index) => ({
  num: index + 1,
  label: dimension.stepLabel,
}));

function pasoDe(dimensionCode: (typeof DIMENSIONS)[number]["code"]): number {
  return DIMENSIONS.findIndex((d) => d.code === dimensionCode) + 1;
}

function Cuestionario() {
  const router = useRouter();
  const [indiceActual, setIndiceActual] = useState(0);
  const [respuestas, setRespuestas] = useState<BenchmarkAnswers>({});

  const preguntaActiva = BENCHMARK_QUESTIONS[indiceActual];
  const esUltimaPregunta = indiceActual === BENCHMARK_QUESTIONS.length - 1;
  const pasoActivo = pasoDe(preguntaActiva.dimensionCode);

  const scoreSeleccionado = respuestas[preguntaActiva.id];
  const tieneRespuesta = scoreSeleccionado !== undefined;

  const seleccionarOpcion = (score: number) => {
    setRespuestas((prev) => ({ ...prev, [preguntaActiva.id]: score }));
  };

  const irSiguiente = () => {
    if (!tieneRespuesta) return;

    if (esUltimaPregunta) {
      sessionStorage.setItem(BENCHMARK_ANSWERS_KEY, JSON.stringify(respuestas));
      router.push("/benchmark/resultados");
    } else {
      setIndiceActual(indiceActual + 1);
    }
  };

  const irAtras = () => {
    if (indiceActual > 0) setIndiceActual(indiceActual - 1);
  };

  return (
    <div className="min-h-screen bg-base-natural font-display flex flex-col relative overflow-hidden">
      <Header subtitle="/ Maturity Benchmark" />

      <section className="w-full flex flex-col px-[408px] py-[26px] gap-[10px] bg-white border-b border-base-border z-20 relative">
        <div className="flex flex-row items-center justify-between w-full">
          {pasosProgreso.map((paso, index) => {
            const isCompleted = paso.num < pasoActivo;
            const isActive = paso.num === pasoActivo;
            const isInactive = paso.num > pasoActivo;

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

            if (index === pasosProgreso.length - 1) return stepNode;

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
            {DIMENSIONS.find((d) => d.code === preguntaActiva.dimensionCode)?.categoryLabel}
          </h4>
          <h1 className="text-text-primary text-xl font-extrabold mb-3">
            {preguntaActiva.text}
          </h1>
          <p className="text-text-secondary text-sm mb-6">
            Pregunta {preguntaActiva.orderInDimension} de 2
          </p>

          <div className="flex flex-col gap-3">
            {preguntaActiva.options.map((opcion, index) => {
              const estaSeleccionada = scoreSeleccionado === opcion.score;

              return (
                <label
                  key={index}
                  className={`flex items-center p-4 border rounded-lg cursor-pointer transition-colors ${
                    estaSeleccionada
                      ? "border-forest bg-forest/5 font-medium"
                      : "border-base-border hover:bg-base-internal"
                  }`}
                >
                  <input
                    type="radio"
                    name={`pregunta-${preguntaActiva.id}`}
                    checked={estaSeleccionada}
                    onChange={() => seleccionarOpcion(opcion.score)}
                    className="w-5 h-5 mr-4 accent-forest"
                  />
                  <span className="text-text-primary">{opcion.label}</span>
                </label>
              );
            })}
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
              disabled={!tieneRespuesta}
              className={`font-bold text-xs flex items-center transition-colors ${
                !tieneRespuesta
                  ? "text-base-border cursor-not-allowed"
                  : "text-forest hover:text-forest-dark"
              }`}
            >
              {esUltimaPregunta ? "Ver resultados →" : "Siguiente →"}
            </button>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
}

export default function CuestionarioPage() {
  return (
    <RequireAuth>
      <Cuestionario />
    </RequireAuth>
  );
}
