# design/ — Sistema de diseño, template del PDF y activos de marca

**Responsable principal:** [Nombre] — UX/UI Designer
**Apoyo:** [Nombre] — Frontend Developer (maquetación del template del PDF)
**Objetivo:** que el sitio y el PDF se vean como el mismo producto, y que lo diseñado se pueda construir.

> Si estas personas cambian, **actualiza esta línea en el mismo PR de la reasignación.**

---

## Lee esto antes de diseñar el PDF

El PDF **no se renderiza en un navegador.** Se genera en el servidor con un motor que soporta un subconjunto de CSS. Esto no es una limitación temporal ni algo que el equipo pueda "arreglar después": es una propiedad de la herramienta.

### Lo que NO se puede usar en el PDF

| No disponible | Qué usar en su lugar |
|---|---|
| `display: flex` | Layout con **tablas** o bloques |
| `display: grid` | Tablas anidadas |
| Cualquier animación o interacción | Nada: es un documento impreso |
| JavaScript, incluidas librerías de gráficas | **SVG** generado en el servidor |
| `position: sticky` | Encabezados y pies de página con `@page` |
| Fuentes de Google cargadas por URL | Archivos de fuente incrustados, con licencia que lo permita |

### Lo que SÍ se puede usar

- Tipografía completa: familias, pesos, tamaños, interlineado, espaciado entre letras.
- Colores, degradados simples, bordes, fondos, opacidad.
- Encabezado y pie fijos en todas las páginas, con numeración automática (`Página 3 de 12`).
- Control de saltos de página: evitar que un bloque se parta a la mitad.
- Imágenes (PNG, JPG) y **SVG**, que es lo ideal para gráficas, íconos y logos.
- Tamaño de página, márgenes y sangrados definidos con precisión.

### Cómo trabajar con esto sin frustrarte

**Diseña como si fuera un documento editorial impreso, no una página web.** Un informe de consultoría, un reporte anual, una ficha técnica. Ese es exactamente el modelo mental correcto, y además es el que produce mejor resultado para este entregable.

**Valida temprano.** Antes de terminar el diseño, entrega una página de muestra al Frontend Dev para que la maquete y la pase por el generador. Si algo no se puede construir, es mucho mejor saberlo en la Semana 0 que en la Semana 3.

**Si un layout que quieres no se puede hacer**, habla con el arquitecto antes de descartarlo. Casi siempre hay una forma de lograr el mismo efecto visual con tablas; y si de verdad no la hay, existe un plan de contingencia técnico documentado en el ADR-0002.

---

## Lo que sí puedes hacer con total libertad

El sitio web **no tiene ninguna de estas restricciones**. Es Next.js: flexbox, grid, animaciones, interacciones, lo que quieras.

Y en el PDF, la restricción es sobre el *layout*, no sobre la calidad visual. Tipografía, color, jerarquía, ritmo, densidad de información, tratamiento de datos — todo eso es tuyo y es lo que hace que un documento se vea institucional o improvisado.

---

## Qué se espera de este directorio

| Entregable | Semana | Formato | Estado |
|---|---|---|---|
| Design tokens | 0–1 | `tokens/tokens.json` | ☐ |
| Sistema de diseño (tipografía, color, espaciado, componentes) | 1 | Figma + export a `exports/` | ☐ |
| Diseño del wizard de la calculadora | 1 | Figma | ☐ |
| Diseño del cuestionario del benchmark | 2 | Figma | ☐ |
| Diseño del PDF (todas las páginas) | 2 | Figma + página de muestra validada | ☐ |
| Plantillas de correo | 3 | Figma o HTML | ☐ |
| Activos de marca (logo, fuentes, íconos) | 0 | `assets/` | ☐ |
| Diseño del dashboard interno | 2–3 | Figma. Funcional antes que bonito | ☐ |

---

## Cómo entregar

**Los archivos de Figma no se versionan aquí.** Son binarios grandes que hacen el repositorio inmanejable.

Lo que sí va en este directorio:

```
tokens/     tokens.json — la fuente de verdad de colores, tipografías y escalas
assets/     logo en SVG, íconos en SVG, archivos de fuente
exports/    PNG o PDF de las pantallas aprobadas, para referencia rápida
README.md   este archivo, con los enlaces a Figma
```

**Enlaces de Figma:**

| Archivo | Enlace | Estado |
|---|---|---|
| Sistema de diseño | [pegar enlace] | ☐ |
| Sitio público | [pegar enlace] | ☐ |
| PDF institucional | [pegar enlace] | ☐ |
| Dashboard interno | [pegar enlace] | ☐ |

---

## Design tokens: por qué importan aquí

Los tokens de `tokens/tokens.json` los consumen **dos** cosas: `frontend/app/globals.css` y el template del PDF. Definir el verde en un solo lugar es lo que evita que el sitio use un verde y el PDF otro ligeramente distinto — un detalle que el cliente sí nota.

Cuando cambies un color, cámbialo en `tokens.json` y avisa en el chat. No lo cambies directamente en el CSS del frontend.

---

## Lo que necesitas del cliente (pídelo el día 1 a través del PM)

- ☐ Logo en formato vectorial (SVG o AI)
- ☐ Códigos exactos de la paleta: forest-green y gold, con sus variantes
- ☐ Tipografías, **con licencia que permita incrustarlas en un PDF** (esto se olvida siempre y bloquea el entregable)
- ☐ Datos de contacto del fundador para el bloque de cierre del PDF
- ☐ Ejemplos de documentos que al cliente le parezcan bien hechos
- ☐ Quién aprueba el diseño y cuántas rondas de revisión hay

---

## Estado actual

- ☑ Estructura del directorio y restricciones documentadas
- ☐ Kit de marca recibido del cliente
- ☐ Tokens definidos
- ☐ Página de muestra del PDF validada contra el generador
