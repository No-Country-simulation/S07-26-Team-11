/**
 * Puente entre /benchmark/cuestionario y /benchmark/resultados: las respuestas viajan por
 * sessionStorage, no por la URL ni por el backend (no hay persistencia server-side del
 * cuestionario en este MVP — ver aux/decisiones.md).
 */
export const BENCHMARK_ANSWERS_KEY = "capacia-benchmark-answers";
