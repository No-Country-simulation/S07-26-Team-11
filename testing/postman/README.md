# postman/

Colecciones y entornos exportados de Postman.

```
coleccion-dcplatform.json     coleccion principal (importada desde OpenAPI)
entorno-local.json            baseUrl = http://localhost:8080/api/v1
entorno-staging.json          baseUrl = [por definir]
```

**Nunca exportes un entorno con tokens o contrasenas reales dentro.**
Postman los incluye en el JSON. Antes de subir un entorno, vacia los valores
sensibles y deja solo los nombres de las variables.
