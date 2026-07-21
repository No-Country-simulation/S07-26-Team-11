# frontend/ — Sitio público y dashboard interno

**Responsable principal:** Maria Daniela Monti Julien — Frontend Developer
**Apoyo:** Oskar Morales — Full Stack Developer · Vanina Restelli — UX/UI Designer
**Objetivo:** las interfaces que usa el operador de data center (reporte, calculadora, benchmark) y el dashboard del equipo comercial.

> Si estas personas cambian, **actualiza esta línea en el mismo PR de la reasignación.**

---

## Stack (propuesta del arquetipo, no obligatoria)

Next.js (App Router) + TypeScript + Tailwind. **El equipo de frontend puede cambiarlo a Angular, Nuxt, Astro, Jeckyll, Remix, Vue, Vite (CSS, Javascript, HTML puro y su script de servidor), etc, están en su libre desición**; la única condición es respetar el contrato de [`docs/API.md`](../docs/API.md) y dejar constancia en un ADR.

Que el backend sea Java no condiciona esta elección: se comunican por JSON.

---

## El diseño no está aquí todavía

Este arquetipo trae una página deliberadamente sin diseño. Existe para verificar que el proyecto compila y alcanza la API. **El sistema de diseño lo define el equipo de UX/UI** en [`design/`](../design/), y de ahí salen los tokens (`--color-forest`, `--color-gold`, tipografías, escalas) que consume `app/globals.css` y `tailwind.config.ts`.

Los mismos tokens alimentan el template del PDF. Es la única forma de que el PDF y el sitio se vean del mismo producto.

---

## Cómo ejecutarlo

```bash
cp .env.example .env.local     # y ajustar NEXT_PUBLIC_API_BASE_URL
npm install
npm run dev                    # http://localhost:3000
```

Necesitas el backend corriendo en `http://localhost:8080` (ver [`backend/README.md`](../backend/README.md)).

---

## Tipos de la API: no los escribas a mano

Con el backend levantado:

```bash
npm run types:api      # genera lib/api-types.ts desde el OpenAPI del backend
```

Esto descarga el contrato real y genera los tipos TypeScript. Si el backend cambia un campo, el compilador te avisa. **Escribir las interfaces a mano garantiza que en algún momento se desincronicen y nadie se entere hasta que falle en la demo.**

---

## Cómo avanzar sin esperar al backend

No te quedes bloqueado. Con el contrato de `docs/API.md` acordado:

1. Genera los tipos desde el OpenAPI, aunque los endpoints devuelvan datos falsos.
2. Trabaja contra un mock (MSW o Prism) mientras el backend implementa.
3. Cuando el endpoint real esté listo, cambia la URL base y listo.

Si el frontend está esperando al backend, es una falla de arquitectura, no del backend. Avísalo en el daily.

---

## Comandos

```bash
npm run dev        # desarrollo
npm run build      # verificar que compila (esto es lo que corre antes del merge semanal)
npm run start      # servir el build
npm run lint       # linter
npm run types:api  # regenerar tipos desde el OpenAPI
```

---

## Estado actual

- ☑ Arquetipo compila y arranca
- ☑ Cliente HTTP con manejo del formato de error de la API
- ☑ Tokens de diseño como variables CSS (valores provisionales)
- ☐ Wizard de la calculadora (Semana 1)
- ☐ Captura de email y verificación (Semana 1)
- ☐ Cuestionario del benchmark con guardado parcial (Semana 2)
- ☐ Pantalla de resultado y descarga del PDF (Semana 3)
- ☐ Dashboard interno (Semana 2–4)
- ☐ Reporte de industria (Semana 2)
