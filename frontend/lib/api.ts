/**
 * Cliente HTTP de la API.
 *
 * Contrato de referencia: docs/API.md
 * Los TIPOS no se escriben a mano: se generan desde el OpenAPI del backend con
 *   npm run types:api
 * Asi la desincronizacion entre frontend y backend se vuelve imposible.
 */

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080/api/v1";

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
