"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import { ApiError, authApi, type AuthenticatedUser } from "@/lib/api";
import {
  clearStoredSession,
  isExpired,
  readAccessToken,
  saveAccessToken,
} from "@/lib/session";

/**
 * - `loading`: todavia se esta comprobando el token guardado. Es el estado
 *   inicial en cada carga de pagina y por eso las pantallas protegidas no deben
 *   decidir nada hasta que salga de aca.
 * - `authenticated` / `anonymous`: ya se sabe.
 */
export type AuthStatus = "loading" | "authenticated" | "anonymous";

interface AuthContextValue {
  status: AuthStatus;
  user: AuthenticatedUser | null;
  /** Lanza ApiError si las credenciales no sirven; la pantalla lo traduce. */
  login: (email: string, password: string) => Promise<AuthenticatedUser>;
  /**
   * Toma por buena una sesion que se obtuvo por fuera del formulario: hoy, el
   * canje del magic link en /auth/verify. Sin esto el token queda guardado pero
   * el proveedor no se entera hasta la proxima recarga completa de la pagina.
   */
  adoptAccessToken: (token: string) => Promise<AuthenticatedUser>;
  logout: () => Promise<void>;
  /** true si el usuario tiene el rol, con o sin el prefijo ROLE_. */
  hasRole: (role: string) => boolean;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export default function AuthProvider({ children }: { children: ReactNode }) {
  const [status, setStatus] = useState<AuthStatus>("loading");
  const [user, setUser] = useState<AuthenticatedUser | null>(null);

  // Al montar se revalida el token guardado contra el backend. No alcanza con
  // mirar su fecha de expiracion: pudo revocarse con un logout desde otra
  // pestana, y solo el servidor conoce la lista de revocacion.
  useEffect(() => {
    let cancelled = false;

    async function restoreSession() {
      const token = readAccessToken();

      if (!token || isExpired(token)) {
        clearStoredSession();
        if (!cancelled) setStatus("anonymous");
        return;
      }

      try {
        const me = await authApi.me();
        if (cancelled) return;
        setUser(me);
        setStatus("authenticated");
      } catch {
        // 401 (revocado o invalido) o backend caido: en ambos casos no hay
        // sesion utilizable, asi que se descarta y se sigue como anonimo.
        clearStoredSession();
        if (cancelled) return;
        setUser(null);
        setStatus("anonymous");
      }
    }

    restoreSession();
    return () => {
      cancelled = true;
    };
  }, []);

  const adoptAccessToken = useCallback(async (token: string) => {
    saveAccessToken(token);

    try {
      const me = await authApi.me();
      setUser(me);
      setStatus("authenticated");
      return me;
    } catch (cause) {
      // El token existe pero no se pudo leer la identidad: no dejar una sesion
      // a medias guardada en el navegador.
      clearStoredSession();
      setUser(null);
      setStatus("anonymous");
      throw cause;
    }
  }, []);

  const login = useCallback(
    async (email: string, password: string) => {
      const session = await authApi.login(email, password);
      return adoptAccessToken(session.accessToken);
    },
    [adoptAccessToken],
  );

  const logout = useCallback(async () => {
    try {
      await authApi.logout();
    } catch (cause) {
      // Que el servidor no pueda revocarlo no debe dejar al usuario atrapado
      // dentro de la sesion: la parte local se limpia igual.
      if (!(cause instanceof ApiError)) console.error("Fallo el logout remoto", cause);
    } finally {
      clearStoredSession();
      setUser(null);
      setStatus("anonymous");
    }
  }, []);

  const hasRole = useCallback(
    (role: string) => {
      if (!user) return false;
      const wanted = role.startsWith("ROLE_") ? role : `ROLE_${role}`;
      return user.roles.includes(wanted);
    },
    [user],
  );

  const value = useMemo(
    () => ({ status, user, login, adoptAccessToken, logout, hasRole }),
    [status, user, login, adoptAccessToken, logout, hasRole],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth debe usarse dentro de <AuthProvider>");
  }
  return context;
}
