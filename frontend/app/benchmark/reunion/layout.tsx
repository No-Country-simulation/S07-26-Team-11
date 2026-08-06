import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Agendar una Reunión | Capacia",
};

export default function AgendarReunionLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <>{children}</>;
}