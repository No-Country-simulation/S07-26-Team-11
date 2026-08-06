import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Maturity Benchmark | Capacia",
};

export default function AgendarReunionLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <>{children}</>;
}