"use client";

import { FormEvent, useEffect, useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import Footer from "@/components/Footer";
import Header from "@/components/Header";
import type { CalculatorEstimate } from "../calculatorUse";

type StoredValues = {
  installedCapacity: string;
  usedCapacity: string;
  electricityRate: string;
  racks: string;
};

const referenceValues: StoredValues = {
  installedCapacity: "2000",
  usedCapacity: "760",
  electricityRate: "0.12",
  racks: "48",
};

const numberFormatter = new Intl.NumberFormat("en-US", {
  maximumFractionDigits: 0,
});

export default function PartialResultPage() {
  const router = useRouter();
  const [values, setValues] = useState(referenceValues);
  const [estimate, setEstimate] = useState<CalculatorEstimate | null>(null);
  const [email, setEmail] = useState("");

  useEffect(() => {
    const stored = sessionStorage.getItem("capacity-calculator");
    if (!stored) return;

    try {
      setValues(JSON.parse(stored) as StoredValues);
    } catch {
      sessionStorage.removeItem("capacity-calculator");
    }

    const storedEstimate = sessionStorage.getItem("capacity-calculator-estimate");
    if (!storedEstimate) return;

    try {
      setEstimate(JSON.parse(storedEstimate) as CalculatorEstimate);
    } catch {
      sessionStorage.removeItem("capacity-calculator-estimate");
    }
  }, []);

  const result = useMemo(() => {
    const installed = Math.max(0, Number(values.installedCapacity) || 0);
    const used = Math.min(installed, Math.max(0, Number(values.usedCapacity) || 0));
    const idleCapacity = installed - used;
    const usedPercentage = installed > 0 ? (used / installed) * 100 : 0;

    const idleCapacityKpi = estimate?.kpis.find(
      (kpi) => kpi.code === "IDLE_CAPACITY_KW",
    );
    const idleRatioKpi = estimate?.kpis.find(
      (kpi) => kpi.code === "IDLE_CAPACITY_RATIO",
    );
    const idlePercentage = idleRatioKpi ? idleRatioKpi.value * 100 : 100 - usedPercentage;

    return {
      idleCapacity: idleCapacityKpi?.value ?? idleCapacity,
      usedPercentage: 100 - idlePercentage,
      idlePercentage,
    };
  }, [estimate, values]);

  const submitEmail = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!email.trim()) return;

    sessionStorage.setItem("capacity-calculator-email", email.trim());
    router.push("/calculadora/resultado-completo");
  };

  return (
    <div className="flex min-h-screen flex-col bg-base-natural font-display text-text-primary">
      <Header subtitle="/ Resultado Parcial" />

      <main className="flex flex-1 items-center px-4 py-12 sm:px-8 lg:py-16">
        <div className="mx-auto w-full max-w-[710px]">
          <section className="rounded-md border border-base-border bg-white px-6 py-10 text-center shadow-[0_1px_3px_rgba(10,46,34,0.04)] sm:px-10">
            <p className="text-xs font-medium uppercase tracking-wide text-gold-dark">
              Tu resultado
            </p>
            <h1 className="mt-3 text-[42px] font-medium leading-none text-gold sm:text-[48px]">
              {numberFormatter.format(result.idleCapacity)} kW
            </h1>
            <p className="mt-1 text-xs text-text-primary">subutilizados</p>

            <div
              className="mx-auto mt-7 grid size-[230px] place-items-center rounded-full"
              style={{
                background: `conic-gradient(var(--color-gold) ${result.idlePercentage * 3.6}deg, var(--color-forest-light) 0deg)`,
              }}
              role="img"
              aria-label={`${result.idlePercentage.toFixed(0)} por ciento subutilizado`}
            >
              <div className="grid size-[132px] place-items-center rounded-full bg-forest-dark text-[36px] text-white">
                {result.idlePercentage.toFixed(0)}%
              </div>
            </div>

            <div className="mx-auto mt-8 grid max-w-[440px] grid-cols-2 gap-8 text-[10px] text-text-secondary">
              <p className="flex items-center justify-center gap-1.5">
                <span className="size-2.5 bg-forest-light" />
                Utilizada - {result.usedPercentage.toFixed(0)}%
              </p>
              <p className="flex items-center justify-center gap-1.5">
                <span className="size-2.5 bg-gold" />
                Subutilizada - {result.idlePercentage.toFixed(0)}%
              </p>
            </div>

            <p className="mx-auto mt-9 max-w-[430px] text-xs leading-5 text-text-secondary">
              Este es tu resultado parcial. Desbloqueá el resultado
              <br className="hidden sm:block" /> completo con el costo estimado en dólares.
            </p>
          </section>

          <form onSubmit={submitEmail} className="mt-6 rounded-md bg-forest-dark px-7 py-6 text-left text-white">
            <label htmlFor="result-email" className="block text-xs font-medium">
              Tu email
            </label>
            <input
              id="result-email"
              type="email"
              required
              placeholder="nombre@ejemplo.com"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              className="mt-2 h-11 w-full rounded border border-base-border bg-base-natural px-3 text-xs text-text-primary outline-none placeholder:text-text-secondary focus:border-gold focus:ring-1 focus:ring-gold"
            />
            <button
              type="submit"
              className="mt-3 h-12 w-full rounded bg-gold px-5 text-sm font-medium text-forest-dark transition-colors hover:bg-gold-dark focus:outline-none focus:ring-2 focus:ring-gold focus:ring-offset-2 focus:ring-offset-forest-dark"
            >
              Desbloquear resultado completo
            </button>
            <p className="mt-3 text-center text-[10px] text-white/80">
              Sin spam. Solo tu resultado
            </p>
          </form>
        </div>
      </main>

      <Footer />
    </div>
  );
}
