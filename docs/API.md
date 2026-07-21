# API.md — Catálogo de endpoints

**Este documento es el contrato entre el backend y el frontend.** Es el único artefacto del repositorio que ningún equipo puede cambiar por su cuenta.

- **Versión:** v1 (propuesta, Semana 0)
- **Base URL local:** `http://localhost:8080/api/v1`
- **Base URL staging:** `[por definir]`
- **Especificación viva:** `http://localhost:8080/v3/api-docs` · UI: `http://localhost:8080/swagger-ui.html`

> Este archivo es el **acuerdo**; el OpenAPI generado desde el código es la **implementación**. Cuando difieran, el código está mal o este documento no se actualizó — ambas cosas son un bug.

---

## Cómo cambiar este contrato

1. Abre un PR que modifique **solo** este archivo.
2. Etiqueta al arquitecto y a quien consume el endpoint.
3. Espera dos aprobaciones.
4. Recién entonces implementa.

**Cambios que rompen (necesitan versión nueva o aviso explícito):** quitar un campo, renombrarlo, cambiar su tipo, volver obligatorio un campo opcional, cambiar un código de estado.
**Cambios que no rompen:** agregar un campo opcional, agregar un endpoint nuevo.

---

## Convenciones generales

| Aspecto | Regla |
|---|---|
| Formato | JSON. `Content-Type: application/json; charset=utf-8` |
| Nombres | `camelCase` en JSON |
| Fechas | ISO-8601 con zona: `2026-07-20T14:30:00Z`. Siempre UTC |
| Identificadores | UUID como string |
| Decimales | Números JSON. Porcentajes como fracción (`0.1523` = 15.23%) |
| Nulos | `null` explícito. Nunca omitir el campo ni usar `""` |
| Paginación | `?page=0&size=20&sort=createdAt,desc` |
| Idempotencia | Header `Idempotency-Key` (UUID) en los POST marcados |
| Autenticación pública | `Authorization: Bearer <token>` obtenido por magic link |
| Autenticación admin | Sesión con cookie `HttpOnly` o Bearer, rol `ADMIN` / `VIEWER` |
| Rate limit | Header `X-RateLimit-Remaining` en la respuesta |

### Formato de error (RFC 9457, uniforme en toda la API)

```json
{
  "type": "https://[dominio]/errors/validation",
  "title": "Datos de entrada inválidos",
  "status": 400,
  "detail": "El campo contractedCapacityMw debe ser mayor que cero",
  "instance": "/api/v1/public/calculator/estimate",
  "traceId": "0af7651916cd43dd8448eb211c80319c",
  "errors": [
    { "field": "contractedCapacityMw", "message": "debe ser mayor que cero" }
  ]
}
```

### Códigos de estado que usamos

`200` OK · `201` Creado · `202` Aceptado (trabajo asíncrono) · `204` Sin contenido · `400` Entrada inválida · `401` Sin autenticar · `403` Sin permiso · `404` No existe · `409` Conflicto · `422` Regla de negocio violada · `429` Demasiadas peticiones · `500` Error del servidor.

---

## 1. Calculadora — `/public/calculator`

### `POST /public/calculator/estimate`
Calcula los KPIs a partir de los parámetros del operador. No requiere autenticación. Devuelve el resultado **básico**; el desglose completo exige desbloqueo con email.

Header opcional: `Idempotency-Key`.

**Request**
```json
{
  "contractedCapacityMw": 12.5,
  "utilizedCapacityMw": 8.2,
  "pue": 1.58,
  "energyCostPerKwh": 0.087,
  "rackCount": 420,
  "designPowerPerRackKw": 8.0,
  "averagePowerPerRackKw": 4.6,
  "currency": "USD",
  "country": "CO"
}
```

**Response `200`**
```json
{
  "estimateId": "0192f3a1-...",
  "calculationVersion": "1.0.0",
  "createdAt": "2026-07-20T14:30:00Z",
  "unlocked": false,
  "kpis": [
    {
      "code": "IDLE_CAPACITY_RATIO",
      "label": "Capacidad ociosa pagada",
      "value": 0.344,
      "unit": "RATIO",
      "benchmarkMedian": 0.28
    },
    {
      "code": "IDLE_CAPACITY_ANNUAL_COST",
      "label": "Costo anual de capacidad ociosa",
      "value": 3278160.0,
      "unit": "CURRENCY",
      "benchmarkMedian": null
    }
  ],
  "lockedKpiCount": 4
}
```

### `GET /public/calculator/estimates/{estimateId}`
Recupera un cálculo. Si se envía Bearer token del lead dueño, devuelve el desglose completo.

**Response `200`** — igual que el anterior, con `unlocked: true` y los KPIs completos, más `breakdown` y `assumptions`.
**Errores:** `404` si no existe.

### `GET /public/calculator/defaults`
Valores por defecto de industria para prellenar el formulario.

**Response `200`**
```json
{ "pue": 1.55, "energyCostPerKwh": 0.09, "designPowerPerRackKw": 8.0 }
```

---

## 2. Captura de email y acceso — `/public/leads`

### `POST /public/leads`
Registra un lead y envía un magic link. **Idempotente por email.**
Rate limit: 3 por email por hora, 10 por IP por hora.

**Request**
```json
{
  "email": "operador@empresa.com",
  "companyName": "Data Center Andino",
  "role": "Director de Operaciones",
  "source": "CALCULATOR",
  "estimateId": "0192f3a1-...",
  "consent": true,
  "privacyPolicyVersion": "1.0"
}
```
`source`: `CALCULATOR` · `BENCHMARK` · `REPORT` · `OUTREACH`

**Response `202`**
```json
{ "message": "Si el correo es válido, recibirás un enlace de acceso en unos segundos." }
```
> La respuesta es **idéntica** exista o no el email. No revelamos si un correo ya está registrado.

**Errores:** `400` sin consentimiento · `429` límite excedido.

### `POST /public/leads/verify`
Canjea el token del magic link por una sesión.

**Request** `{ "token": "..." }`

**Response `200`**
```json
{
  "accessToken": "eyJ...",
  "expiresAt": "2026-07-20T15:00:00Z",
  "lead": { "id": "0192...", "email": "operador@empresa.com", "companyName": "Data Center Andino" }
}
```
**Errores:** `400` token inválido, expirado o ya usado.

### `GET /public/leads/me` 🔒
Devuelve el lead autenticado con sus cálculos y respuestas de benchmark asociadas.

---

## 3. Benchmark — `/public/benchmark`

### `GET /public/benchmark/instrument`
Devuelve el cuestionario vigente. Público, sin autenticación.

**Response `200`**
```json
{
  "instrumentId": "0192...",
  "version": "1.0",
  "dimensions": [
    {
      "code": "PHYSICAL_OPS_COORDINATION",
      "label": "Coordinación física y operativa",
      "weight": 0.30,
      "questions": [
        {
          "id": "0192...",
          "order": 1,
          "text": "¿Con qué frecuencia se concilian los datos de capacidad física con los de utilización real?",
          "helpText": null,
          "options": [
            { "id": "0192...", "label": "Nunca o de forma manual esporádica", "order": 1 },
            { "id": "0192...", "label": "Trimestralmente, en planilla", "order": 2 },
            { "id": "0192...", "label": "Mensualmente, con proceso definido", "order": 3 },
            { "id": "0192...", "label": "En tiempo real, instrumentado", "order": 4 }
          ]
        }
      ]
    }
  ],
  "estimatedMinutes": 8
}
```
> Los pesos y puntajes de cada opción **no se exponen**: el scoring es del servidor.

### `POST /public/benchmark/responses` 🔒
Inicia una respuesta. Idempotente por lead + instrumento activo.

**Request** `{ "instrumentId": "0192..." }`
**Response `201`** `{ "responseId": "0192...", "startedAt": "...", "status": "IN_PROGRESS" }`

### `PATCH /public/benchmark/responses/{responseId}` 🔒
Guardado parcial. Se llama cada vez que el usuario responde una pregunta. Sin esto, el abandono es alto.

**Request**
```json
{ "answers": [ { "questionId": "0192...", "optionId": "0192..." } ] }
```
**Response `200`** `{ "responseId": "0192...", "answeredCount": 12, "totalQuestions": 25 }`

### `POST /public/benchmark/responses/{responseId}/complete` 🔒
Cierra la respuesta, calcula el score y **encola la generación del PDF**.

**Response `202`**
```json
{
  "responseId": "0192...",
  "completedAt": "2026-07-20T14:45:00Z",
  "globalScore": 62.4,
  "maturityLevel": 3,
  "maturityLabel": "Coordinado",
  "percentile": 0.68,
  "cohortSize": 47,
  "dimensions": [
    { "code": "PHYSICAL_OPS_COORDINATION", "label": "Coordinación física y operativa",
      "score": 71.0, "cohortMedian": 58.0, "gap": 13.0 }
  ],
  "pdfJobId": "0192..."
}
```
> Si `cohortSize` es menor que 5, `percentile` viene `null` y el frontend muestra valores de referencia de industria en su lugar. Es una regla anti-desanonimización, no un error.

### `GET /public/benchmark/responses/{responseId}` 🔒
Recupera el resultado ya calculado.

---

## 4. Generación del PDF — `/public/pdf`

### `GET /public/pdf/jobs/{jobId}` 🔒
Consulta el estado del trabajo. El frontend hace polling cada 2 segundos.

**Response `200`**
```json
{
  "jobId": "0192...",
  "status": "DONE",
  "attempts": 1,
  "documentId": "0192...",
  "downloadUrl": "https://.../benchmark-report.pdf?sig=...",
  "expiresAt": "2026-07-27T14:45:00Z",
  "failureReason": null
}
```
`status`: `PENDING` · `PROCESSING` · `DONE` · `FAILED`

### `GET /public/pdf/documents/{documentId}/download` 🔒
Redirige (`302`) a la URL firmada del storage y registra la descarga.

---

## 5. Reporte de industria — `/public/industry`

### `GET /public/industry/stats`
Agregados de la cohorte que alimentan el reporte público. Sin autenticación, con caché.

**Response `200`**
```json
{
  "cohortSize": 47,
  "updatedAt": "2026-07-20T06:00:00Z",
  "maturityDistribution": [
    { "level": 1, "label": "Inicial", "count": 6, "share": 0.128 }
  ],
  "dimensionMedians": [
    { "code": "PHYSICAL_OPS_COORDINATION", "p25": 42.0, "p50": 58.0, "p75": 74.0 }
  ],
  "idleCapacityRatio": { "p25": 0.19, "p50": 0.28, "p75": 0.41 }
}
```

---

## 6. Outreach (interno) — `/admin/outreach` 🔒 ADMIN

### `POST /admin/outreach/campaigns`
**Request** `{ "name": "Invitación Q3 — operadores Colombia", "subject": "...", "templateCode": "BENCHMARK_INVITE_V1" }`
**Response `201`** `{ "campaignId": "0192...", "status": "DRAFT", "createdAt": "..." }`

### `POST /admin/outreach/campaigns/{campaignId}/contacts`
Sube la lista. `multipart/form-data`, campo `file`. CSV UTF-8 o XLSX. Cabecera obligatoria: `email,nombre,empresa,cargo`. Máximo 5.000 filas / 5 MB.

**Response `200`** — reporte de importación, **sin persistir todavía**:
```json
{
  "importBatchId": "0192...",
  "totalRows": 512,
  "valid": 487,
  "duplicatesInFile": 12,
  "alreadyInCampaign": 6,
  "suppressed": 2,
  "invalid": [ { "row": 44, "email": "juan@", "reason": "FORMATO_INVALIDO" } ]
}
```

### `POST /admin/outreach/campaigns/{campaignId}/contacts/{importBatchId}/confirm`
Confirma la importación previsualizada. **Response `201`** `{ "imported": 487 }`

### `POST /admin/outreach/campaigns/{campaignId}/send`
Encola los envíos respetando la cuota diaria del proveedor.

**Response `202`**
```json
{ "campaignId": "0192...", "queued": 487, "dailyQuota": 300, "estimatedCompletionDate": "2026-07-22" }
```

### `GET /admin/outreach/campaigns/{campaignId}/stats`
Embudo de la campaña.
```json
{
  "campaignId": "0192...", "status": "SENDING",
  "funnel": { "contacts": 487, "sent": 300, "delivered": 291, "opened": 118,
              "clicked": 54, "benchmarkStarted": 31, "benchmarkCompleted": 19,
              "bounced": 9, "unsubscribed": 3 }
}
```

### `POST /public/webhooks/email/{provider}`
Recibe eventos del proveedor de correo. **Público pero con firma verificada.** Devuelve `200` siempre que la firma sea válida, incluso si el evento se ignora — los proveedores reintentan ante cualquier otro código.

### `GET /public/unsubscribe?token=...`
Baja de la lista. Sin autenticación, un solo clic. Requisito legal.

---

## 7. Dashboard interno — `/admin` 🔒 ADMIN / VIEWER

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/admin/auth/login` | Login del equipo interno |
| `POST` | `/admin/auth/logout` | Cierre de sesión |
| `GET` | `/admin/leads` | Listado paginado y filtrable de leads |
| `GET` | `/admin/leads/{id}` | Detalle con cálculos y respuestas |
| `GET` | `/admin/benchmark/responses` | Respuestas acumuladas, paginadas |
| `GET` | `/admin/benchmark/responses/export` | Exporte CSV (UTF-8 con BOM) |
| `GET` | `/admin/calculator/estimates` | Cálculos realizados |
| `GET` | `/admin/dashboard/summary` | Métricas del sistema: conversión, tasa de finalización, PDFs generados |
| `GET` | `/admin/pdf/jobs?status=FAILED` | Trabajos fallidos, para reintento manual |
| `POST` | `/admin/pdf/jobs/{jobId}/retry` | Reintentar generación |

---

## 8. Operación

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/actuator/health` | Healthcheck. Público. Lo usa el monitor de uptime |
| `GET` | `/actuator/info` | Versión desplegada |
| `GET` | `/v3/api-docs` | OpenAPI generado |

---

## 9. Estado de implementación

| Endpoint | Semana objetivo | Estado |
|---|---|---|
| `POST /public/calculator/estimate` | 1 | ☐ |
| `GET /public/calculator/defaults` | 1 | ☐ |
| `POST /public/leads` | 1 | ☐ |
| `POST /public/leads/verify` | 1 | ☐ |
| `GET /public/benchmark/instrument` | 2 | ☐ |
| `POST /public/benchmark/responses` | 2 | ☐ |
| `PATCH /public/benchmark/responses/{id}` | 2 | ☐ |
| `POST /public/benchmark/responses/{id}/complete` | 2 | ☐ |
| `GET /public/industry/stats` | 2 | ☐ |
| `GET /public/pdf/jobs/{id}` | 3 | ☐ |
| `GET /public/pdf/documents/{id}/download` | 3 | ☐ |
| `POST /admin/outreach/*` | 4 | ☐ |
| `POST /public/webhooks/email/{provider}` | 4 | ☐ |
| `/admin/*` (dashboard) | 2–4 | ☐ |

🔒 = requiere autenticación
