"use client";

import { useCallback, useState } from "react";
import { ApiError, calculatorApi } from "@/lib/api";

export type CalculatorInput = {
  installedCapacityKw: number;
  usedCapacityKw: number;
  electricityRatePerKwh: number;
  rackCount: number;
};

export type CalculatorKpiCode =
  | "IDLE_CAPACITY_KW"
  | "IDLE_CAPACITY_RATIO"
  | "IDLE_CAPACITY_ANNUAL_COST"
  | "IDLE_COST_MONTHLY"
  | "IDLE_COST_3Y_PROJECTION"
  | "IDLE_COST_PER_RACK_ANNUAL";

export type CalculatorEstimate = {
  estimateId: string | null;
  calculationVersion: string;
  createdAt: string;
  unlocked: boolean;
  kpis: Array<{
    code: CalculatorKpiCode;
    label: string;
    value: number;
    unit: "KW" | "RATIO" | "CURRENCY";
  }>;
  lockedKpiCount: number;
};

function getErrorMessage(error: unknown) {
  if (error instanceof ApiError) {
    return error.problem.detail ?? error.problem.title;
  }

  if (error instanceof Error) return error.message;
  return "No se pudo conectar con la calculadora. Intentá nuevamente.";
}

export function useCalculator() {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const estimate = useCallback(async (input: CalculatorInput) => {
    setIsLoading(true);
    setError(null);

    try {
      return await calculatorApi.estimate<CalculatorEstimate>(input);
    } catch (requestError) {
      setError(getErrorMessage(requestError));
      return null;
    } finally {
      setIsLoading(false);
    }
  }, []);

  return { estimate, isLoading, error };
}
