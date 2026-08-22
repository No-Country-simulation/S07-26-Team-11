const login = () => {
  cy.clearCookies()
  cy.clearLocalStorage()
  cy.visit('/login')

  cy.get('#email').type('demouser@capacia.app')
  cy.get('#password').type('1SMKs9fH16')
  cy.contains('Entrar').click()

  cy.url().should('include', '/cuenta')
}

const irAlBenchmark = () => {
  cy.contains('Maturity Benchmark').should('be.visible').click()
  cy.url().should('include', '/benchmark')
}

describe('Maturity Benchmark', () => {

 it('TC022 - Verificar inicio de sesión con credenciales válidas', () => {
  cy.clearCookies()
  cy.clearLocalStorage()

  cy.visit('/login')

  cy.get('#email').type('demouser@capacia.app')
  cy.get('#password').type('1SMKs9fH16')
  cy.contains('Entrar').click()

  cy.url().should('include', '/cuenta')
  cy.contains('demouser@capacia.app').should('be.visible')

  cy.screenshot('TC022-login-credenciales-validas')
})

  it('TC023 - Verificar acceso al Maturity Benchmark desde la cuenta', () => {
    login()

    irAlBenchmark()

    cy.contains('MATURITY BENCHMARK').should('be.visible')

    cy.screenshot('TC023-acceso-maturity-benchmark')
  })


  it('TC024 - Verificar inicio del cuestionario del Maturity Benchmark', () => {
    login()
    irAlBenchmark()

    cy.contains('Empezar el Benchmark').should('be.visible').click()

    cy.url().should('include', '/benchmark/cuestionario')

    cy.screenshot('TC024-inicio-cuestionario-benchmark')
  })


  it('TC025 - Verificar selección de respuesta y avance a la siguiente pregunta', () => {
    login()
    irAlBenchmark()

    cy.contains('Empezar el Benchmark').click()

    cy.contains('VISIBILIDAD').should('be.visible')
    cy.contains('Pregunta 1 de 2').should('be.visible')

    cy.contains('Siguiente →').should('be.disabled')

    cy.contains(
      'Vemos utilización agregada por rack o fila, sin detalle por carga.'
    ).click()

    cy.contains('Siguiente →')
      .should('not.be.disabled')
      .click()

    cy.contains('Pregunta 2 de 2').should('be.visible')

    cy.screenshot('TC025-seleccion-respuesta-avance-pregunta-2')
  })


  it('TC026 - Verificar selección de respuesta en la segunda pregunta del Benchmark', () => {
    login()
    irAlBenchmark()

    cy.contains('Empezar el Benchmark').click()

    cy.contains(
      'Vemos utilización agregada por rack o fila, sin detalle por carga.'
    ).click()

    cy.contains('Siguiente →').click()

    cy.contains('Pregunta 2 de 2').should('be.visible')

    cy.contains(
      '¿Cómo se enteran los responsables del sitio de que hay capacidad ociosa?'
    ).should('be.visible')

    cy.contains('No hay un mecanismo').should('be.visible')
    cy.contains('Un informe manual').should('be.visible')
    cy.contains('Un reporte periódico automático').should('be.visible')
    cy.contains('Un dashboard vivo').should('be.visible')

    cy.contains('Siguiente →').should('be.disabled')

    cy.contains(
      'Un reporte periódico automático, revisado por el equipo.'
    ).click()

    cy.contains('Siguiente →')
      .should('not.be.disabled')
      .click()

    cy.screenshot('TC026-seleccion-respuesta-pregunta-2')
  })

})

const iniciarBenchmark = () => {
  login()
  irAlBenchmark()
  cy.contains('Empezar el Benchmark').click()
}

const llegarAEficienciaEnergetica = () => {
  iniciarBenchmark()

  // Visibilidad - Pregunta 1
  cy.contains(
    'Vemos utilización agregada por rack o fila, sin detalle por carga.'
  ).click()
  cy.contains('Siguiente →').click()

  // Visibilidad - Pregunta 2
  cy.contains(
    'Un reporte periódico automático, revisado por el equipo.'
  ).click()
  cy.contains('Siguiente →').click()

  cy.contains('EFICIENCIA ENERGÉTICA').should('be.visible')
}

const llegarAGobernanzaDatos = () => {
  llegarAEficienciaEnergetica()

  // Eficiencia Energética - Pregunta 1
  cy.contains(
    'El margen se ajusta por tipo de carga, con revisión anual.'
  ).click()
  cy.contains('Siguiente →').click()

  // Eficiencia Energética - Pregunta 2
  cy.contains(
    'Se mide mensualmente y se compara contra el mes anterior.'
  ).click()
  cy.contains('Siguiente →').click()

  cy.contains('GOBERNANZA DE DATOS').should('be.visible')
}

const llegarAAutomatizacion = () => {
  llegarAGobernanzaDatos()

  // Gobernanza de Datos - Pregunta 1
  cy.contains(
    'Cada carga tiene un dueño formal y una política de baja con revisión periódica.'
  ).click()
  cy.contains('Siguiente →').click()

  // Gobernanza de Datos - Pregunta 2
  cy.contains(
    'El inventario se sincroniza automáticamente con la infraestructura real.'
  ).click()
  cy.contains('Siguiente →').click()

  cy.contains('AUTOMATIZACIÓN').should('be.visible')
}

it('TC027 - Verificar respuesta de la pregunta 1 de Eficiencia Energética', () => {
  llegarAEficienciaEnergetica()

  cy.contains('Pregunta 1 de 2').should('be.visible')

  cy.contains(
    'El margen se ajusta por tipo de carga, con revisión anual.'
  ).click()

  cy.contains('Siguiente →')
    .should('not.be.disabled')
    .click()

  cy.contains('Pregunta 2 de 2').should('be.visible')

  cy.screenshot('TC027-eficiencia-energetica-pregunta-1')
})

it('TC028 - Verificar respuesta de la pregunta 2 de Eficiencia Energética', () => {
  llegarAEficienciaEnergetica()

  cy.contains(
    'El margen se ajusta por tipo de carga, con revisión anual.'
  ).click()
  cy.contains('Siguiente →').click()

  cy.contains('Pregunta 2 de 2').should('be.visible')

  cy.contains(
    'Se mide mensualmente y se compara contra el mes anterior.'
  ).click()

  cy.contains('Siguiente →')
    .should('not.be.disabled')
    .click()

  cy.contains('GOBERNANZA DE DATOS').should('be.visible')

  cy.screenshot('TC028-eficiencia-energetica-pregunta-2')
})

it('TC029 - Verificar respuesta de la pregunta 1 de Gobernanza de Datos', () => {
  llegarAGobernanzaDatos()

  cy.contains('Pregunta 1 de 2').should('be.visible')

  cy.contains(
    'Cada carga tiene un dueño formal y una política de baja con revisión periódica.'
  ).click()

  cy.contains('Siguiente →')
    .should('not.be.disabled')
    .click()

  cy.contains('Pregunta 2 de 2').should('be.visible')

  cy.screenshot('TC029-gobernanza-datos-pregunta-1')
})

it('TC030 - Verificar respuesta de la pregunta 2 de Gobernanza de Datos', () => {
  llegarAGobernanzaDatos()

  cy.contains(
    'Cada carga tiene un dueño formal y una política de baja con revisión periódica.'
  ).click()
  cy.contains('Siguiente →').click()

  cy.contains('Pregunta 2 de 2').should('be.visible')

  cy.contains(
    'El inventario se sincroniza automáticamente con la infraestructura real.'
  ).click()

  cy.contains('Siguiente →')
    .should('not.be.disabled')
    .click()

  cy.contains('AUTOMATIZACIÓN').should('be.visible')

  cy.screenshot('TC030-gobernanza-datos-pregunta-2')
})

it('TC031 - Verificar respuesta de la pregunta 1 de Automatización', () => {
  llegarAAutomatizacion()

  cy.contains('Pregunta 1 de 2').should('be.visible')

  cy.contains(
    'El apagado programado cubre la mayoría de los ambientes no productivos.'
  ).click()

  cy.contains('Siguiente →')
    .should('not.be.disabled')
    .click()

  cy.contains('Pregunta 2 de 2').should('be.visible')

  cy.screenshot('TC031-automatizacion-pregunta-1')
})


it('TC032 - Verificar respuesta de la pregunta 2 de Automatización', () => {
  llegarAAutomatizacion()

  cy.contains(
    'El apagado programado cubre la mayoría de los ambientes no productivos.'
  ).click()
  cy.contains('Siguiente →').click()

  cy.contains('Pregunta 2 de 2').should('be.visible')

  cy.contains(
    'Auto escalado extendido a la mayoría de las cargas, con políticas definidas.'
  ).click()

  cy.contains('Ver resultados')
    .should('not.be.disabled')
    .click()

  cy.screenshot('TC032-automatizacion-pregunta-2')
})

it('TC033 - Verificar descarga del informe PDF', () => {
  llegarAAutomatizacion()

  cy.contains(
    'El apagado programado cubre la mayoría de los ambientes no productivos.'
  ).click()
  cy.contains('Siguiente →').click()

  cy.contains(
    'Auto escalado extendido a la mayoría de las cargas, con políticas definidas.'
  ).click()

  cy.contains('Ver resultados').click()

  cy.contains('Descargar informe PDF')
    .should('be.visible')
    .click()

  cy.screenshot('TC033-descarga-informe-pdf')
})



