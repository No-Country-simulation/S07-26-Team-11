"use client";
import React from "react";

interface CalendarioProps {
  selectedDay: string | null;
  onSelectDay: (day: string) => void;
}

export default function Calendario({ selectedDay, onSelectDay }: CalendarioProps) {
  const daysOfWeek = ["DOM", "LUN", "MAR", "MIE", "JUE", "VIE", "SAB"];
  
  const calendarDays = [
    { day: "", disabled: true }, { day: "", disabled: true }, { day: "1", disabled: true }, 
    { day: "2", disabled: true }, { day: "3", disabled: true }, { day: "4", disabled: true }, 
    { day: "5", disabled: true }, { day: "6", disabled: true }, { day: "7", disabled: true }, 
    { day: "8", disabled: true }, { day: "9", disabled: true }, { day: "10", disabled: true }, 
    { day: "11", disabled: true }, { day: "12", disabled: true }, { day: "13", disabled: true }, 
    { day: "14", disabled: false }, { day: "15", disabled: false }, { day: "16", disabled: false }, 
    { day: "17", disabled: false }, { day: "18", disabled: false }, { day: "19", disabled: true }, 
    { day: "20", disabled: true }, { day: "21", disabled: false }, { day: "22", disabled: false }, 
    { day: "23", disabled: true }, { day: "24", disabled: false }, { day: "25", disabled: false }, 
    { day: "26", disabled: true }, { day: "27", disabled: true }, { day: "28", disabled: false }, 
    { day: "29", disabled: false }, { day: "30", disabled: false }, { day: "31", disabled: false }, 
    { day: "27", disabled: true }, { day: "28", disabled: true },
  ];

  return (
    <div className="w-full bg-white rounded-md shadow-sm border border-base-border p-6 sm:p-8 mb-6 relative z-10">
      
      <div className="flex items-center justify-between mb-8">
        <button className="text-text-secondary hover:text-text-primary">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-5 h-5">
            <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" />
          </svg>
        </button>
        <h3 className="text-base font-medium text-text-primary">Julio 2026</h3>
        <button className="text-text-secondary hover:text-text-primary">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-5 h-5">
            <path strokeLinecap="round" strokeLinejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
          </svg>
        </button>
      </div>

      <div className="grid grid-cols-7 gap-y-6 text-center mb-4">
        {daysOfWeek.map((day, index) => (
          <div key={index} className="text-[10px] sm:text-xs font-medium text-text-primary uppercase">
            {day}
          </div>
        ))}
        
        {calendarDays.map((item, index) => {
          const isActive = item.day === selectedDay;
          return (
            <div key={index} className="flex justify-center items-center">
              <div 
                onClick={() => !item.disabled && item.day !== "" && onSelectDay(item.day)}
                className={`w-8 h-8 flex items-center justify-center rounded-full text-sm 
                  ${isActive ? 'bg-forest-dark text-white font-bold' : ''} 
                  ${item.disabled ? 'text-gray-300' : ''} 
                  ${!item.disabled && !isActive ? 'text-forest-dark font-medium hover:bg-base-natural cursor-pointer transition-colors' : ''}
                `}
              >
                {item.day}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}