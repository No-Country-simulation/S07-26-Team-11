# Arquitectura del PDF Generator

## Flujo

```
POST /api/v1/public/benchmark/responses/{id}/complete
  |
  |-- transaccion: guarda respuestas + calcula scores y percentil
  |-- inserta fila en pdf_jobs (status = PENDING)
  |-- publica evento de aplicacion (AFTER_COMMIT)
  '-- responde 202 Accepted con jobId          <- NO bloquea al usuario

Worker (@Scheduled cada 5 s, perfil "worker"):
  1. SELECT ... FROM pdf_jobs WHERE status = 'PENDING'
     ORDER BY created_at FOR UPDATE SKIP LOCKED LIMIT 1
  2. carga scores + percentiles + KPIs de la calculadora del mismo lead
  3. genera las graficas como SVG en el servidor (gauge, radar, barra de posicion)
  4. Thymeleaf renderiza el HTML del template con los datos
  5. Jsoup normaliza el HTML a XHTML bien formado
  6. Open HTML to PDF -> byte[]
  7. sube al object storage con key derivada del responseId
  8. registra pdf_documents, marca el job DONE
  9. encola el correo con la URL firmada (expira en 7 dias)

Frontend: GET /api/v1/public/pdf/jobs/{id} en polling cada 2 s
```

## Decisiones

| Decision | Razon |
|---|---|
| Cola en PostgreSQL con `FOR UPDATE SKIP LOCKED` | Cero infraestructura extra; correcto con varios workers; Redis es innecesario a esta escala |
| Generacion asincrona | Renderizar un PDF puede tomar segundos. Nunca dentro del request HTTP |
| Idempotencia por `responseId` | Dos ejecuciones del mismo job producen el mismo `storage_key`, no dos PDFs |
| Reintentos con backoff, maximo 3 | Los fallos transitorios de red son normales. Tras 3 intentos queda `FAILED` y visible en el dashboard |
| `template_version` guardado por documento | Permite regenerar historicos con la plantilla correcta |
| Contacto del fundador en configuracion | Va a cambiar al menos una vez |
| Graficas como SVG generado en servidor | El motor no ejecuta JavaScript |
| Un solo PDF a la vez por worker | Con 512 MB de RAM, PDFBox necesita margen |

## Restricciones del motor (leer antes de maquetar)

Open HTML to PDF **no soporta flexbox ni CSS grid y no ejecuta JavaScript**. Requiere XHTML bien formado.

**Se puede usar:** layout con tablas y bloques, `@page` con margenes y contadores de pagina,
`page-break-inside: avoid`, `@font-face` con fuentes incrustadas, SVG.

**No se puede usar:** `display: flex`, `display: grid`, `position: sticky`, cualquier libreria de graficas en JavaScript.

## Estructura de paginas del documento (propuesta)

1. Portada con nombre del operador y fecha
2. Resumen ejecutivo (una pagina)
3. Posicion en el benchmark: gauge + percentil
4. Detalle por dimension: radar + tabla de brechas
5. KPIs de la calculadora
6. Recomendaciones priorizadas
7. Bloque de contacto del fundador
8. Metodologia y nota legal

## Pruebas

- Perfiles sinteticos: madurez baja, media y alta; con y sin datos de calculadora; cohorte insuficiente
- Verificacion de texto extraible: el nombre de la empresa debe aparecer en la pagina 1
- Regresion visual: rasterizar paginas a PNG y comparar contra referencias
- Casos borde: nombres muy largos, tildes y enie, valores en cero
