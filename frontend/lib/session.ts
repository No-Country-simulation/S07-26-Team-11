/**
 * Almacenamiento del token de sesion en el navegador.
 *
 * Vive aparte de lib/api.ts para que el cliente HTTP pueda leer el token sin
 * depender del arbol de React, y para que no haya import circular entre el
 * cliente y el proveedor de sesion.
 *
 * La clave `accessToken` es la misma que ya usaba el aterrizaje del magic link
 * (app/auth/verify/page.tsx), asi que las dos formas de entrar comparten sesion:
 * quien llega por enlace queda logueado igual que quien puso su contrasena.
 */

const TOKEN_KEY = "accessToken";
const LEAD_KEY = "leadInfo";

/** El token guardado, o null en SSR y cuando no hay sesion. */
export function readAccessToken(): string | null {
  if (typeof window === "undefined") return null;
  return window.localStorage.getItem(TOKEN_KEY);
}

export function saveAccessToken(token: string): void {
  if (typeof window === "undefined") return;
  window.localStorage.setItem(TOKEN_KEY, token);
}

/** Borra todo rastro de la sesion, incluido lo que deja el magic link. */
export function clearStoredSession(): void {
  if (typeof window === "undefined") return;
  window.localStorage.removeItem(TOKEN_KEY);
  window.localStorage.removeItem(LEAD_KEY);
}

/**
 * Momento de expiracion segun el claim `exp` del JWT, en milisegundos.
 *
 * Solo sirve para ahorrarse peticiones que ya sabemos que van a dar 401: la
 * autoridad sobre si un token vale sigue siendo el backend, que ademas conoce
 * la lista de revocacion (un token deslogueado no expiro, pero ya no sirve).
 * Nunca tomar una decision de seguridad con esto.
 */
export function expiresAt(token: string): number | null {
  const payload = decodePayload(token);
  return typeof payload?.exp === "number" ? payload.exp * 1000 : null;
}

/** true si el token ya venció segun su propio claim `exp`. */
export function isExpired(token: string): boolean {
  const expiry = expiresAt(token);
  return expiry !== null && expiry <= Date.now();
}

function decodePayload(token: string): Record<string, unknown> | null {
  const segments = token.split(".");
  if (segments.length !== 3) return null;

  try {
    // base64url -> base64 antes de atob, que no acepta los caracteres - y _
    const base64 = segments[1].replace(/-/g, "+").replace(/_/g, "/");
    const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), "=");
    return JSON.parse(atob(padded)) as Record<string, unknown>;
  } catch {
    return null;
  }
}
