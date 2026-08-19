"use client";

import { useEffect, type ReactNode } from "react";
import { usePathname, useRouter } from "next/navigation";
import { useAuth } from "@/components/AuthProvider";

/**
 * Envuelve una pantalla que exige sesion. Sin token manda a /login guardando la
 * ruta pedida en `?next=`, para volver ahi apenas entre.
 *
 * Es una guarda de navegacion, no de seguridad: el token vive en el navegador y
 * cualquiera puede saltearla. Lo que de verdad protege los datos es que la API
 * responde 401 sin un token valido; esto solo evita mostrar pantallas vacias.
 */
export default function RequireAuth({ children }: { children: ReactNode }) {
  const { status } = useAuth();
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    if (status === "anonymous") {
      router.replace(`/login?next=${encodeURIComponent(pathname)}`);
    }
  }, [status, pathname, router]);

  if (status === "loading") {
    return (
      <div className="flex flex-1 items-center justify-center py-20">
        <div className="h-10 w-10 animate-spin rounded-full border-4 border-base-border border-t-forest" />
        <span className="sr-only">Verificando tu sesión...</span>
      </div>
    );
  }

  // Mientras corre el replace no se muestra nada: evita el parpadeo del
  // contenido protegido antes de que el router cambie de pantalla.
  if (status === "anonymous") return null;

  return <>{children}</>;
}
