import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Agendar una Reunión",
};

export default function AgendarReunionLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <>{children}</>;
}