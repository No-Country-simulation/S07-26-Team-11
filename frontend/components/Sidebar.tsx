"use client";

import { useState, useEffect } from "react";
import Image from "next/image";
import Link from "next/link";
import { usePathname } from "next/navigation";

const navItems = [
  { label: "Overview", href: "/admin/overview" },
  { label: "Leads", href: "/admin/leads" },
  { label: "Bench Analytics", href: "/admin/analytics" },
  { label: "Outreach", href: "/admin/outreach" },
  { label: "Campaigns", href: "/admin/campaigns" },
];

export default function Sidebar() {
  const pathname = usePathname();
  const [isOpen, setIsOpen] = useState(false);


  useEffect(() => {
    setIsOpen(false);
  }, [pathname]);

  return (
    <>

      <button
        onClick={() => setIsOpen(true)}
        aria-label="Abrir menú"
        className="fixed bottom-5 right-5 z-40 flex h-12 w-12 items-center justify-center rounded-full bg-forest text-white shadow-lg transition-transform active:scale-95 lg:hidden"
      >
        <svg className="h-6 w-6 stroke-current" fill="none" viewBox="0 0 24 24" strokeWidth="2">
          <path strokeLinecap="round" strokeLinejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
        </svg>
      </button>

      {isOpen && (
        <div
          onClick={() => setIsOpen(false)}
          className="fixed inset-0 z-40 bg-black/40 backdrop-blur-sm transition-opacity lg:hidden"
        />
      )}

      <aside
        className={`fixed inset-y-0 left-0 z-50 flex h-screen w-64 shrink-0 flex-col justify-between border-r border-base-border/50 bg-white px-6 py-8 transition-transform duration-300 ease-in-out lg:sticky lg:top-0 lg:translate-x-0 ${
          isOpen ? "translate-x-0" : "-translate-x-full"
        }`}
      >
        <div className="flex flex-col gap-6">

          <div className="flex items-start justify-between">
            <div>
              <div className="flex items-center gap-2">
                <div className="flex items-center gap-1.5 font-bold tracking-tight text-forest">
                  <Image
                    src="/assets/brand/logotipo.svg"
                    alt="Capacia"
                    width={115}
                    height={30}
                    priority
                    className="h-auto w-[115px] p-2"
                  />
                </div>
              </div>
              <span className="mt-2 inline-block rounded bg-[#FDF6E2] px-2.5 py-0.5 text-[11px] font-semibold tracking-wider text-[#A07800]">
                TEAM
              </span>
            </div>

            <button
              onClick={() => setIsOpen(false)}
              aria-label="Cerrar menú"
              className="rounded-md p-1 text-text-secondary hover:bg-black/5 hover:text-text-primary lg:hidden"
            >
              <svg className="h-5 w-5 stroke-current" fill="none" viewBox="0 0 24 24" strokeWidth="2">
                <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <nav className="flex flex-col gap-1">
            {navItems.map((item) => {
              const isActive = pathname === item.href;
              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={`rounded-md px-3.5 py-2.5 text-sm font-medium transition-colors ${
                    isActive
                      ? "bg-[#E6ECE8] text-[#1E3A2F]"
                      : "text-text-secondary hover:bg-black/5 hover:text-text-primary"
                  }`}
                >
                  {item.label}
                </Link>
              );
            })}
          </nav>
        </div>

        <div className="flex flex-col gap-1 border-t border-base-border/40 pt-4">
          <Link
            href="/admin/settings"
            className={`flex items-center gap-2.5 rounded-md px-3.5 py-2 text-sm font-medium transition-colors ${
              pathname === "/admin/settings"
                ? "bg-[#E6ECE8] text-[#1E3A2F]"
                : "text-text-secondary hover:bg-black/5 hover:text-text-primary"
            }`}
          >
            <svg className="h-4 w-4 stroke-current" fill="none" viewBox="0 0 24 24" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
              <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            Settings
          </Link>

          <button
            onClick={() => console.log("Logout")}
            className="flex w-full items-center gap-2.5 rounded-md px-3.5 py-2 text-sm text-text-secondary hover:bg-black/5 hover:text-text-primary"
          >
            <svg className="h-4 w-4 stroke-current" fill="none" viewBox="0 0 24 24" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15M12 9l-3 3m0 0l3 3m-3-3h12.75" />
            </svg>
            Logout
          </button>
        </div>
      </aside>
    </>
  );
}
