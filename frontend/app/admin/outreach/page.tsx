"use client";

import { useState, type FormEvent } from "react";
import Sidebar from "@/components/Sidebar";

type InvitationStatus = "Benchmark completo" | "Abierto" | "Enviado";

type Invitation = {
  id: number;
  email: string;
  date: string;
  status: InvitationStatus;
};

const initialInvitations: Invitation[] = [
  {
    id: 1,
    email: "contacto@solvex.com",
    date: "29 jul 2026",
    status: "Benchmark completo",
  },
  {
    id: 2,
    email: "cto@brindlecolo.com",
    date: "28 jul 2026",
    status: "Benchmark completo",
  },
  {
    id: 3,
    email: "ops@velarion.io",
    date: "25 jul 2026",
    status: "Abierto",
  },
  {
    id: 4,
    email: "ops.2@veltrix.io",
    date: "24 jul 2026",
    status: "Abierto",
  },
  {
    id: 5,
    email: "procurement@haldorix.com",
    date: "23 jul 2026",
    status: "Enviado",
  },
];

const statusStyles: Record<InvitationStatus, string> = {
  "Benchmark completo": "bg-[#DDECE4] text-[#1D6E52]",
  Abierto: "bg-[#FDF3D8] text-[#A07800]",
  Enviado: "bg-neutral-200 text-neutral-500",
};

export default function OutreachPage() {
  const [email, setEmail] = useState("");
  const [invitations, setInvitations] =
    useState<Invitation[]>(initialInvitations);
  const [message, setMessage] = useState("");

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const normalizedEmail = email.trim().toLowerCase();
    if (!normalizedEmail) return;

    setInvitations((current) => [
      {
        id: Date.now(),
        email: normalizedEmail,
        date: new Intl.DateTimeFormat("es", {
          day: "2-digit",
          month: "short",
          year: "numeric",
        })
          .format(new Date())
          .replace(" de ", " ")
          .replace(" de ", " "),
        status: "Enviado",
      },
      ...current,
    ]);
    setEmail("");
    setMessage(`Invitación enviada a ${normalizedEmail}`);
  }

  const metrics = [
    {
      label: "Invitaciones enviadas",
      value: String(64 + invitations.length - initialInvitations.length),
    },
    { label: "Tasa de apertura", value: "47%" },
    { label: "Conversión a Benchmark completo", value: "22%" },
  ];

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
            Outreach
          </h1>

          <section
            aria-label="Métricas de outreach"
            className="grid grid-cols-1 gap-4 sm:grid-cols-3"
          >
            {metrics.map((metric) => (
              <article
                key={metric.label}
                className="rounded-xl border border-neutral-200/60 bg-white p-5 shadow-sm"
              >
                <p className="text-xs font-medium text-neutral-500">
                  {metric.label}
                </p>
                <p className="mt-2 text-2xl font-bold text-neutral-900">
                  {metric.value}
                </p>
              </article>
            ))}
          </section>

          <section>
            <h2 className="text-lg font-bold text-neutral-900 sm:text-xl">
              Nueva invitación
            </h2>

            <form
              onSubmit={handleSubmit}
              className="mt-4 rounded-xl border border-neutral-200/60 bg-white p-5 shadow-sm"
            >
              <label
                htmlFor="recipient-email"
                className="mb-2 block text-xs font-medium text-neutral-700"
              >
                Email del destinatario
              </label>
              <div className="flex flex-col gap-3 sm:flex-row">
                <input
                  id="recipient-email"
                  type="email"
                  required
                  value={email}
                  onChange={(event) => {
                    setEmail(event.target.value);
                    setMessage("");
                  }}
                  placeholder="ops@cordillerahost.com"
                  className="h-11 min-w-0 flex-1 rounded-md border border-neutral-300 bg-[#F7F7F4] px-4 text-sm text-neutral-800 outline-none transition-colors placeholder:text-neutral-400 focus:border-[#183327] focus:ring-2 focus:ring-[#183327]/10"
                />
                <button
                  type="submit"
                  className="h-11 shrink-0 rounded-md bg-[#0F291E] px-6 text-sm font-semibold text-white transition-colors hover:bg-[#183D2E] focus:outline-none focus:ring-2 focus:ring-[#183327] focus:ring-offset-2"
                >
                  Enviar nueva invitación
                </button>
              </div>
              <p className="mt-2 min-h-4 text-xs text-[#1D6E52]" role="status">
                {message}
              </p>
            </form>
          </section>

          <section className="pb-8">
            <h2 className="text-lg font-bold text-neutral-900 sm:text-xl">
              Invitaciones enviadas
            </h2>

            <div className="mt-4 overflow-hidden rounded-xl border border-neutral-200/60 bg-white shadow-sm">
              <div className="overflow-x-auto">
                <table className="w-full min-w-[680px] text-left text-sm">
                  <thead className="border-b border-neutral-200 bg-[#F0F0EE] text-xs font-bold text-neutral-700">
                    <tr>
                      <th className="px-6 py-4">Email</th>
                      <th className="px-6 py-4">Fecha de envío</th>
                      <th className="px-6 py-4">Estado</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-neutral-100 text-neutral-600">
                    {invitations.map((invitation) => (
                      <tr
                        key={invitation.id}
                        className="transition-colors hover:bg-neutral-50/60"
                      >
                        <td className="px-6 py-3.5 font-medium text-neutral-700">
                          {invitation.email}
                        </td>
                        <td className="px-6 py-3.5 text-neutral-500">
                          {invitation.date}
                        </td>
                        <td className="px-6 py-3.5">
                          <span
                            className={`inline-flex rounded-full px-3 py-1 text-xs font-medium ${statusStyles[invitation.status]}`}
                          >
                            {invitation.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </section>
        </div>
      </main>
    </div>
  );
}
