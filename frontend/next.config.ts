import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  reactStrictMode: true,
  // La URL de la API se inyecta por variable de entorno, nunca se escribe en el codigo.
  env: {
    NEXT_PUBLIC_API_BASE_URL: process.env.NEXT_PUBLIC_API_BASE_URL,
  },
};

export default nextConfig;
