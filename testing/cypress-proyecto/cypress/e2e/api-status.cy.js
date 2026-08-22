it('TC021 - Verificar estado de la API', () => {

  cy.visit('https://capacia.vercel.app/api-status')

  // Verificamos que la página cargue correctamente
  cy.contains('Estado de la conexión')
    .should('be.visible')

  // Verificamos API
  cy.contains('API')
    .should('be.visible')

  cy.contains('ok')
    .should('be.visible')

  cy.contains('200')
    .first()
    .should('be.visible')

  // Verificamos base de datos
  cy.contains('Base de datos')
    .should('be.visible')

  cy.contains('UP')
    .should('be.visible')

  cy.contains('Volver a verificar')
    .should('be.visible')

  cy.screenshot('TC021-api-status')
})