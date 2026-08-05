"use client";
import React from "react";
import Footer from "@/components/Footer";
import ChartPosicion from "@/components/ChartPosicion"; 
import Recomendaciones from "@/components/Recomendaciones";
import KpiCards from "@/components/KpiCards";import type { Metadata } from "next";
import Header from "@/components/Header";
import { useRouter } from "next/navigation";


export default function BenchmarkResultadosPage() {
  const router = useRouter();
  return (
    <div className="min-h-screen bg-base-natural flex flex-col font-display">
      
      <Header subtitle="/ Resultados" />

      <section className="bg-forest-dark w-full py-16 flex flex-col items-center text-center px-4 relative z-0">
        <p className="text-white/80 uppercase font-semibold mb-3">
          Tu nivel de madurez
        </p>
        <h1 className="text-gold-dark text-[52px] sm:text-5xl font-extrabold mb-2">
          Gestionado
        </h1>
        <p className="text-white/90 text-sm mb-6">Score 54/100</p>
        
        <div className="w-48 h-px bg-white/20 mb-6"></div>
        <p className="text-white/80 text-[14px] mb-1">Costo anual desperdiciado</p>
        <p className="text-white text-[28px] sm:text-3xl font-bold">
          US$ 48.200
        </p>
      </section>

      <main className="flex-grow px-4 py-20">
        <KpiCards />
        <ChartPosicion />
        <Recomendaciones />

        <div className="max-w-3xl mx-auto flex flex-col sm:flex-row gap-4 mt-8">
          <button className="flex-1 bg-white border border-forest text-forest-dark font-semibold text-base py-3 rounded-md hover:bg-base-natural transition-colors">
            Descargar informe PDF
          </button>
          <button 
            onClick={() => router.push("/benchmark/reunion")}
            className="flex-1 bg-gold-dark text-white font-medium text-sm py-3 rounded-md hover:opacity-90 transition-opacity"
          >
            Agendar una reunión
          </button>
        </div>

      </main>
      
      <Footer />
      
    </div>
  );
}