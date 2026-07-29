import type { Metadata } from "next";
import { Red_Hat_Display } from "next/font/google";
import "./globals.css";

const redHatDisplay = Red_Hat_Display({ 
  subsets: ["latin"],
  variable: "--font-redhat" // Opcional, para usarla en Tailwind
});

export const metadata: Metadata = {
  title: "[Nombre del proyecto]",
  description:
    "Calculadora de capacidad ociosa y benchmark de madurez para operadores de data centers.",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="es">
      <body className={redHatDisplay.className}>{children}</body>
    </html>
  );
}
