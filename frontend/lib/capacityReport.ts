/**
 * Arma el informe completo combinando dos fuentes que hoy son independientes:
 *  - los KPI de capacidad (kW subutilizados, costo anual, % de utilización, costo por
 *    rack) salen de la calculadora ya construida (`POST /public/calculator/estimate`,
 *    con los valores por defecto de industria: no hay un paso previo en este flujo donde
 *    el operador cargue sus propios numeros — ver aux/decisiones.md).
 *  - el nivel de madurez y las recomendaciones salen del Maturity Benchmark
 *    (`lib/benchmarkScoring.ts`), calculado en el cliente.
 *
 * El resultado se guarda una sola vez en sessionStorage: lo arma /benchmark/resultados y
 * lo reusa /benchmark/resultados/descarga al generar el PDF, para no recalcular (y
 * arriesgar numeros distintos entre lo que se muestra en pantalla y lo que se descarga).
 */

import { calculatorApi, type GenerateDocumentInput } from "./api";
import {
  computeBenchmarkResult,
  INDUSTRY_REFERENCE_SCORES,
  type BenchmarkAnswers,
  type DimensionScore,
} from "./benchmarkScoring";
import { formatKw, formatPercent, formatUsd } from "./format";

export const CAPACITY_REPORT_KEY = "capacia-benchmark-report";

export interface CapacityReport {
  maturityLevel: string;
  globalScore: number;
  dimensionScores: DimensionScore[];
  document: GenerateDocumentInput;
}

interface CalculatorKpi {
  code: string;
  value: number;
}

function kpiValue(kpis: CalculatorKpi[], code: string): number {
  return kpis.find((k) => k.code === code)?.value ?? 0;
}

export async function buildCapacityReport(
  answers: BenchmarkAnswers,
  companyName: string,
): Promise<CapacityReport> {
  const { maturityLevel, globalScore, dimensionScores, recommendations } =
    computeBenchmarkResult(answers);

  const defaultsInput = (await calculatorApi.defaults()) as Record<string, unknown>;
  const defaults = await calculatorApi.estimate<{ kpis: CalculatorKpi[] }>(defaultsInput);

  const kwUnderutilized = kpiValue(defaults.kpis, "IDLE_CAPACITY_KW");
  const idleRatio = kpiValue(defaults.kpis, "IDLE_CAPACITY_RATIO");
  const annualCost = kpiValue(defaults.kpis, "IDLE_CAPACITY_ANNUAL_COST");
  const costPerRack = kpiValue(defaults.kpis, "IDLE_COST_PER_RACK_ANNUAL");

  const industryScores = [
    { label: "Tu resultado", value: globalScore, own: true },
    ...INDUSTRY_REFERENCE_SCORES.map((item) => ({ ...item, own: false })),
  ];

  const document: GenerateDocumentInput = {
    metadata: { name: "informe-benchmark" },
    title: `Informe de capacidad — ${companyName}`,
    executiveSummary:
      `Su infraestructura opera hoy con una utilización promedio del ` +
      `${formatPercent(1 - idleRatio)} de la capacidad instalada. La brecha restante ` +
      `representa un gasto energético recurrente sin retorno de negocio, equivalente a ` +
      `${formatUsd(annualCost)} al año. Este informe resume el hallazgo, su comparación ` +
      `con la industria y los pasos recomendados para recuperarlo.`,
    annualCost: formatUsd(annualCost),
    maturityLevel,
    score: `${globalScore} / 100`,
    kwUnderutilized: formatKw(kwUnderutilized),
    utilizationPercent: formatPercent(1 - idleRatio),
    costPerRack: formatUsd(costPerRack),
    industryScores,
    recommendations,
  };

  return { maturityLevel, globalScore, dimensionScores, document };
}
