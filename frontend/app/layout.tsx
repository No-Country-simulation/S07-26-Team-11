import type { Metadata } from "next";
import { Red_Hat_Display, Alegreya_Sans } from "next/font/google";
import "./globals.css";

const alegreyaSans = Alegreya_Sans({
  subsets: ["latin"],
  weight: ["400", "500", "700"], 
  variable: "--font-alegreya"
});

const redHatDisplay = Red_Hat_Display({
  subsets: ["latin"],
  variable: "--font-redhat",
  display: "swap",
});

export const metadata: Metadata = {
  title: "[Nombre del proyecto]",
  description:
    "Calculadora de capacidad ociosa y benchmark de madurez para operadores de data centers.",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="es" className={`${redHatDisplay.variable} ${alegreyaSans.variable}`}>
      <body className="font-display">{children}</body>
    </html>
  );
}
