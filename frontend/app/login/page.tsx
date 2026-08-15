"use client";

import { Suspense, useEffect, useState, type FormEvent } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import Link from "next/link";
import Header from "@/components/Header";
import Footer from "@/components/Footer";
import { useAuth } from "@/components/AuthProvider";
import { ApiError } from "@/lib/api";

/** A donde se manda al usuario si entro directo a /login, sin `?next=`. */
const DEFAULT_DESTINATION = "/cuenta";

/**
 * Traduce el error de la API a algo que el usuario pueda leer.
 *
 * El backend responde 401 con un mensaje deliberadamente generico: no dice si
 * el email existe o si lo que fallo fue la contrasena, para no confirmarle a
 * nadie que una cuenta esta registrada. Aca se respeta esa decision.
 */
function messageFor(error: unknown): string {
  if (!(error instanceof ApiError)) {
    return "No pudimos conectar con el servidor. Probá de nuevo en unos segundos.";
  }

  const { status, errors } = error.problem;

  if (status === 401) return "Email o contraseña incorrectos.";
  if (status === 400 && errors?.length) return errors.map((e) => e.message).join(". ");
  if (status === 400) return "Revisá los datos ingresados.";
  if (status === 429) return "Demasiados intentos. Esperá un momento antes de reintentar.";
  if (status >= 500) return "El servidor no está respondiendo. Probá de nuevo en unos minutos.";

  return error.problem.detail ?? "No pudimos iniciar sesión.";
}

function LoginForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { login, status } = useAuth();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Solo rutas internas: un `next` con http:// o // seria un redirect abierto.
  const rawNext = searchParams.get("next");
  const destination =
    rawNext && rawNext.startsWith("/") && !rawNext.startsWith("//")
      ? rawNext
      : DEFAULT_DESTINATION;

  // Quien ya tiene sesion no necesita ver el formulario.
  useEffect(() => {
    if (status === "authenticated") router.replace(destination);
  }, [status, destination, router]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setIsSubmitting(true);

    try {
      await login(email.trim(), password);
      router.replace(destination);
    } catch (cause) {
      setError(messageFor(cause));
      setPassword("");
      setIsSubmitting(false);
    }
  }

  const isDisabled = isSubmitting || email.trim() === "" || password === "";

  return (
    <div className="relative mx-auto w-full max-w-[440px] rounded-lg border border-base-border/60 bg-white px-6 py-10 shadow-sm sm:px-10">
      <h1 className="text-xl font-bold text-text-primary">Iniciar sesión</h1>
      <p className="mt-2 text-xs text-text-secondary">
        Accedé con tu cuenta para ver tus informes y documentos.
      </p>

      <form onSubmit={handleSubmit} className="mt-8 flex flex-col gap-5" noValidate>
        <div className="flex flex-col gap-1.5">
          <label htmlFor="email" className="text-xs font-semibold text-text-primary">
            Email
          </label>
          <input
            id="email"
            name="email"
            type="email"
            autoComplete="email"
            required
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            placeholder="tu@empresa.com"
            className="h-11 rounded-md border border-base-border px-3 text-sm text-text-primary outline-none transition-colors placeholder:text-text-secondary/60 focus:border-forest"
          />
        </div>

        <div className="flex flex-col gap-1.5">
          <label htmlFor="password" className="text-xs font-semibold text-text-primary">
            Contraseña
          </label>
          <div className="relative">
            <input
              id="password"
              name="password"
              type={showPassword ? "text" : "password"}
              autoComplete="current-password"
              required
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              className="h-11 w-full rounded-md border border-base-border px-3 pr-16 text-sm text-text-primary outline-none transition-colors focus:border-forest"
            />
            <button
              type="button"
              onClick={() => setShowPassword((visible) => !visible)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-[11px] font-semibold text-forest hover:underline"
            >
              {showPassword ? "Ocultar" : "Ver"}
            </button>
          </div>
        </div>

        {error && (
          <p
            role="alert"
            aria-live="polite"
            className="rounded-md border border-red-200 bg-red-50 px-3 py-2.5 text-xs text-red-700"
          >
            {error}
          </p>
        )}

        <button
          type="submit"
          disabled={isDisabled}
          className="mt-1 inline-flex h-11 items-center justify-center rounded-md bg-forest px-6 text-sm font-medium text-white transition-colors hover:bg-forest/90 disabled:cursor-not-allowed disabled:opacity-50"
        >
          {isSubmitting ? "Entrando..." : "Entrar"}
        </button>
      </form>

      <p className="mt-8 border-t border-base-border/60 pt-5 text-center text-[11px] leading-relaxed text-text-secondary">
        ¿No tenés cuenta? Pedí un enlace de acceso desde{" "}
        <Link href="/calculadora" className="font-semibold text-forest hover:underline">
          la calculadora
        </Link>{" "}
        y te llega por correo.
      </p>
    </div>
  );
}

export default function LoginPage() {
  return (
    <div className="flex min-h-screen flex-col bg-base-natural font-display text-text-primary">
      {/* Sin el control de sesion: ya estamos en la pantalla de entrar. */}
      <Header hideSessionControls />

      <main className="relative flex flex-1 items-center justify-center overflow-hidden px-4 py-12 sm:px-8">
        <img
          src="/assets/backgrounds/vector.svg"
          alt=""
          aria-hidden="true"
          className="pointer-events-none absolute -left-[-36px] top-1/2 hidden h-[430px] w-[390px] -translate-y-1/2 opacity-90 md:block"
        />

        <Suspense fallback={<div className="text-sm text-text-secondary">Cargando...</div>}>
          <LoginForm />
        </Suspense>
      </main>

      <Footer />
    </div>
  );
}
