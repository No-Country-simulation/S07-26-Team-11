import React from "react";
import ChartCapacidad from "@/components/ChartCapacidad";
import Footer from "@/components/Footer";

export default function ReportePage() {
  return (
    <div className="min-h-screen bg-base-natural text-text-primary">
      <header className="relative z-20 flex w-full items-center gap-6 bg-white px-40 py-5 shadow-sm">
        <img src="/assets/brand/logotipo.svg" alt="Capacia Logo" className="h-9" />
      </header>

      <main className="mx-auto max-w-[800px] px-6 py-16 sm:px-8 sm:py-24">
        
        <section className="mb-16">
          <p className="font-display text-sm font-bold uppercase tracking-widest text-gold-dark">
            Reporte Anual · Julio 2026
          </p>
          <h1 className="font-editorial mb-4 mt-4 text-[40px] font-bold leading-none text-forest-dark sm:text-5xl">
            El estado de la capacidad en <br className="hidden sm:block" />
            Data Centers 2026
          </h1>
          <p className="font-editorial mt-6 text-2xl font-medium text-text-secondary">
            Un análisis de utilización, capacidad fantasma y costo energético en más de 300 instalaciones de América Latina y el mundo.
          </p>

          <div className="mt-4 flex flex-wrap gap-6 font-display text-xs font-normal tracking-wider text-text-secondary sm:gap-12">
            <span>Lectura: 8 min</span>
            <span>300+ instalaciones</span>
            <span>+2.400 MW analizados</span>
          </div>
        </section>

        <section className="mb-20">
          <h2 className="font-editorial flex items-center gap-3 text-3xl font-medium text-forest-dark">
            <span className="text-gold-dark">01</span> Estado del sector
          </h2>
          
          <div className="font-editorial mt-6 space-y-5 text-[22px] font-light leading-relaxed text-text-secondary">
            <p>
              La capacidad instalada global creció 22% interanual, impulsada por cargas de IA 
              y consolidación de nube. Pero la utilización real no acompañó: el promedio de la 
              industria sigue anclado por debajo del 50% de la capacidad encendida.
            </p>
            <p>
              La brecha entre lo que se enciende y lo que se usa se amplió por tercer año 
              consecutivo. En instalaciones enterprise, es común encontrar racks energizados 
              durante meses esperando proyectos que se demoraron o cancelaron.
            </p>
          </div>
          
          <div className="mt-10">
            <ChartCapacidad />
          </div>
        </section>

        <section className="font-editorial mb-20">
          <h2 className="font-editorial flex items-center gap-3 text-3xl font-medium text-forest-dark">
            <span className="text-gold-dark">02</span> El problema de la capacidad fantasma
          </h2>
          
          <p className="mt-6 text-[22px] leading-relaxed text-text-secondary">
            Llamamos capacidad fantasma a la infraestructura que consume energía, 
            refrigeración y espacio sin entregar cómputo útil: servidores zombis, ambientes 
            de prueba olvidados, redundancia sobredimensionada y hardware aprovisionado 
            "por las dudas".
          </p>

          <div className="mt-8 rounded-xl bg-forest-light p-8 sm:p-10">
            <p className="px-4 text-xl font-bold leading-snug text-forest sm:text-2xl">
              En la instalación mediana, 1 de cada 3 kW encendidos no produce 
              ningún resultado de negocio medible.
            </p>
          </div>

          <p className="mt-8 text-[22px] leading-relaxed text-text-secondary">
            El fenómeno es invisible en los dashboards tradicionales porque la métrica 
            dominante disponibilidad premia mantener todo encendido. Sin una medición 
            explícita de utilización por carga, la capacidad fantasma se acumula 
            silenciosamente año tras año.
          </p>
        </section>

        <section className="mb-20">
          <div className="font-editorial">
            <h2 className="font-editorial flex items-center gap-3 text-3xl font-medium text-forest-dark">
              <span className="text-gold-dark">03</span> Benchmarks por tipo de operador
            </h2>
            
            <p className="mt-6 text-[22px] leading-relaxed text-text-secondary">
              La utilización varía fuertemente según el modelo operativo. Los hyperscalers, con 
              orquestación agresiva de cargas, lideran; el segmento enterprise queda más 
              expuesto a la capacidad fantasma.
            </p>
          </div>

          <div className="mt-10 grid gap-6 font-display sm:grid-cols-3">
            
            <div className="rounded-xl border border-transparent bg-white p-6 shadow-sm">
              <h3 className="text-sm font-bold uppercase tracking-wider text-forest-dark">
                Hyperscale
              </h3>
              <p className="mt-1 text-6xl font-bold tracking-tight text-forest">64%</p>
              <p className="mt-1 text-sm text-text-secondary">utilización promedio</p>
              
              <div className="my-5 h-3 w-full rounded-full bg-forest-light">
                <div className="h-full w-[64%] rounded-full bg-forest"></div>
              </div>
              
              <p className="text-sm leading-relaxed text-text-secondary">
                Orquestación automática y bin-packing de cargas mantienen la brecha acotada.
              </p>
            </div>

            <div className="rounded-xl border border-transparent bg-white p-6 shadow-sm">
              <h3 className="text-sm font-bold uppercase tracking-wider text-forest-dark">
                Colocation
              </h3>
              <p className="mt-1 text-6xl font-bold tracking-tight text-forest">51%</p>
              <p className="mt-1 text-sm text-text-secondary">utilización promedio</p>
              
              <div className="my-5 h-3 w-full rounded-full bg-forest-light">
                <div className="h-full w-[51%] rounded-full bg-forest"></div>
              </div>
              
              <p className="text-sm leading-relaxed text-text-secondary">
                La visibilidad termina en el contrato: lo que el cliente enciende queda fuera del control del operador.
              </p>
            </div>

            <div className="rounded-xl border border-gold bg-white p-6 shadow-sm">
              <h3 className="text-sm font-bold uppercase tracking-wider text-gold-dark">
                Enterprise
              </h3>
              <p className="mt-1 text-6xl font-bold tracking-tight text-forest">38%</p>
              <p className="mt-1 text-sm text-text-secondary">utilización promedio</p>
              
              <div className="my-5 h-3 w-full rounded-full bg-gold/20">
                <div className="h-full w-[38%] rounded-full bg-gold-dark"></div>
              </div>
              
              <p className="text-sm leading-relaxed text-text-secondary">
                El segmento con mayor capacidad fantasma — y mayor oportunidad de recupero.
              </p>
            </div>

          </div>
        </section>

        <section className="font-editorial mb-20">
          <h2 className="font-editorial flex items-center gap-3 text-3xl font-medium text-forest-dark">
            <span className="text-gold-dark font-bold">04</span> El costo oculto
          </h2>
          
          <p className="mt-6 text-[22px] leading-relaxed text-text-secondary">
            Traducido a la factura eléctrica, el desperdicio deja de ser abstracto. Cada kW
            fantasma consume energía las 24 horas, arrastra refrigeración proporcional y
            ocupa capacidad contratada que igual se paga.
          </p>

          <div className="mt-8 rounded-xl bg-forest-dark p-8 font-display sm:p-10">
            <h3 className="text-[16px] font-semibold uppercase tracking-wider text-white">
              Costo anual por cada 100 kW de capacidad fantasma
            </h3>
            
            <div className="mt-8 grid gap-4 sm:grid-cols-3">
              <div className="flex flex-col">
                <p className="text-[28px] font-bold tracking-tight text-gold sm:text-4xl">
                  US$ 105.000
                </p>
                <p className="mt-2 text-[16px] leading-relaxed text-white/80">
                  energía directa a US$<br className="hidden sm:block" /> 0,12/kWh
                </p>
              </div>

              <div className="flex flex-col">
                <p className="text-[28px] font-bold tracking-tight text-gold-dark sm:text-4xl">
                  +45%
                </p>
                <p className="mt-2 text-[16px] leading-relaxed text-white/80">
                  adicional en refrigeración<br className="hidden sm:block" /> y pérdidas (PUE 1,45)
                </p>
              </div>

              <div className="flex flex-col">
                <p className="text-[28px] font-bold tracking-tight text-white sm:text-4xl">
                  US$ 152.000
                </p>
                <p className="mt-2 text-[16px] leading-relaxed text-white/80">
                  costo total anual, sin contar<br className="hidden sm:block" /> espacio ni capital inmovilizado
                </p>
              </div>
            </div>
          </div>

          <p className="mt-8 text-[22px] leading-relaxed text-text-secondary">
            Para una instalación enterprise típica de 2 MW con 38% de utilización, la
            capacidad fantasma representa entre US$ 900.000 y US$ 1,4 millones anuales
            en gasto energético sin retorno.
          </p>
        </section>

        <section className="mb-4">
          <div className="flex flex-col items-center justify-center rounded-2xl border border-base-border bg-white px-6 py-16 text-center shadow-sm sm:px-12 sm:py-20">
            
            <p className="font-display text-sm font-bold uppercase tracking-widest text-gold-dark">
              ¿Y tu instalación?
            </p>
            
            <h2 className="font-editorial mt-4 text-3xl font-bold text-forest-dark sm:text-4xl">
              Calculá tu propia capacidad subutilizada
            </h2>
            
            <p className="font-editorial mt-4 max-w-xl text-[22px] leading-relaxed text-text-secondary sm:text-xl">
              Cuatro minutos, sin instalar nada. Obtené tu brecha de utilización y el costo anual estimado.
            </p>
            
            <a 
              href="/calculadora" 
              className="mt-6 flex items-center gap-2 rounded-lg bg-forest px-8 py-3.5 font-display font-medium text-white transition-all hover:bg-forest-dark"
            >
              Ir a la calculadora
              <svg 
                xmlns="http://www.w3.org/2000/svg" 
                fill="none" 
                viewBox="0 0 24 24" 
                strokeWidth={2} 
                stroke="currentColor" 
                className="h-5 w-5"
              >
                <path 
                  strokeLinecap="round" 
                  strokeLinejoin="round" 
                  d="M4.5 19.5l15-15m0 0H8.25m11.25 0v11.25" 
                />
              </svg>
            </a>
            
          </div>
        </section>

      </main>

      <Footer />

    </div>
  );
}