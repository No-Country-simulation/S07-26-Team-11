"use client";

import { useState } from "react";
import Link from "next/link";
import Sidebar from "@/components/Sidebar";

type NotificationKey = "benchmark" | "meeting" | "weeklySummary";

const teamMembers = [
  {
    name: "Sebastian Di Giuseppe",
    email: "sebastian.do.giuseppe@capacia.com",
    role: "Admin",
  },
  {
    name: "Maria Monti",
    email: "maria.monti@capacia.com",
    role: "Miembro",
  },
  {
    name: "Hector Cortez",
    email: "hector.cortez@capacia.com",
    role: "Miembro",
  },
];

const notificationOptions: Array<{
  key: NotificationKey;
  label: string;
}> = [
  {
    key: "benchmark",
    label: "Notificarme por email cuando un lead complete el Benchmark",
  },
  {
    key: "meeting",
    label: "Notificarme cuando se agende una reunión",
  },
  {
    key: "weeklySummary",
    label: "Recibir un resumen semanal de actividad",
  },
];

export default function SettingsPage() {
  const [notifications, setNotifications] = useState<
    Record<NotificationKey, boolean>
  >({
    benchmark: true,
    meeting: true,
    weeklySummary: false,
  });
  const [message, setMessage] = useState("");

  function toggleNotification(key: NotificationKey) {
    setNotifications((current) => ({
      ...current,
      [key]: !current[key],
    }));
  }

  return (
    <div className="relative flex min-h-screen bg-[#F7F7F4] text-[#1E1E1E]">
      <Sidebar />

      <main className="relative flex-1 overflow-y-auto px-6 py-8 sm:px-10 lg:py-10">
        <img
          src="/assets/backgrounds/vector.svg"
          alt=""
          aria-hidden="true"
          className="pointer-events-none fixed -bottom-20 -right-16 z-0 hidden w-[420px] opacity-30 select-none lg:block"
        />

        <div className="relative z-10 w-full space-y-8">
          <h1 className="text-2xl font-bold tracking-tight text-neutral-900 sm:text-3xl">
            Settings
          </h1>

          <section>
            <h2 className="text-lg font-bold text-neutral-900 sm:text-xl">
              Perfil
            </h2>

            <div className="mt-4 flex flex-col gap-4 rounded-xl border border-neutral-200/60 bg-white p-5 shadow-sm sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p className="text-sm font-medium text-neutral-900">
                  Marina Ríos
                </p>
                <p className="mt-1 text-xs text-neutral-500">
                  marina.rios@capacia.com
                </p>
              </div>
              <button
                type="button"
                onClick={() =>
                  setMessage("Te enviamos las instrucciones para cambiar tu contraseña")
                }
                className="h-11 rounded-md border border-[#1D6E52] bg-white px-6 text-sm font-semibold text-[#0F291E] transition-colors hover:bg-[#F0F7F4] focus:outline-none focus:ring-2 focus:ring-[#183327] focus:ring-offset-2"
              >
                Cambiar contraseña
              </button>
            </div>
            <p className="mt-2 min-h-4 text-xs text-[#1D6E52]" role="status">
              {message}
            </p>
          </section>

          <section>
            <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
              <h2 className="text-lg font-bold text-neutral-900 sm:text-xl">
                Equipo
              </h2>
              <Link
                href="/admin/campaigns"
                className="inline-flex h-11 items-center justify-center gap-3 self-start rounded-md bg-[#0F291E] px-6 text-sm font-semibold text-white transition-colors hover:bg-[#183D2E] focus:outline-none focus:ring-2 focus:ring-[#183327] focus:ring-offset-2 sm:self-auto"
              >
                Crear campaña
                <svg
                  className="h-4 w-4 stroke-current"
                  fill="none"
                  viewBox="0 0 24 24"
                  strokeWidth="2"
                  aria-hidden="true"
                >
                  <rect x="4" y="4" width="16" height="16" rx="2" />
                  <path strokeLinecap="round" d="M12 8v8M8 12h8" />
                </svg>
              </Link>
            </div>

            <div className="mt-4 overflow-hidden rounded-xl border border-neutral-200/60 bg-white shadow-sm">
              <ul className="divide-y divide-neutral-200">
                {teamMembers.map((member) => (
                  <li
                    key={member.email}
                    className="flex items-center justify-between gap-5 px-5 py-4"
                  >
                    <div className="min-w-0">
                      <p className="truncate text-sm font-medium text-neutral-900">
                        {member.name}
                      </p>
                      <p className="mt-1 truncate text-xs text-neutral-500">
                        {member.email}
                      </p>
                    </div>
                    <span className="shrink-0 text-sm font-semibold text-neutral-800">
                      {member.role}
                    </span>
                  </li>
                ))}
              </ul>
            </div>
          </section>

          <section className="pb-8">
            <h2 className="text-lg font-bold text-neutral-900 sm:text-xl">
              Notificaciones
            </h2>

            <div className="mt-4 overflow-hidden rounded-xl border border-neutral-200/60 bg-white shadow-sm">
              {notificationOptions.map((option) => {
                const isEnabled = notifications[option.key];

                return (
                  <div
                    key={option.key}
                    className="flex items-center justify-between gap-5 border-b border-neutral-200 px-5 py-4 last:border-b-0"
                  >
                    <span
                      id={`${option.key}-label`}
                      className="text-sm text-neutral-700"
                    >
                      {option.label}
                    </span>
                    <button
                      type="button"
                      role="switch"
                      aria-checked={isEnabled}
                      aria-labelledby={`${option.key}-label`}
                      onClick={() => toggleNotification(option.key)}
                      className={`relative h-6 w-11 shrink-0 rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-[#183327] focus:ring-offset-2 ${
                        isEnabled ? "bg-[#0F3B2C]" : "bg-neutral-300"
                      }`}
                    >
                      <span
                        aria-hidden="true"
                        className={`absolute top-0.5 h-5 w-5 rounded-full bg-white shadow-sm transition-transform ${
                          isEnabled ? "translate-x-5" : "translate-x-0.5"
                        }`}
                      />
                    </button>
                  </div>
                );
              })}
            </div>
          </section>
        </div>
      </main>
    </div>
  );
}
