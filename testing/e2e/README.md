# e2e/ — Pruebas de extremo a extremo

Cypress. El QA elige la herramienta; esta es la propuesta del arquetipo.

```bash
npm install
npx cypress open
```

## Flujos prioritarios

1. `calculadora-a-email.cy.ts` — calcular, dejar correo, verificar enlace
2. `benchmark-completo.cy.ts` — responder, cerrar, ver resultado, descargar PDF
3. `guardado-parcial.cy.ts` — responder a medias, salir, volver y encontrar el avance

## Notas

- Las pruebas E2E se ejecutan contra **local** durante el desarrollo y contra
  **staging** antes de cada demo.
- No dependas de datos que otro test crea: cada prueba prepara lo que necesita.
- Los correos son dificiles de probar en E2E. Para el magic link, expon en el
  perfil de desarrollo un endpoint que devuelva el ultimo token generado,
  **deshabilitado en staging**.
