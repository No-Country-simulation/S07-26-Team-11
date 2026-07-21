/**
 * Pagina de arranque, deliberadamente sin diseno.
 * Existe para verificar que el proyecto compila y que el frontend alcanza la API.
 * La Disenadora y el Frontend Dev la reemplazan por completo en la Semana 1.
 */
export default function Home() {
  return (
    <main className="mx-auto max-w-2xl p-8">
      <h1 className="text-2xl font-semibold text-forest">[Nombre del proyecto]</h1>

      <p className="mt-4 text-sm">
        Arquetipo del frontend. Sin diseño todavía: el sistema de diseño lo define el
        equipo de UX/UI en <code>design/</code>.
      </p>

      <section className="mt-8 border-t pt-6">
        <h2 className="font-medium">Pantallas por construir</h2>
        <ul className="mt-2 list-disc space-y-1 pl-5 text-sm">
          <li>Reporte de industria (público)</li>
          <li>Calculadora de estimación, en pasos</li>
          <li>Captura de email y verificación por enlace</li>
          <li>Cuestionario del benchmark, con guardado parcial</li>
          <li>Resultado y descarga del PDF</li>
          <li>Dashboard interno</li>
        </ul>
      </section>

      <section className="mt-8 border-t pt-6 text-sm">
        <h2 className="font-medium">Verificar conexión con la API</h2>
        <p className="mt-2">
          Con el backend levantado, <code>GET /api/v1/public/ping</code> debe responder{" "}
          <code>{`{ "status": "ok" }`}</code>.
        </p>
      </section>
    </main>
  );
}
