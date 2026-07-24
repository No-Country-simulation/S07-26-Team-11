# CONTRIBUTING.md — Flujo de trabajo con Git

Este documento es la referencia completa del flujo de Git. Si es tu primera vez, sigue la sección 2 paso a paso.

---

## 1. El modelo en una imagen

```
main    ●────────────────●────────────────●────────────────●
        │  merge sem. 0  │  merge sem. 1  │  merge sem. 2  │   (solo el encargado de la semana)
        │                │                │                │
dev     ●──●──●──●──●──●──●──●──●──●──●──●──●──●──●──●──●──●
           ▲     ▲     ▲        ▲     ▲
           │     │     │        │     │        (Pull Requests)
   backend/…  frontend/…   database/…   design/…   testing/…
```

- **`main`** — La rama estable. Solo recibe merges desde `dev`, **una vez por semana**, hechos por el encargado rotativo. Es lo que se muestra al cliente.
- **`dev`** — La rama de integración. Todo el trabajo del equipo confluye aquí. Puede romperse ocasionalmente; se arregla el mismo día.
- **Ramas de trabajo** — Salen de `dev` y vuelven a `dev` por Pull Request. Son de vida corta: idealmente menos de 3 días.

---

## 2. Configuración inicial (una sola vez)

```bash
# Clonar el repositorio
git clone git@github.com:No-Country-simulation/S07-26-Team-11.git
# https: git clone https://github.com/No-Country-simulation/S07-26-Team-11.git
cd S07-26-Team-11/

# Configurar tu identidad (usa el mismo email de tu cuenta de GitHub)
git config user.name "Tu Nombre"
git config user.email "tu@email.com"

# Traer la rama dev y posicionarte en ella
git fetch origin
git checkout dev
```

**Verifica que estás en `dev` antes de empezar cualquier cosa:**

```bash
git branch --show-current    # debe imprimir: dev
```

---

## 3. Convención de nombres de ramas

Formato: **`area/descripcion-corta-en-kebab-case`**

| Área | Prefijo | Ejemplos |
|---|---|---|
| Backend | `backend/` | `backend/lead-capture-endpoint`, `backend/pdf-worker` |
| Frontend | `frontend/` | `frontend/calculator-wizard`, `frontend/admin-dashboard` |
| Base de datos | `database/` | `database/initial-schema`, `database/outreach-tables` |
| Pruebas | `testing/` | `testing/postman-collection`, `testing/e2e-benchmark-flow` |
| Diseño | `design/` | `design/design-tokens`, `design/pdf-template` |
| Documentación | `docs/` | `docs/api-contract-v1`, `docs/adr-0003-email-provider` |
| Corrección urgente | `fix/` | `fix/staging-cors-error` |

Sin tildes, sin espacios, sin mayúsculas, sin nombres de personas. Que el nombre diga qué hace la rama.

---

## 4. Ciclo de trabajo diario

### 4.1 Empezar una tarea

```bash
# 1. Ponerte al día con dev
git checkout dev
git pull origin dev

# 2. Crear tu rama a partir de dev
git checkout -b backend/lead-capture-endpoint

# 3. Trabajar y guardar avances
git add .
git commit -m "feat(backend): agregar endpoint de captura de email"

# 4. Subir tu rama por primera vez
git push -u origin backend/lead-capture-endpoint

# (siguientes veces basta con)
git push
```

### 4.2 Mantenerte al día mientras trabajas

Si tu rama vive más de un día, tráete los cambios de `dev` con frecuencia. Esto evita conflictos grandes al final:

```bash
git checkout dev
git pull origin dev
git checkout backend/lead-capture-endpoint
git merge dev
# resolver conflictos si los hay, luego:
git push
```

### 4.3 Abrir el Pull Request

1. En GitHub: **Pull requests → New pull request**.
2. **base: `dev`** ← **compare: `tu-rama`**. Verifica esto: el error más común es apuntar el PR a `main`.
3. Título con el mismo formato que los commits: `feat(backend): endpoint de captura de email`.
4. En la descripción: qué hace, cómo probarlo, y qué queda pendiente.
5. Asigna al menos un revisor.
6. Espera la aprobación. **No hagas merge de tu propio PR sin aprobación.**

### 4.4 Después del merge

```bash
git checkout dev
git pull origin dev
git branch -d backend/lead-capture-endpoint          # borrar rama local
git push origin --delete backend/lead-capture-endpoint   # borrar rama remota
```

Borrar las ramas ya mergeadas no es cosmético: con 8 personas y 5 semanas, un repositorio con 40 ramas muertas hace que nadie encuentre nada.

---

## 5. Convención de commits

Formato **Conventional Commits**: `tipo(alcance): descripción en imperativo`

```
feat(backend): agregar endpoint de captura de email
fix(frontend): corregir validación del formulario de la calculadora
docs(api): documentar endpoints del benchmark
chore(database): agregar migración de tablas de outreach
test(e2e): agregar flujo completo calculadora a PDF
style(design): actualizar tokens de la paleta
refactor(backend): extraer servicio de scoring
```

**Tipos:** `feat`, `fix`, `docs`, `chore`, `test`, `style`, `refactor`.
**Alcances:** `backend`, `frontend`, `database`, `testing`, `design`, `docs`, `api`, `pdf`, `outreach`.

Reglas: en español, en imperativo ("agregar", no "agregado"), menos de 72 caracteres, un commit por cambio lógico. Nada de `wip`, `cambios`, `fix2` ni `asdf`.

---

## 6. Reglas de Pull Request

| Regla | Detalle |
|---|---|
| Base siempre `dev` | Nunca abras un PR contra `main` salvo que seas el encargado del merge semanal |
| Mínimo una aprobación | Dos aprobaciones si el PR toca `docs/API.md`, `database/migrations/` o la arquitectura, y una de ellas debe ser del arquitecto |
| Solo tu directorio | Si tu PR toca el directorio de otra persona, etiquétala como revisora obligatoria |
| PRs pequeños | Un PR de 40 archivos no se revisa, se aprueba a ciegas. Divide el trabajo |
| Sin secretos | Revisa el diff antes de subir. Un `.env` en el diff es motivo de rechazo automático |
| `.env.example` actualizado | Si agregaste una variable de entorno, agrégala también al ejemplo |
| Documentación en el mismo PR | Si cambiaste un endpoint, actualiza `docs/API.md` en el mismo PR. No en "otro PR después" |

### Cómo revisar un PR

Comenta el código, no a la persona. Sé explícito sobre qué bloquea y qué es opinión: usa **"bloqueante:"** para lo que impide el merge y **"sugerencia:"** para lo demás. Un comentario sin esa marca genera dudas y retrasa el PR un día entero.

---

## 7. Merge semanal a `main`

Lo hace **una sola persona por semana**, según la rotación publicada en [`PROJECT.md`](PROJECT.md). Es el último paso de la semana, después de la revisión de sprint.

```bash
# 1. Asegurarte de que dev está estable y todo el mundo mergeó lo suyo
git checkout dev
git pull origin dev

# 2. Verificar que compila y arranca
cd backend && ./mvnw clean package && cd ..
cd frontend && npm ci && npm run build && cd ..

# 3. Crear el PR de dev hacia main desde GitHub
#    base: main  <-  compare: dev
#    Título: "release: cierre semana N"
#    Descripción: lista de lo que entra esta semana

# 4. Tras la aprobación y el merge, etiquetar la versión
git checkout main
git pull origin main
git tag -a v0.N.0 -m "Cierre de la semana N"
git push origin --tags
```

**Si `dev` está roto el día del merge, no se hace el merge.** Se arregla primero. Un `main` roto durante la demo es el peor escenario posible.

---

## 8. Separar el trabajo por proyecto y rama

Cada entregable tiene su directorio y su prefijo de rama. Estas son las ramas iniciales de la Semana 0, una por área. Las crea el arquitecto o el PM el primer día:

```bash
git checkout dev

# Backend
git checkout -b backend/arquetipo-inicial
git push -u origin backend/arquetipo-inicial
git checkout dev

# Frontend
git checkout -b frontend/arquetipo-inicial
git push -u origin frontend/arquetipo-inicial
git checkout dev

# Base de datos
git checkout -b database/esquema-inicial
git push -u origin database/esquema-inicial
git checkout dev

# Pruebas
git checkout -b testing/plan-de-pruebas
git push -u origin testing/plan-de-pruebas
git checkout dev

# Diseño
git checkout -b design/sistema-de-diseno
git push -u origin design/sistema-de-diseno
git checkout dev

# Documentación
git checkout -b docs/contratos-api
git push -u origin docs/contratos-api
git checkout dev
```

**Importante:** estas ramas iniciales son de arranque, no permanentes. Se mergean a `dev` al cierre de la Semana 0 y a partir de ahí cada tarea nueva crea su propia rama corta. **No conviertas `backend/arquetipo-inicial` en una rama eterna** donde todo el backend vive: eso reproduce el problema que el flujo intenta evitar.

### Si solo quieres trabajar en un directorio (repositorio grande)

Git permite clonar sin descargar todo el árbol de archivos:

```bash
git clone --filter=blob:none --sparse git@github.com:No-Country-simulation/S07-26-Team-11.git
cd [nombre-del-repo]
git sparse-checkout set backend docs
```

Opcional. Para un repositorio de este tamaño no hace falta.

---

## 9. Problemas frecuentes

| Problema | Solución |
|---|---|
| **Hice commits en `dev` por error** | `git branch mi-rama` → `git reset --hard origin/dev` → `git checkout mi-rama`. Tus commits quedan a salvo en `mi-rama` |
| **Mi PR apunta a `main`** | En GitHub, edita el PR y cambia la rama base a `dev`. No hace falta cerrarlo |
| **Tengo conflictos al mergear `dev`** | Abre los archivos marcados con `<<<<<<<`, decide qué queda, borra los marcadores, `git add` y `git commit`. Si el conflicto es en código de otra persona, **pregúntale antes de resolverlo por tu cuenta** |
| **Subí un archivo `.env`** | Avisa en el chat, **rota las credenciales**, y luego `git rm --cached .env` + commit. Borrarlo no basta: queda en el historial |
| **Mi rama quedó muy atrás de `dev`** | `git merge dev` desde tu rama. Si los conflictos son inmanejables, a veces es más rápido crear una rama nueva desde `dev` y reaplicar tus cambios |
| **Borré algo sin querer** | `git reflog` muestra todo lo que hiciste. Casi nada se pierde de verdad en Git. Pregunta antes de intentar arreglarlo con comandos que no conoces |
| **No sé qué comando usar** | Pregunta en el chat **antes** de ejecutar algo con `--force`. `git push --force` sobre `dev` o `main` puede borrar el trabajo de todo el equipo |

> **Regla de oro:** nunca uses `git push --force` sobre `dev` ni sobre `main`. Sobre tu propia rama, y solo si sabes exactamente qué hace, `git push --force-with-lease`.

---

## 10. Archivo `.gitignore`

Ya está configurado en la raíz. Antes de tu primer commit, verifica que no estás subiendo nada de esto:

- Archivos `.env` con valores reales
- `node_modules/`, `target/`, `build/`, `.next/`
- Archivos de configuración de tu IDE (`.idea/`, `.vscode/` con configuración personal)
- Archivos grandes de diseño (`.psd`, `.fig`, `.ai`) — esos van enlazados desde `design/README.md`, no versionados
- Reportes y logs generados automáticamente
- **Archivos de herramientas de IA** — ver la sección 11 abajo

---

## 11. Herramientas de IA — archivos que no entran al repositorio

Muchas herramientas de desarrollo asistido por IA (Claude, GitHub Copilot / Codex, Kiro, Cursor, Continue, Antigravity, y otras) crean archivos de configuración en el proyecto para darle contexto al modelo. **Ninguno de esos archivos entra a este repositorio.**

Varias están en el `.gitignore`. Esta sección explica por qué y qué hacer si uno escapa.

### Está prohibido por las siguentes razones

**1. Fuga de información sensible sin darse cuenta.**
Estos archivos a menudo contienen o terminan conteniendo cosas que no deben ser públicas: rutas internas del sistema, nombres de servicios de staging, fragmentos de código con lógica de negocio, variables de entorno copiadas como ejemplo, o simplemente descripciones del sistema que no queremos que sean públicas. La herramienta le pide al desarrollador que "describa el proyecto" y el desarrollador escribe. Ese texto queda en el archivo y en el historial de Git para siempre.

**2. Prevenir posibles Skills o instrucciones maliciosas.**
Las herramientas usan archivos de "skills" o "reglas" (`.kiro/`, `skills/`, `AGENTS.md`, `.claude/`, etc.) para indicarle al modelo cómo comportarse. Si ese archivo entra al repositorio, cualquier persona con acceso puede modificarlo y hacer que la IA le dé instrucciones perjudiciales a otros miembros del equipo sin que lo noten: por ejemplo, que "siempre omita las validaciones de seguridad" o que "agregue este comentario a todos los archivos que genere". Es una superficie de ataque de cadena de suministro (supply chain attack) dentro del propio equipo.

**3. Prompt injection a través del repositorio.**
Si el repositorio queda público, un archivo de instrucciones de IA visible en el repo puede ser leído por bots que buscan exactamente eso para inyectar instrucciones al modelo de quien clone el proyecto.

**4. Dependencia implícita de una herramienta externa.**
Si el flujo de trabajo del equipo queda codificado en archivos de una herramienta propietaria, el miembro que no usa esa herramienta queda en desventaja sin saberlo. Las convenciones del proyecto están en este repositorio, en texto plano, accesibles sin instalar nada.

**5. El contexto del proyecto ya está documentado.**
`CLAUDE.md` en la raíz existe para que cualquier miembro pueda darle contexto a su asistente de IA manualmente, sin que ese contexto quede versionado. Úsalo como referencia al configurar tu herramienta en tu máquina local, pero no subas la configuración local de la herramienta.

### Archivos y directorios bloqueados

El `.gitignore` ya cubre todos estos patrones. No tienes que hacer nada — pero sí tienes que saber que existen para reconocerlos si aparecen en un diff:

| Herramienta | Archivos/directorios que genera |
|---|---|
| Claude / Claude Code | `CLAUDE.md` en subdirectorios, `.claude/`, `claude_files/` |
| GitHub Copilot / Codex | `.github/copilot-instructions.md`, `AGENTS.md` |
| Kiro | `.kiro/`, `.kiro/specs/`, `.kiro/steering/` |
| Cursor | `.cursorrules`, `.cursor/` |
| Antigravity | `.antigravity/`, `skills/` |
| Genérico | `AGENTS.md`, `skills/`, cualquier `*.rules` en la raíz |

> **El `CLAUDE.md` en la raíz del proyecto es la única excepción** — ese archivo lo mantiene el arquitecto como documento de referencia del equipo. Los `CLAUDE.md` que las herramientas crean automáticamente en subdirectorios sí están bloqueados.

### Qué hacer si descubres uno en un PR

1. **Rechaza el PR** con comentario: `"bloqueante: archivo de configuración de IA — ver sección 11 de CONTRIBUTING.md"`.
2. Avisa en el chat del equipo para que la persona sepa que debe quitarlo antes de pedir aprobación.
3. No lo apruebes aunque "solo sea un archivo de texto sin nada importante" — el historial de Git no se borra fácilmente, y no podemos saber qué habrá ahí en tres semanas.

### Qué hacer si ya entró al repositorio (eliminación del historial)

Si uno de estos archivos ya pasó el PR y está en `dev` o en `main`, borrarlo con un commit normal **no es suficiente**: el archivo sigue en el historial y cualquiera puede recuperarlo con `git checkout <commit-anterior> -- ruta/al/archivo`.

**Procedencia en este orden:**

**Paso 1 — Verificar qué contiene antes de actuar.**
```bash
# Ver el contenido actual del archivo comprometido
git show HEAD:ruta/al/archivo.md

# Ver en qué commits apareció
git log --all --full-history -- ruta/al/archivo.md
```
Si no contiene nada sensible (solo texto genérico sin credenciales, rutas ni lógica interna), es suficiente con borrarlo en un commit normal y pasar a agregar el patrón al `.gitignore`. El procedimiento de reescritura del historial es costoso para el equipo — aplícalo solo cuando valga la pena.

**Paso 2 — Si sí contiene información sensible: reescribir el historial con `git filter-repo`.**

> ⚠️ Esto reescribe los hashes de todos los commits afectados. Todo el equipo tiene que re-clonar después. Coordínalo en el chat antes de ejecutar.

```bash
# Instalar git-filter-repo (una sola vez)
pip install git-filter-repo        # o: brew install git-filter-repo

# En una copia limpia del repositorio (no tu copia de trabajo habitual)
git clone git@github.com:No-Country-simulation/S07-26-Team-11.git S07-26-Team-11-clean
cd S07-26-Team-11-clean

# Eliminar el archivo de TODAS las ramas y de TODO el historial
git filter-repo --path ruta/al/archivo.md --invert-paths

# Verificar que desapareció
git log --all --full-history -- ruta/al/archivo.md   # debe estar vacío

# Subir el historial reescrito (requiere force-push coordinado)
git push origin --force --all
git push origin --force --tags
```

**Paso 3 — Después de la reescritura, todos los miembros del equipo deben:**
```bash
# NO hacer git pull: eso mezcla el historial viejo con el nuevo y lo rompe
# En cambio, re-clonar desde cero:
cd ..
rm -rf S07-26-Team-11
git clone git@github.com:No-Country-simulation/S07-26-Team-11.git
cd S07-26-Team-11
git checkout dev
```

**Paso 4 — Si el repositorio es público, también hay que:**
- Ir a **Settings → Danger Zone → Delete this repository** pedirle al Architect Solution la restauración al checkpoint anterior más reciente.

### Cómo usar herramientas de IA en este proyecto sin violar esta regla

Puedes y debes usar la herramienta que prefieras. La restricción es sobre lo que llega al repositorio, no sobre lo que haces en tu máquina.

- **Para darle contexto al modelo**, usa el `CLAUDE.md` de la raíz como referencia y pégalo o cópialo en la configuración local de tu herramienta. Ese archivo existe exactamente para eso.
- **Configura tu herramienta en modo "local"**, sin sincronización al repositorio. La mayoría tienen esa opción.
- **Agrega los archivos de tu herramienta a tu `.gitignore` global** (no al del proyecto), para que nunca aparezcan en ningún `git status`:
  ```bash
  # Editar el gitignore global de tu máquina
  git config --global core.excludesFile ~/.gitignore_global

  # Agregar ahí los patrones de tu herramienta, por ejemplo:
  echo ".claude/" >> ~/.gitignore_global
  echo ".kiro/" >> ~/.gitignore_global
  echo ".cursor" >> ~/.gitignore_global
  echo ".cursorrules" >> ~/.gitignore_global
  echo ".continue/" >> ~/.gitignore_global
  echo ".antigravity/" >> ~/.gitignore_global
  echo "skills/" >> ~/.gitignore_global
  echo "AGENTS.md" >> ~/.gitignore_global
  ```
  Esto hace que Git los ignore en todos tus proyectos, no solo en este.
- **Antes de cualquier `git add .`**, revisa con `git status` o `git diff --name-only` que no haya archivos inesperados. Un segundo de atención aquí evita la limpieza de historial descrita arriba.