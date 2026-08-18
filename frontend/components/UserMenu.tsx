"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/components/AuthProvider";

/**
 * Estado de sesion para la barra superior: entrar, o quien sos y salir.
 * Lo monta <Header> en todas las pantallas; no hace falta ponerlo a mano.
 *
 * Los tres estados ocupan un alto parecido para que la barra no salte cuando
 * termina la comprobacion inicial del token.
 */
export default function UserMenu() {
  const { status, user, logout, hasRole } = useAuth();
  const router = useRouter();
  const [isLeaving, setIsLeaving] = useState(false);

  // Mientras se revalida el token no se sabe si mostrar "entrar" o el email:
  // un placeholder neutro evita enseñar el estado equivocado por un instante.
  if (status === "loading") {
    return (
      <div
        aria-hidden="true"
        className="h-4 w-20 animate-pulse rounded-full bg-base-internal"
      />
    );
  }

  if (status === "anonymous") {
    return (
      <Link
        href="/login"
        // Enlace y no boton: en varias pantallas el Header ya trae su propio
        // CTA y dos botones llenos competirian entre si.
        className="text-xs font-semibold text-forest transition-colors hover:text-forest-dark hover:underline"
      >
        Iniciar sesión
      </Link>
    );
  }

  async function handleLogout() {
    setIsLeaving(true);
    await logout();
    router.replace("/login");
  }

  return (
    <div className="flex items-center gap-3">
      <Link
        href="/cuenta"
        className="hidden max-w-[200px] truncate text-xs font-medium text-text-secondary hover:text-forest sm:block"
        title={user?.email}
      >
        {user?.email}
      </Link>

      {hasRole("ADMIN") && (
        <span className="hidden rounded-[100px] bg-forest-soft px-2.5 py-1 text-[10px] font-semibold uppercase tracking-wide text-status-success sm:inline">
          Admin
        </span>
      )}

      <button
        type="button"
        onClick={handleLogout}
        disabled={isLeaving}
        className="inline-flex h-9 items-center rounded-md border border-base-border px-4 text-xs font-medium text-text-primary transition-colors hover:border-forest hover:text-forest disabled:opacity-50"
      >
        {isLeaving ? "Saliendo..." : "Salir"}
      </button>
    </div>
  );
}
