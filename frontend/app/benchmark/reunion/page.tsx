"use client";
import React, { useState } from "react";
import Footer from "@/components/Footer";
import Calendario from "@/components/Calendario";
import Header from "@/components/Header";


export default function AgendarReunionPage() {
  const [selectedDay, setSelectedDay] = useState<string | null>(null);
  const [selectedTime, setSelectedTime] = useState<string | null>(null);
  const [nombre, setNombre] = useState("");
  const [email, setEmail] = useState("");
  const [mensaje, setMensaje] = useState("");

  const timeSlots = ["09:00", "10:30", "13:00", "15:30", "17:00"];

  const getDayName = (dayNumber: string | null) => {
    if (!dayNumber) return "";
    const date = new Date(2026, 6, parseInt(dayNumber));
    const dayName = date.toLocaleDateString("es-AR", { weekday: "short" }).replace(".", "");
    return dayName.charAt(0).toUpperCase() + dayName.slice(1);
  };

  const isFormValid = selectedTime !== null && nombre.trim() !== "" && email.trim() !== "";

  return (
    <div className="min-h-screen bg-base-natural flex flex-col font-display relative overflow-hidden">
      
      <Header subtitle="/ Agendar una Reunión" />

      <div className="w-full bg-forest-light py-5 px-4 text-center text-[14px] sm:text-sm text-text-primary z-20 relative">
        Reunión para revisar el resultado de <span className="font-bold">gerencia@northbridge.com</span> – Nivel: <span className="font-bold">Gestionado</span>, <span className="text-gold-dark">US$ 48.200</span> anuales en oportunidad.
      </div>

      <div className="hidden lg:block absolute left-[5%] top-[55%] -translate-y-1/2 opacity-80 pointer-events-none z-0">
        <img src="/assets/backgrounds/vector.svg" alt="Fondo" className="w-[387.27px] h-[433.97px]" />
      </div>

      <main className="flex-grow flex flex-col items-center px-4 py-10 z-10 relative w-full max-w-4xl mx-auto">
        
        <div className="w-full mb-6">
          <h2 className="text-xl font-bold text-text-primary">Elegí una fecha</h2>
        </div>

        <Calendario 
          selectedDay={selectedDay} 
          onSelectDay={(day) => {
            setSelectedDay(day);
            setSelectedTime(null);
          }} 
        />

        {selectedDay && (
          <div className="w-full grid grid-cols-1 md:grid-cols-2 gap-6">
            
            <div className="bg-white rounded-md shadow-sm border border-base-border p-6 sm:p-8">
              <h3 className="text-sm font-bold text-text-primary mb-6">
                Horarios disponibles - {getDayName(selectedDay)} {selectedDay} Jul
              </h3>
              <div className="flex flex-col gap-3">
                {timeSlots.map((time, index) => {
                  const isSelected = selectedTime === time;
                  return (
                    <button 
                      key={index} 
                      onClick={() => setSelectedTime(time)}
                      className={`w-full text-left px-4 py-3 rounded text-sm transition-colors border ${
                        isSelected 
                          ? 'bg-base-internal border-forest-dark text-text-primary' 
                          : 'bg-base-internal border-base-border text-text-primary hover:border-forest-dark'
                      }`}
                    >
                      {time}
                    </button>
                  );
                })}
              </div>
            </div>

            <div className="bg-white rounded-md shadow-sm border border-base-border p-6 sm:p-8">
              <h3 className="text-sm font-bold text-text-primary mb-6">Confirmá tus datos</h3>
              <form className="flex flex-col gap-4" onSubmit={(e) => e.preventDefault()}>
                <div>
                  <label className="block text-xs text-text-secondary mb-1.5">Nombre</label>
                  <input 
                    type="text" 
                    placeholder="Tu nombre" 
                    value={nombre}
                    onChange={(e) => setNombre(e.target.value)}
                    className="w-full px-3 py-2.5 bg-base-internal border border-base-border rounded text-sm text-text-primary focus:outline-none focus:border-forest-dark"
                  />
                </div>
                <div>
                  <label className="block text-xs text-text-secondary mb-1.5">Email</label>
                  <input 
                    type="email" 
                    placeholder="nombre@ejemplo.com" 
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full px-3 py-2.5 bg-base-internal border border-base-border rounded text-sm text-text-primary focus:outline-none focus:border-forest-dark"
                  />
                </div>
                <div>
                  <label className="block text-xs text-text-secondary mb-1.5">Mensaje (opcional)</label>
                  <textarea 
                    placeholder="Algo que quieras contarnos" 
                    rows={3}
                    value={mensaje}
                    onChange={(e) => setMensaje(e.target.value)}
                    className="w-full px-3 py-2.5 bg-base-internal border border-base-border rounded text-sm text-text-primary focus:outline-none focus:border-forest-dark resize-none"
                  ></textarea>
                </div>
                <button 
                  type="submit" 
                  disabled={!isFormValid}
                  className={`w-full mt-2 py-3 font-medium text-sm rounded transition-colors ${
                    isFormValid 
                      ? 'bg-[#C9A227] text-white hover:opacity-90 cursor-pointer' 
                      : 'bg-gray-200 text-gray-400 cursor-not-allowed'
                  }`}
                >
                  Confirmar reunión
                </button>
              </form>
            </div>

          </div>
        )}
      </main>

      <Footer />
    </div>
  );
}