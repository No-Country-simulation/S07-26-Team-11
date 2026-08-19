/** Formato de moneda/porcentaje para las cadenas que exige POST /documents (ya formateadas). */

export function formatUsd(value: number): string {
  return `US$ ${Math.round(value).toLocaleString("es-AR")}`;
}

export function formatKw(value: number): string {
  return `${Math.round(value).toLocaleString("es-AR")} kW`;
}

export function formatPercent(fraction: number): string {
  return `${Math.round(fraction * 100)}%`;
}

/**
 * `20260717T2351`: fecha y hora local, compacta, sin separadores. Se usa para que cada
 * generación del informe quede como un archivo propio (`informe-benchmark-<esto>.pdf`) en vez
 * de reemplazar la anterior — ver metadata.name en POST /documents, único por usuario.
 */
export function formatTimestampCompact(date: Date): string {
  const pad = (n: number) => String(n).padStart(2, "0");
  const y = date.getFullYear();
  const mo = pad(date.getMonth() + 1);
  const d = pad(date.getDate());
  const h = pad(date.getHours());
  const mi = pad(date.getMinutes());
  return `${y}${mo}${d}T${h}${mi}`;
}
