/**
 * Cliente HTTP de la API.
 *
 * Contrato de referencia: docs/API.md
 * Los TIPOS no se escriben a mano: se generan desde el OpenAPI del backend con
 *   npm run types:api
 * Asi la desincronizacion entre frontend y backend se vuelve imposible.
 */

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080/api/v1";

/** La URL contra la que se esta trabajando. Util para mostrarla en diagnostico. */
export const apiBaseUrl = BASE_URL;

/** Formato de error uniforme de la API (RFC 9457 Problem Details). */
export interface ProblemDetail {
  type: string;
  title: string;
  status: number;
  detail?: string;
  instance?: string;
  traceId?: string;
  errors?: Array<{ field: string; message: string }>;
}

export class ApiError extends Error {
  constructor(public readonly problem: ProblemDetail) {
    super(problem.title);
  }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
    credentials: "include",
  });

  if (!response.ok) {
    const problem = (await response.json().catch(() => ({
      type: "about:blank",
      title: "Error de red",
      status: response.status,
    }))) as ProblemDetail;
    throw new ApiError(problem);
  }

  if (response.status === 204) return undefined as T;
  return (await response.json()) as T;
}

export const api = {
  get: <T>(path: string) => request<T>(path),
  post: <T>(path: string, body?: unknown) =>
    request<T>(path, { method: "POST", body: JSON.stringify(body ?? {}) }),
  patch: <T>(path: string, body?: unknown) =>
    request<T>(path, { method: "PATCH", body: JSON.stringify(body ?? {}) }),
};

/* ------------------------------------------------------------------ */
/* Endpoints. Reflejan docs/API.md. Ampliar a medida que se implementen. */
/* ------------------------------------------------------------------ */

export const calculatorApi = {
  estimate: (input: Record<string, unknown>) =>
    api.post<unknown>("/public/calculator/estimate", input),
  defaults: () => api.get<unknown>("/public/calculator/defaults"),
};

export const leadsApi = {
  capture: (input: { email: string; consent: boolean; source: string }) =>
    api.post<{ message: string }>("/public/leads", input),
  verify: (token: string) => api.post<unknown>("/public/leads/verify", { token }),
};

export const benchmarkApi = {
  instrument: () => api.get<unknown>("/public/benchmark/instrument"),
  start: (instrumentId: string) =>
    api.post<unknown>("/public/benchmark/responses", { instrumentId }),
  saveProgress: (responseId: string, answers: unknown[]) =>
    api.patch<unknown>(`/public/benchmark/responses/${responseId}`, { answers }),
  complete: (responseId: string) =>
    api.post<unknown>(`/public/benchmark/responses/${responseId}/complete`),
};

export const pdfApi = {
  jobStatus: (jobId: string) => api.get<unknown>(`/public/pdf/jobs/${jobId}`),
};

/* ------------------------------------------------------------------ */
/* Diagnostico. No pasa por request(): /db-status responde 503 con un   */
/* cuerpo que igual queremos leer, y estos endpoints son publicos, asi  */
/* que se piden sin credenciales.                                       */
/* ------------------------------------------------------------------ */

/** GET /public/ping */
export interface PingResponse {
  status: string;
  service: string;
  timestamp: string;
}

/** GET /public/db-status. Los detalles solo llegan si DB_STATUS_DETAILS=true. */
export interface DbStatusResponse {
  status: "UP" | "DOWN";
  timestamp: string;
  latencyMs?: number;
  sqlState?: string;
  database?: string;
  schema?: string;
  readOnly?: boolean;
  pool?: {
    name: string;
    active: number;
    idle: number;
    total: number;
    waiting: number;
    max: number;
  };
}

/** Resultado de una sonda: siempre resuelve, nunca lanza. */
export interface HealthProbe<T> {
  ok: boolean;
  /** null si la peticion no llego a salir (backend caido, CORS, DNS). */
  httpStatus: number | null;
  /** Medido en el navegador: incluye red, no solo el trabajo del servidor. */
  latencyMs: number;
  body: T | null;
  error?: string;
}

async function probe<T>(path: string): Promise<HealthProbe<T>> {
  const startedAt = performance.now();

  try {
    const response = await fetch(`${BASE_URL}${path}`, {
      headers: { Accept: "application/json" },
      cache: "no-store",
    });
    const body = (await response.json().catch(() => null)) as T | null;

    return {
      ok: response.ok,
      httpStatus: response.status,
      latencyMs: Math.round(performance.now() - startedAt),
      body,
    };
  } catch (cause) {
    return {
      ok: false,
      httpStatus: null,
      latencyMs: Math.round(performance.now() - startedAt),
      body: null,
      error: cause instanceof Error ? cause.message : "Error de red",
    };
  }
}

export const healthApi = {
  ping: () => probe<PingResponse>("/public/ping"),
  dbStatus: () => probe<DbStatusResponse>("/public/db-status"),
};
