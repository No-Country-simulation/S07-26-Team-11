import type { Config } from "tailwindcss";

/**
 * Los colores vienen de design/tokens/tokens.json, que mantiene la Disenadora.
 * Estos son valores de arranque para que el proyecto compile: la paleta real
 * (forest-green y gold) la define el kit de marca del cliente.
 *
 * Los mismos tokens alimentan el template del PDF: una sola fuente de verdad.
 */
const config: Config = {
  content: ["./app/**/*.{ts,tsx}", "./lib/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        forest: {
          DEFAULT: "var(--color-forest)",
          dark: "var(--color-forest-dark)",
          light: "var(--color-forest-light)",
        },
        gold: {
          DEFAULT: "var(--color-gold)",
          dark: "var(--color-gold-dark)",
        },
      },
    },
  },
  plugins: [],
};

export default config;
