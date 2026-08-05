import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Resultados",
};

export default function AgendarReunionLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <>{children}</>;
}