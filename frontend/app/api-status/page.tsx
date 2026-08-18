import type { Metadata } from "next";
import ApiStatusPanel from "@/components/ApiStatusPanel";
import Footer from "@/components/Footer";
import Header from "@/components/Header";

export const metadata: Metadata = {
  title: "Estado de la conexión | Capacia",
  description: "Verificación de la conexión entre el frontend, la API y la base de datos.",
};

export default function ApiStatusPage() {
  return (
    <div className="min-h-screen bg-base-natural text-text-primary">
      <Header subtitle="Diagnóstico" />

      <main className="mx-auto max-w-[720px] px-6 py-16 sm:px-8 sm:py-24">
        <section className="mb-10">
          <p className="inline-flex h-7 items-center gap-2.5 rounded-[100px] bg-forest-soft px-[14px] py-1.5 font-display text-xs font-semibold leading-none text-forest">
            Diagnóstico interno
          </p>

          <h1 className="mt-6 text-[40px] font-bold leading-none text-forest-dark sm:text-5xl">
            Estado de la conexión
          </h1>

          <p className="mt-6 font-display leading-6 text-text-secondary">
            Esta página consulta los dos endpoints públicos de salud del backend desde el
            navegador. Sirve para distinguir de un vistazo si la API está caída o si está
            arriba pero no llega a la base de datos.
          </p>
        </section>

        <ApiStatusPanel />
      </main>

      <Footer />
    </div>
  );
}
