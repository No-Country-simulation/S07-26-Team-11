import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "[Nombre del proyecto]",
  description:
    "Calculadora de capacidad ociosa y benchmark de madurez para operadores de data centers.",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="es">
      <body>{children}</body>
    </html>
  );
}
