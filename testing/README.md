# testing/ — Plan de pruebas, colecciones y reportes

**Responsable principal:** Vanina Restelli — QA Tester
**Objetivo:** verificar que el sistema hace lo que dice, y dejar constancia de qué se probó y con qué resultado.

> Si esta persona cambia, **actualiza esta línea en el mismo PR de la reasignación.**

---

## Estructura

```
postman/    Colecciones y entornos de Postman
e2e/        Pruebas de extremo a extremo (Cypress)
reports/    Reportes de ejecución, por fecha
```

---

## Cómo hacer las pruebas

### 1. Pruebas de API con Postman

**No escribas la colección a mano.** El backend publica su especificación OpenAPI: impórtala y tendrás todos los endpoints con sus esquemas, ya actualizados.

```
1. Levanta el backend (ver backend/README.md)
2. En Postman: Import → Link → http://localhost:8080/v3/api-docs
3. Crea dos entornos: "local" y "staging", con la variable baseUrl
4. Guarda la colección exportada en postman/
```

Cuando el backend cambie un endpoint, **reimporta**. Actualizar la colección a mano garantiza que en algún momento pruebes contra un contrato que ya no existe.

**Qué probar en cada endpoint, más allá del camino feliz:**

| Categoría | Ejemplos |
|---|---|
| Validación | Campos faltantes, tipos incorrectos, números negativos donde no aplican, strings vacíos |
| Límites | Valores en cero, valores enormes, textos de 500 caracteres, listas vacías |
| Autenticación | Sin token, token expirado, token de otro usuario, token ya usado |
| Autorización | Rol `VIEWER` intentando acciones de `ADMIN` |
| Idempotencia | La misma petición dos veces: ¿crea dos registros o uno? |
| Rate limit | Cuatro solicitudes de captura de email en una hora: la cuarta debe devolver `429` |
| Formato de error | ¿Todos los errores siguen el formato de `docs/API.md`? ¿Alguno filtra una traza? |
| Privacidad | ¿`POST /public/leads` responde igual con un email registrado y con uno que no lo está? |

### 2. Pruebas de extremo a extremo con Cypress

```bash
cd testing/e2e
npm install
npx cypress open      # modo interactivo
npx cypress run       # modo consola, para reportes
```

**Los tres flujos que hay que cubrir sí o sí:**

1. **Calculadora → captura de email → verificación**: el usuario calcula, deja su correo, abre el enlace y ve el resultado desbloqueado.
2. **Benchmark completo → PDF**: el usuario responde las 25 preguntas, cierra el cuestionario, ve su resultado, y el PDF se genera y se puede descargar.
3. **Guardado parcial**: el usuario responde 10 preguntas, cierra el navegador, vuelve y encuentra su avance intacto. Este es el que más falla y el que más abandono causa.

### 3. Pruebas manuales exploratorias

No todo se automatiza en cinco semanas. Reserva tiempo para probar sin guion — es donde aparecen los errores que nadie anticipó. Documenta lo que encuentres en `reports/`.

**Lista de verificación manual antes de cada demo:**

- ☐ El sitio carga en móvil y en escritorio
- ☐ El correo del magic link llega y **no cae en spam**
- ☐ El PDF abre correctamente en Acrobat, en el visor de Chrome y en un móvil
- ☐ Las tildes y la ñ se ven bien en el PDF y en los exportes CSV
- ☐ Un nombre de empresa muy largo no rompe el diseño del PDF
- ☐ El sitio en staging responde a la primera carga tras estar inactivo (o se hizo el ping previo)
- ☐ El enlace de baja del correo funciona

---

## Cómo reportar un error

Abre un issue en GitHub con **estas cinco cosas**. Un reporte sin ellas se devuelve:

1. **Qué esperabas que pasara**
2. **Qué pasó en realidad**
3. **Cómo reproducirlo**, paso a paso, desde cero
4. **Dónde**: local o staging, navegador, hora aproximada
5. **Evidencia**: captura, video corto, o el cuerpo de la respuesta del error

Etiqueta a la persona responsable del área. Si bloquea a alguien más, además avisa en el chat del equipo.

**Severidad:**

| Nivel | Criterio | Respuesta esperada |
|---|---|---|
| Bloqueante | Impide usar el flujo principal o rompe la demo | El mismo día |
| Alta | Funcionalidad importante rota, con rodeo posible | Dentro de la semana |
| Media | Comportamiento incorrecto que no impide avanzar | Se prioriza en planeación |
| Baja | Cosmético o de detalle | Si sobra tiempo |

---

## Dónde poner los reportes

En `reports/`, con el formato `AAAA-MM-DD-descripcion.md` o el archivo generado por la herramienta.

```
reports/
├── 2026-XX-XX-semana-1-api.md
├── 2026-XX-XX-semana-2-benchmark.md
└── 2026-XX-XX-ensayo-demo-final.md
```

**Plantilla de reporte:**

```markdown
# Reporte de pruebas — [fecha]

**Ambiente:** local / staging
**Versión probada:** [tag o commit]
**Ejecutado por:** [nombre]

## Alcance
Qué se probó y qué quedó fuera.

## Resultados
| Caso | Resultado | Issue |
|---|---|---|
| | ✅ / ❌ | #12 |

## Hallazgos
Los errores encontrados, con su severidad.

## Riesgos detectados
Lo que no falló pero preocupa.
```

---

## Estado actual

- ☑ Estructura del directorio
- ☐ Plan de pruebas de la Semana 1
- ☐ Colección de Postman importada desde OpenAPI (Semana 0–1)
- ☐ Cypress configurado (Semana 1)
- ☐ Los tres flujos E2E (Semanas 2–4)
- ☐ Ensayo completo de la demo (Semana 4)
