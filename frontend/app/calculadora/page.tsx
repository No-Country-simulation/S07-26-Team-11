"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import Footer from "@/components/Footer";
import Header from "@/components/Header";

type CalculatorValues = {
  installedCapacity: string;
  usedCapacity: string;
  electricityRate: string;
  racks: string;
};

const emptyValues: CalculatorValues = {
  installedCapacity: "",
  usedCapacity: "",
  electricityRate: "",
  racks: "",
};

const fields: Array<{
  id: keyof CalculatorValues;
  label: string;
  placeholder: string;
  hint: string;
  step?: string;
}> = [
  {
    id: "installedCapacity",
    label: "Capacidad instalada (kW)",
    placeholder: "Ej: 2000",
    hint: "Potencia total energizada disponible en el sitio.",
  },
  {
    id: "usedCapacity",
    label: "Capacidad promedio utilizada (kW)",
    placeholder: "Ej: 760",
    hint: "Consumo real promedio de cómputo en los últimos 30 días.",
  },
  {
    id: "electricityRate",
    label: "Tarifa eléctrica local (US$/kWh)",
    placeholder: "Ej: 0.12",
    hint: "Costo promedio pagado por kWh en tu región.",
    step: "0.01",
  },
  {
    id: "racks",
    label: "Cantidad de racks",
    placeholder: "Ej: 48",
    hint: "Usado para estimar el promedio de utilización por rack.",
  },
];

export default function CalculatorPage() {
  const router = useRouter();
  const [values, setValues] = useState(emptyValues);

  const isComplete = Object.values(values).every(
    (value) => value.trim() !== "" && Number(value) > 0,
  );

  const updateValue = (id: keyof CalculatorValues, value: string) => {
    setValues((current) => ({ ...current, [id]: value }));
  };

  const submit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!isComplete) return;

    sessionStorage.setItem("capacity-calculator", JSON.stringify(values));
    router.push("/calculadora/resultado-parcial");
  };

  return (
    <div className="flex min-h-screen flex-col bg-base-natural font-display text-text-primary">
      <Header subtitle="/ Capacity Calculator" />

      <main className="relative flex flex-1 overflow-hidden px-4 py-12 sm:px-8 lg:py-[72px]">
        <img
          src="/assets/backgrounds/vector.svg"
          alt=""
          aria-hidden="true"
          className="pointer-events-none absolute -left-[180px] top-1/2 hidden h-[430px] w-[390px] -translate-y-1/2 opacity-45 md:block"
        />

        <div className="relative mx-auto grid w-full max-w-[1320px] items-start gap-8 lg:grid-cols-[minmax(0,780px)_minmax(340px,470px)] lg:gap-16">
          <form
            onSubmit={submit}
            className="rounded-md border border-base-border/60 bg-white px-7 py-8 shadow-[0_1px_3px_rgba(10,46,34,0.03)] sm:px-10 sm:py-10"
          >
            <h1 className="text-base font-bold text-text-primary">Capacity Calculator</h1>
            <p className="mt-2 text-xs text-text-secondary">
              Completá los datos de tu instalación. El resultado se actualiza en vivo.
            </p>

            <div className="mt-7 space-y-3">
              {fields.map((field) => (
                <label key={field.id} htmlFor={field.id} className="block">
                  <span className="block text-xs font-medium text-text-primary">
                    {field.label}
                  </span>
                  <input
                    id={field.id}
                    type="number"
                    min="0"
                    step={field.step ?? "1"}
                    placeholder={field.placeholder}
                    value={values[field.id]}
                    onChange={(event) => updateValue(field.id, event.target.value)}
                    className="mt-1 h-10 w-full rounded border border-base-border bg-base-natural px-3 text-xs text-text-primary outline-none placeholder:text-text-secondary/70 focus:border-forest focus:ring-1 focus:ring-forest"
                  />
                  <span className="mt-1 block text-[10px] leading-3 text-text-secondary">
                    {field.hint}
                  </span>
                </label>
              ))}
            </div>

            <button
              type="submit"
              disabled={!isComplete}
              className="mt-5 h-11 w-full rounded bg-forest px-5 text-xs font-medium text-white transition-colors hover:bg-forest-dark focus:outline-none focus:ring-2 focus:ring-forest focus:ring-offset-2 disabled:cursor-not-allowed disabled:bg-[#e5e9e6] disabled:text-[#898d8a]"
            >
              Ver mi resultado
            </button>
            <p className="mt-3 text-center text-[10px] text-text-secondary">
              {isComplete
                ? "Los datos están listos para calcular tu resultado."
                : "Completá los 4 campos para habilitar el botón."}
            </p>
          </form>

          <aside className="flex min-h-[460px] flex-col rounded-md bg-forest-dark px-9 py-10 text-white shadow-sm">
            <p className="text-xs font-medium uppercase tracking-wide text-white/90">
              Resultado en vivo
            </p>
            <div className="flex flex-1 flex-col items-center justify-center text-center">
              <div
                className="grid size-44 place-items-center rounded-full bg-[#194b3b]"
                role="img"
                aria-label="Resultado pendiente"
              >
                <div className="size-[124px] rounded-full bg-forest-dark" />
              </div>
              <p className="mt-12 text-xs text-white/80">
                Completá los campos para ver tu resultado
              </p>
            </div>
          </aside>
        </div>
      </main>

      <Footer />
    </div>
  );
}
