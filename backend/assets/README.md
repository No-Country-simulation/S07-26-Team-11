# backend/assets/

Recursos que se incrustan en el PDF institucional.

Se empaquetan dentro del JAR (ver el bloque `<resources>` de `backend/pom.xml`),
asi que funcionan igual en local y en el contenedor de Render, sin variables de
entorno ni rutas absolutas.

| Archivo | Uso |
|---|---|
| `logo.svg` | Logotipo del encabezado. Se incrusta como data URI. |
| `fonts/*.ttf` | Opcional. Fuentes propias del documento. |

## Logo

Formatos admitidos: `svg`, `png`, `jpg`, `gif`. El nombre se configura en
`app.documents.logo-file` (`application.yml`).

Si el archivo no existe la aplicacion **arranca igual**: se registra un WARN y
el documento se genera sin logo. Es a proposito, la misma regla que el resto de
la infraestructura del proyecto.

## Fuentes

Convencion de nombre, heredada de Google Fonts: `<Familia>-<Peso>[Italic].ttf`

```
Inter-Regular.ttf     -> font-family: Inter; font-weight: 400
Inter-SemiBold.ttf    -> font-family: Inter; font-weight: 600
Inter-BoldItalic.ttf  -> font-family: Inter; font-weight: 700; font-style: italic
```

Pesos reconocidos: thin(100), extralight(200), light(300), regular(400),
medium(500), semibold(600), bold(700), extrabold(800), black(900).

Sin fuentes propias el motor usa las 14 fuentes base del PDF (Helvetica, Times,
Courier), que cubren el espanol completo incluidas tildes y enie.

**Licencia:** solo fuentes cuya licencia permita incrustarlas en un documento
(SIL OFL y Apache 2.0 lo permiten). No subir fuentes de pago sin verificarlo.
