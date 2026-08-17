"use client";

import { useEffect, useState, Suspense } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import Link from "next/link";
import Header from "@/components/Header";
import Footer from "@/components/Footer";
import { leadsApi, ApiError } from "@/lib/api";

type ErrorType = "invalid_link" | "server_error" | null;

interface VerifyResponse {
  accessToken?: string;
  lead?: unknown;
}

function VerifyContent() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const token = searchParams.get("token");

  const [isLoading, setIsLoading] = useState(true);
  const [errorType, setErrorType] = useState<ErrorType>(null);

  useEffect(() => {
    if (isLoading) {
      document.title = "Verificando acceso... | Capacia";
    } else if (errorType === "invalid_link") {
      document.title = "Link inválido | Capacia";
    } else if (errorType === "server_error") {
      document.title = "Error de conexión | Capacia";
    }
  }, [isLoading, errorType]);

  const validarToken = async () => {
    if (!token) {
      console.log("No hay token en la URL");
      setErrorType("invalid_link");
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setErrorType(null);

    try {
      const data = (await leadsApi.verify(token)) as VerifyResponse;

      if (data?.accessToken) {
        localStorage.setItem("accessToken", data.accessToken);
      }
      if (data?.lead) {
        localStorage.setItem("leadInfo", JSON.stringify(data.lead));
      }

      router.push("/benchmark");
    } catch (err) {
      console.error("Error capturado en la verificación:", err);

      if (
        err instanceof ApiError &&
        (err.problem.status === 400 || err.problem.status === 404)
      ) {
        setErrorType("invalid_link");
      } else {
        setErrorType("server_error");
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    validarToken();
  }, [token]);

  return (
    <div className="relative mx-auto flex w-full max-w-[500px] flex-col items-center justify-center rounded-lg border border-base-border/60 bg-white px-6 py-12 text-center shadow-sm sm:px-10">
      {isLoading && (
        <>
          <div className="h-10 w-10 animate-spin rounded-full border-4 border-base-border border-t-forest" />
          <p className="mt-6 text-sm font-medium text-text-secondary">
            Verificando tu acceso...
          </p>
        </>
      )}

      {!isLoading && errorType === "invalid_link" && (
        <>
          <div className="flex h-12 w-12 items-center justify-center rounded-full border border-red-200 text-red-500">
            <span className="text-xl font-medium">!</span>
          </div>

          <h2 className="mt-5 text-lg font-bold text-text-primary">
            Este link de invitación ya no es válido.
          </h2>

          <p className="mt-2 text-xs text-text-secondary max-w-[320px]">
            Puede que haya expirado o ya fue utilizado. Si creés que es un error, contactá a quien te lo compartió.
          </p>

          <Link
            href="/"
            className="mt-6 inline-flex items-center gap-1 text-xs font-semibold text-forest hover:underline"
          >
            Ir a la página principal ↗
          </Link>
        </>
      )}

      {!isLoading && errorType === "server_error" && (
        <>
          <div className="flex h-12 w-12 items-center justify-center rounded-full border border-red-200 text-red-500">
            <span className="text-xl font-medium">!</span>
          </div>

          <h2 className="mt-5 text-lg font-bold text-text-primary">
            No pudimos cargar esto
          </h2>

          <p className="mt-2 text-xs text-text-secondary max-w-[320px]">
            Puede ser un problema temporal de conexión. Probá de nuevo en unos segundos.
          </p>

          <button
            onClick={validarToken}
            className="mt-6 inline-flex items-center gap-1.5 rounded-md bg-forest px-6 py-2.5 text-xs font-medium text-white transition-colors hover:bg-forest/90"
          >
            Reintentar ↗
          </button>
        </>
      )}
    </div>
  );
}

export default function VerifyPage() {
  return (
    <div className="flex min-h-screen flex-col bg-base-natural font-display text-text-primary">
      <Header />

      <main className="relative flex flex-1 items-center justify-center overflow-hidden px-4 py-12 sm:px-8">
        <img
          src="/assets/backgrounds/vector.svg"
          alt=""
          aria-hidden="true"
          className="pointer-events-none absolute -left-[-36px] top-1/2 hidden h-[430px] w-[390px] -translate-y-1/2 opacity-90 md:block"
        />

        <Suspense fallback={<div>Cargando...</div>}>
          <VerifyContent />
        </Suspense>
      </main>

      <Footer />
    </div>
  );
}
