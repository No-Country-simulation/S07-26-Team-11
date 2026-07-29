import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        forest: {
          light: "var(--color-forest-light)",
          DEFAULT: "var(--color-forest)",
          dark: "var(--color-forest-dark)",
        },
        gold: {
          DEFAULT: "var(--color-gold)",
          dark: "var(--color-gold-dark)",
        },
        base: {
          natural: "var(--color-base-natural)",
          internal: "var(--color-base-internal)",
          border: "var(--color-base-border)",
        },
        text: {
          primary: "var(--color-text-primary)",
          secondary: "var(--color-text-secondary)",
        },
        status: {
          success: "var(--color-status-success)",
        }
      },
      fontFamily: {
        display: ["var(--font-redhat)", "sans-serif"], 
      }
    },
  },
  plugins: [],
};

export default config;