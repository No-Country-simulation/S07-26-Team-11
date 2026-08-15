"use client";

import { useCallback, useEffect, useState } from "react";
import {
  apiBaseUrl,
  healthApi,
  type DbStatusResponse,
  type HealthProbe,
  type PingResponse,
} from "@/lib/api";

type Status = "loading" | "ok" | "error";

function statusOf(probe: HealthProbe<unknown> | null): Status {
  if (!probe) return "loading";
  return probe.ok ? "ok" : "error";
}

function Badge({ status }: { status: Status }) {
  const styles: Record<Status, string> = {
    loading: "bg-base-internal text-text-secondary",
    ok: "bg-forest-soft text-status-success",
    error: "bg-gold/15 text-gold-dark",
  };
  const labels: Record<Status, string> = {
    loading: "Verificando",
    ok: "Conectado",
    error: "Sin conexión",
  };
  const dots: Record<Status, string> = {
    loading: "bg-text-secondary",
    ok: "bg-status-success",
    error: "bg-gold-dark",
  };

  return (
    <span
      className={`inline-flex h-7 items-center gap-2 rounded-[100px] px-[14px] py-1.5 font-display text-xs font-semibold leading-none ${styles[status]}`}
    >
      <span
        className={`size-2 rounded-full ${dots[status]} ${
          status === "loading" ? "animate-pulse" : ""
        }`}
      />
      {labels[status]}
    </span>
  );
}

/** Una fila de dato. Se omite sola cuando no hay valor que mostrar. */
function Detail({ label, value }: { label: string; value?: string | number | null }) {
  if (value === undefined || value === null || value === "") return null;

  return (
    <div className="flex items-baseline justify-between gap-4 border-b border-base-border py-2 last:border-b-0">
      <dt className="text-sm text-text-secondary">{label}</dt>
      <dd className="text-right text-sm font-semibold text-text-primary">{value}</dd>
    </div>
  );
}

function Card({
  title,
  description,
  endpoint,
  probe,
  children,
}: {
  title: string;
  description: string;
  endpoint: string;
  probe: HealthProbe<unknown> | null;
  children?: React.ReactNode;
}) {
  const status = statusOf(probe);

  return (
    <article className="rounded-2xl border border-base-border bg-white p-6 shadow-sm sm:p-8">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <h2 className="text-xl font-semibold text-text-primary">{title}</h2>
          <p className="mt-1 text-sm leading-6 text-text-secondary">{description}</p>
        </div>
        <Badge status={status} />
      </div>

      <p className="mt-4 break-all font-mono text-xs text-text-secondary">
        GET {apiBaseUrl}
        {endpoint}
      </p>

      <dl className="mt-4">
        <Detail
          label="Código HTTP"
          value={probe ? (probe.httpStatus ?? "sin respuesta") : null}
        />
        <Detail label="Tiempo de ida y vuelta" value={probe ? `${probe.latencyMs} ms` : null} />
        {children}
        <Detail label="Error" value={probe?.error} />
      </dl>
    </article>
  );
}

export default function ApiStatusPanel() {
  const [ping, setPing] = useState<HealthProbe<PingResponse> | null>(null);
  const [db, setDb] = useState<HealthProbe<DbStatusResponse> | null>(null);
  const [checking, setChecking] = useState(true);
  const [lastChecked, setLastChecked] = useState<string | null>(null);

  const check = useCallback(async () => {
    setChecking(true);
    setPing(null);
    setDb(null);

    const [pingResult, dbResult] = await Promise.all([
      healthApi.ping(),
      healthApi.dbStatus(),
    ]);

    setPing(pingResult);
    setDb(dbResult);
    setLastChecked(new Date().toLocaleTimeString("es-AR"));
    setChecking(false);
  }, []);

  useEffect(() => {
    void check();
  }, [check]);

  const pool = db?.body?.pool;

  return (
    <div className="space-y-6">
      <Card
        title="API"
        description="El proceso del backend responde peticiones."
        endpoint="/public/ping"
        probe={ping}
      >
        <Detail label="Estado" value={ping?.body?.status} />
        <Detail label="Servicio" value={ping?.body?.service} />
        <Detail label="Marca de tiempo" value={ping?.body?.timestamp} />
      </Card>

      <Card
        title="Base de datos"
        description="La API abre una conexión real del pool y ejecuta una consulta."
        endpoint="/public/db-status"
        probe={db}
      >
        <Detail label="Estado" value={db?.body?.status} />
        <Detail
          label="Latencia de la consulta"
          value={db?.body?.latencyMs !== undefined ? `${db.body.latencyMs} ms` : null}
        />
        <Detail label="Motor" value={db?.body?.database} />
        <Detail label="Esquema" value={db?.body?.schema} />
        <Detail label="SQLState" value={db?.body?.sqlState} />
        <Detail
          label="Pool de conexiones"
          value={
            pool
              ? `${pool.active} activas · ${pool.idle} libres · ${pool.total}/${pool.max} totales`
              : null
          }
        />
        <Detail label="Marca de tiempo" value={db?.body?.timestamp} />
      </Card>

      <div className="flex flex-wrap items-center gap-4">
        <button
          type="button"
          onClick={() => void check()}
          disabled={checking}
          className="inline-flex h-12 items-center justify-center gap-2.5 rounded bg-forest-dark px-6 py-3 text-sm font-semibold text-white shadow-sm hover:bg-forest focus:outline-none focus:ring-2 focus:ring-forest focus:ring-offset-2 focus:ring-offset-base-natural disabled:cursor-not-allowed disabled:opacity-60"
        >
          {checking ? "Verificando…" : "Volver a verificar"}
        </button>

        {lastChecked && !checking && (
          <p className="text-xs text-text-secondary">
            Última verificación: {lastChecked}
          </p>
        )}
      </div>
    </div>
  );
}
