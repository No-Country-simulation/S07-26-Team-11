"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import Footer from "@/components/Footer";
import Header from "@/components/Header";

type StoredValues = {
  installedCapacity: string;
  usedCapacity: string;
  electricityRate: string;
  racks: string;
};

const referenceValues = {
  installed: 480,
  used: 338,
  rate: 48200 / (142 * 8760),
  racks: 48,
  forcedPercentage: 62,
};

const formatter = new Intl.NumberFormat("es-AR", {
  maximumFractionDigits: 0,
});

export default function CompleteResultPage() {
  const [storedValues, setStoredValues] = useState<StoredValues | null>(null);

  useEffect(() => {
    const stored = sessionStorage.getItem("capacity-calculator");
    if (!stored) return;

    try {
      setStoredValues(JSON.parse(stored) as StoredValues);
    } catch {
      sessionStorage.removeItem("capacity-calculator");
    }
  }, []);

  const result = useMemo(() => {
    if (!storedValues) {
      const idle = referenceValues.installed - referenceValues.used;
      const annualCost = idle * 8760 * referenceValues.rate;

      return {
        installed: referenceValues.installed,
        idle,
        usedPercentage: 38,
        idlePercentage: referenceValues.forcedPercentage,
        annualCost,
        monthlyCost: annualCost / 12,
        costPerRack: annualCost / referenceValues.racks,
      };
    }

    const installed = Math.max(0, Number(storedValues.installedCapacity) || 0);
    const used = Math.min(installed, Math.max(0, Number(storedValues.usedCapacity) || 0));
    const rate = Math.max(0, Number(storedValues.electricityRate) || 0);
    const racks = Math.max(1, Number(storedValues.racks) || 1);
    const idle = installed - used;
    const usedPercentage = installed > 0 ? (used / installed) * 100 : 0;
    const idlePercentage = 100 - usedPercentage;
    const annualCost = idle * 8760 * rate;

    return {
      installed,
      idle,
      usedPercentage,
      idlePercentage,
      annualCost,
      monthlyCost: annualCost / 12,
      costPerRack: annualCost / racks,
    };
  }, [storedValues]);

  return (
    <div className="flex min-h-screen flex-col bg-base-natural font-display text-text-primary">
      <Header subtitle="/ Resultado Completo" />

      <main className="relative flex flex-1 overflow-hidden px-4 py-12 sm:px-8 lg:py-16">
        <img
          src="/assets/backgrounds/vector.svg"
          alt=""
          aria-hidden="true"
          className="pointer-events-none absolute -left-[215px] top-1/2 hidden h-[520px] w-[470px] -translate-y-1/2 opacity-55 md:block"
        />

        <div className="relative mx-auto w-full max-w-[690px] text-center">
          <p className="text-xs font-medium uppercase tracking-wide text-gold-dark">
            Costo anual desperdiciado
          </p>
          <h1 className="mt-3 text-[42px] font-semibold leading-none text-gold sm:text-[48px]">
            US$ {formatter.format(result.annualCost)}
          </h1>
          <p className="mt-2 text-xs text-text-secondary">
            {formatter.format(result.idle)} kW subutilizados sobre{" "}
            {formatter.format(result.installed)} kW instalados
          </p>

          <section className="mt-7 rounded-md bg-forest-dark px-7 py-9 text-left text-white shadow-sm sm:px-10 sm:py-10">
            <h2 className="text-xs font-medium uppercase tracking-wide text-white/90">
              Desglose del resultado
            </h2>

            <div
              className="mx-auto mt-7 grid size-[230px] place-items-center rounded-full"
              style={{
                background: `conic-gradient(var(--color-gold) ${result.idlePercentage * 3.6}deg, #dce7e2 0deg)`,
              }}
              role="img"
              aria-label={`${result.idlePercentage.toFixed(0)} por ciento subutilizado`}
            >
              <div className="grid size-[132px] place-items-center rounded-full bg-forest-dark text-[36px] text-white">
                {result.idlePercentage.toFixed(0)}%
              </div>
            </div>

            <div className="mx-auto mt-8 grid max-w-[390px] grid-cols-2 gap-8 text-[10px] text-white/75">
              <p className="flex items-center justify-center gap-1.5">
                <span className="size-2.5 bg-[#dce7e2]" />
                Utilizada - {result.usedPercentage.toFixed(0)}%
              </p>
              <p className="flex items-center justify-center gap-1.5">
                <span className="size-2.5 bg-gold" />
                Subutilizada - {result.idlePercentage.toFixed(0)}%
              </p>
            </div>

            <div className="mt-9 border-t border-white/35 pt-7">
              <dl className="grid grid-cols-2 gap-x-10 gap-y-7 text-xs">
                <div>
                  <dt className="text-white/70">Costo mensual estimado</dt>
                  <dd className="mt-1 font-semibold">US$ {formatter.format(result.monthlyCost)}</dd>
                </div>
                <div>
                  <dt className="text-white/70">Costo por rack / año</dt>
                  <dd className="mt-1 font-semibold">US$ {formatter.format(result.costPerRack)}</dd>
                </div>
                <div>
                  <dt className="text-white/70">Proyección a 3 años</dt>
                  <dd className="mt-1 font-semibold">US$ {formatter.format(result.annualCost * 3)}</dd>
                </div>
                <div>
                  <dt className="text-white/70">Capacidad instalada</dt>
                  <dd className="mt-1 font-semibold">{formatter.format(result.installed)} kW</dd>
                </div>
              </dl>
            </div>
          </section>

          <section className="mt-6 rounded-md border border-gold bg-[#f7f1df] px-6 py-6">
            <p className="text-xs text-text-secondary">
              Completá el Maturity Benchmark para ver cómo te comparás con la industria.
            </p>
            <Link
              href="/benchmark"
              className="mx-auto mt-4 inline-flex h-11 items-center justify-center gap-4 rounded border border-forest px-7 text-sm font-medium text-forest-dark transition-colors hover:bg-forest hover:text-white focus:outline-none focus:ring-2 focus:ring-forest focus:ring-offset-2"
            >
              Ir al Maturity Benchmark <span aria-hidden="true">↗</span>
            </Link>
          </section>
        </div>
      </main>

      <Footer />
    </div>
  );
}
