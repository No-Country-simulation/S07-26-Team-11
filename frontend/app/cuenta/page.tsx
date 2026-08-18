"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import Header from "@/components/Header";
import Footer from "@/components/Footer";
import RequireAuth from "@/components/RequireAuth";
import { useAuth } from "@/components/AuthProvider";
import { expiresAt, readAccessToken } from "@/lib/session";

function Row({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex flex-col gap-1 border-b border-base-border/60 py-3 last:border-b-0 sm:flex-row sm:items-center sm:justify-between">
      <span className="text-xs font-semibold uppercase tracking-wide text-text-secondary">
        {label}
      </span>
      <span className="break-all text-sm text-text-primary">{value}</span>
    </div>
  );
}

function AccountDetail() {
  const { user } = useAuth();
  const [expiry, setExpiry] = useState<string>("—");

  // Se lee despues de montar: en el servidor no hay localStorage y renderizar
  // esto durante el SSR daria un desajuste de hidratacion.
  useEffect(() => {
    const token = readAccessToken();
    const expiresOn = token ? expiresAt(token) : null;
    if (expiresOn) {
      setExpiry(new Date(expiresOn).toLocaleString("es-AR"));
    }
  }, []);

  const roles = user?.roles.map((role) => role.replace(/^ROLE_/, "")).join(", ") ?? "—";

  return (
    <div className="mx-auto w-full max-w-[560px] rounded-lg border border-base-border/60 bg-white px-6 py-8 shadow-sm sm:px-10">
      <h1 className="text-xl font-bold text-text-primary">Tu cuenta</h1>
      <p className="mt-2 text-xs text-text-secondary">
        Datos de la sesión abierta en este navegador.
      </p>

      <div className="mt-6">
        <Row label="Email" value={user?.email ?? "—"} />
        <Row label="Rol" value={roles} />
        <Row label="La sesión vence" value={expiry} />
      </div>

      <div className="mt-8 border-t border-base-border/60 pt-6">
        <p className="text-xs font-semibold uppercase tracking-wide text-text-secondary">
          Accesos rápidos
        </p>
        <div className="mt-3 flex flex-col gap-3 sm:flex-row">
          <Link
            href="/benchmark"
            className="flex-1 rounded-md border border-forest px-5 py-3 text-center text-sm font-semibold text-forest-dark transition-colors hover:bg-base-natural"
          >
            Maturity Benchmark
          </Link>
          <Link
            href="/reporte"
            className="flex-1 rounded-md border border-forest px-5 py-3 text-center text-sm font-semibold text-forest-dark transition-colors hover:bg-base-natural"
          >
            Reporte de la industria
          </Link>
        </div>
      </div>
    </div>
  );
}

export default function AccountPage() {
  return (
    <div className="flex min-h-screen flex-col bg-base-natural font-display text-text-primary">
      <Header subtitle="/ Tu cuenta" />

      <main className="flex flex-1 items-start justify-center px-4 py-12 sm:px-8">
        <RequireAuth>
          <AccountDetail />
        </RequireAuth>
      </main>

      <Footer />
    </div>
  );
}
