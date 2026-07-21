# .github/

Directorio reservado para la configuracion de GitHub.

**Todavia vacio a proposito.** No hay CI/CD en la Semana 0.
Ver `docs/ci-cd-notas.md` para lo que hay que decidir antes de activarlo,
en particular la eleccion entre repositorio publico y privado, que
condiciona tanto los minutos de Actions como la proteccion de ramas.

Cuando se active, aqui van:

```
workflows/ci.yml          build y tests en cada PR hacia dev
workflows/deploy.yml      despliegue a staging al mergear a main
PULL_REQUEST_TEMPLATE.md  plantilla de PR
ISSUE_TEMPLATE/           plantillas de issue y de reporte de error
CODEOWNERS                revisores automaticos por directorio
```
