/**
 * Calculo del resultado del Maturity Benchmark a partir de las respuestas.
 *
 * Todo el calculo es client-side: no hay tablas de benchmark_responses/answers de por
 * medio (esas existen en la base pero sin ningun endpoint que las use — ver
 * aux/decisiones.md). Para el MVP alcanza con lo que ya sale de responder el cuestionario;
 * si mas adelante se conecta el sistema formal, este archivo es lo que se reemplaza.
 */

import { BENCHMARK_QUESTIONS, DIMENSIONS, type DimensionCode } from "./benchmarkQuestions";

export type BenchmarkAnswers = Record<string, number>; // questionId -> score elegido

/**
 * Umbral y nombre de cada nivel de madurez. No estan especificados en el diseno (que solo
 * muestra un ejemplo, "Gestionado" en score 54): es una escala propia de 4 niveles para
 * este MVP, ajustable sin tocar el resto del calculo.
 */
const MATURITY_LEVELS: Array<{ min: number; label: string }> = [
  { min: 75, label: "Optimizado" },
  { min: 50, label: "Gestionado" },
  { min: 25, label: "Reactivo" },
  { min: 0, label: "Inicial" },
];

/** Una recomendacion fija por dimension. Texto real donde el diseño lo mostraba. */
const RECOMMENDATION_BY_DIMENSION: Record<DimensionCode, string> = {
  VISIBILIDAD:
    "Implementá monitoreo por carga de trabajo para reducir el punto ciego de visibilidad.",
  EFICIENCIA: "Automatizá el apagado programado en ambientes no productivos.",
  GOBERNANZA: "Asigná ownership formal a cada carga para sostener la gobernanza en el tiempo.",
  AUTOMATIZACION: "Establecé un proceso de baja de recursos ociosos con revisión trimestral.",
};

/** Score de referencia de la industria, fijo: no se deriva de esta sesión. */
export const INDUSTRY_REFERENCE_SCORES: Array<{ label: string; value: number }> = [
  { label: "Hyperscale", value: 71 },
  { label: "Colocation", value: 58 },
  { label: "Enterprise (promedio)", value: 46 },
];

export interface DimensionScore {
  code: DimensionCode;
  label: string;
  /** Promedio 0-100 de las respuestas de esa dimension. */
  score: number;
}

export interface BenchmarkResult {
  /** Promedio de las 8 respuestas, 0-100, redondeado. */
  globalScore: number;
  maturityLevel: string;
  dimensionScores: DimensionScore[];
  /** Las 4 recomendaciones, de la dimension mas debil a la mas fuerte. */
  recommendations: string[];
}

/** true si respondio las 8 preguntas. Antes de eso no tiene sentido calcular nada. */
export function isComplete(answers: BenchmarkAnswers): boolean {
  return BENCHMARK_QUESTIONS.every((q) => answers[q.id] !== undefined);
}

export function computeBenchmarkResult(answers: BenchmarkAnswers): BenchmarkResult {
  const dimensionScores: DimensionScore[] = DIMENSIONS.map((dimension) => {
    const questions = BENCHMARK_QUESTIONS.filter((q) => q.dimensionCode === dimension.code);
    const sum = questions.reduce((total, q) => total + (answers[q.id] ?? 0), 0);
    return {
      code: dimension.code,
      label: dimension.stepLabel,
      score: Math.round(sum / questions.length),
    };
  });

  const globalScore = Math.round(
    dimensionScores.reduce((total, d) => total + d.score, 0) / dimensionScores.length,
  );

  const maturityLevel =
    MATURITY_LEVELS.find((level) => globalScore >= level.min)?.label ?? "Inicial";

  const recommendations = [...dimensionScores]
    .sort((a, b) => a.score - b.score)
    .map((d) => RECOMMENDATION_BY_DIMENSION[d.code]);

  return { globalScore, maturityLevel, dimensionScores, recommendations };
}
