describe('CAPACIA - Flujo principal', () => {

  // TC001 - Verificar carga de la página de inicio
  it('TC001 - Verificar carga de la página de inicio', () => {

    // Abrimos la página principal de CAPACIA
    cy.visit('https://capacia.vercel.app/')

    // Verificamos que el botón "Calculá tu capacidad" sea visible
    cy.contains('Calculá tu capacidad')
      .should('be.visible')

    // Verificamos que el botón central "Calculá tu capacidad ahora" sea visible
    cy.contains('Calculá tu capacidad ahora')
      .should('be.visible')

    // Verificamos que el botón "Visualiza el Report" sea visible
    cy.contains('Visualiza el Report')
      .should('be.visible')

  })

})

// TC002 - Verificar navegación a la calculadora desde el botón superior
it('TC002 - Verificar navegación a la calculadora desde el botón superior', () => {

  // Paso 1: Ingresamos a la página principal de CAPACIA
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Buscamos el botón "Calculá tu capacidad" y hacemos clic
  cy.contains('Calculá tu capacidad')
    .click()

  // Paso 3: Verificamos que la URL sea la de la calculadora
  cy.url()
    .should('eq', 'https://capacia.vercel.app/calculadora')

})

// TC003 - Verificar navegación a la calculadora desde el botón central
it('TC003 - Verificar navegación a la calculadora desde el botón central', () => {

  // Paso 1: Ingresamos a la página principal de CAPACIA
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Buscamos el botón central "Calculá tu capacidad ahora" y hacemos clic
  cy.contains('Calculá tu capacidad ahora')
    .click()

  // Paso 3: Verificamos que la URL sea la de la calculadora
  cy.url()
    .should('eq', 'https://capacia.vercel.app/calculadora')

})

// TC004 - Verificar navegación al reporte desde el botón "Visualiza el Report"
it('TC004 - Verificar navegación al reporte desde el botón "Visualiza el Report"', () => {

  // Paso 1: Ingresamos a la página principal de CAPACIA
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Buscamos el botón "Visualiza el Report" y hacemos clic
  cy.contains('Visualiza el Report')
    .click()

  // Paso 3: Verificamos que la URL sea la del reporte
  cy.url()
    .should('eq', 'https://capacia.vercel.app/reporte')

})

// TC005 - Verificar que la calculadora muestra correctamente sus campos y botón
it('TC005 - Verificar que la calculadora muestra correctamente sus campos y botón', () => {

  // Paso 1: Ingresamos a la página principal de CAPACIA
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Hacemos clic en "Calculá tu capacidad" para acceder a la calculadora
  cy.contains('Calculá tu capacidad')
    .click()

  // Paso 3: Verificamos que el campo "Capacidad instalada (kW)" sea visible
  cy.contains('Capacidad instalada (kW)')
    .should('be.visible')

  // Paso 4: Verificamos que el campo "Capacidad promedio utilizada (kW)" sea visible
  cy.contains('Capacidad promedio utilizada (kW)')
    .should('be.visible')

  // Paso 5: Verificamos que el campo "Tarifa eléctrica local (US$/kWh)" sea visible
  cy.contains('Tarifa eléctrica local (US$/kWh)')
    .should('be.visible')

  // Paso 6: Verificamos que el campo "Cantidad de racks" sea visible
  cy.contains('Cantidad de racks')
    .should('be.visible')

  // Paso 7: Verificamos que el botón "Ver mi resultado" sea visible
  cy.contains('Ver mi resultado')
    .should('be.visible')

})

// TC006 - Verificar que el botón "Ver mi resultado" esté deshabilitado con los campos vacíos
it('TC006 - Verificar que el botón "Ver mi resultado" esté deshabilitado con los campos vacíos', () => {

  // Paso 1: Ingresamos a la página principal de CAPACIA
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Hacemos clic en "Calculá tu capacidad" para acceder a la calculadora
  cy.contains('Calculá tu capacidad')
    .click()

  // Paso 3: Verificamos que el botón "Ver mi resultado" esté deshabilitado
  cy.contains('Ver mi resultado')
    .should('be.disabled')

})


// TC007 - Verificar cálculo con datos válidos
it('TC007 - Verificar cálculo con datos válidos', () => {

  // Paso 1: Ingresamos a la página principal de CAPACIA
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Hacemos clic en "Calculá tu capacidad"
  cy.contains('Calculá tu capacidad')
    .click()

  // Paso 3: Completamos la capacidad instalada
  cy.get('#installedCapacity')
    .type('2000')

  // Paso 4: Completamos la capacidad promedio utilizada
  cy.get('#usedCapacity')
    .type('760')

  // Paso 5: Completamos la tarifa eléctrica
  cy.get('#electricityRate')
    .type('0.12')

  // Paso 6: Completamos la cantidad de racks
  cy.get('#racks')
    .type('48')

  // Paso 7: Verificamos que el botón esté habilitado
  cy.contains('Ver mi resultado')
    .should('not.be.disabled')

  // Paso 8: Hacemos clic en "Ver mi resultado"
  cy.contains('Ver mi resultado')
    .click()

  // Paso 9: Esperamos a que se cargue el resultado parcial
  cy.url({ timeout: 10000 })
  .should('include', '/calculadora/resultado-parcial')

  // Paso 10: Verificamos la capacidad subutilizada
  cy.contains('1,240 kW')
    .should('be.visible')

  // Paso 11: Verificamos el porcentaje de subutilización
  cy.contains('62%')
    .should('be.visible')

  // Evidencia
  cy.screenshot('TC007-calculo-datos-validos')

})

// TC008 - Verificar validación de valores negativos
it('TC008 - Verificar validación de valores negativos', () => {

  // Paso 1: Ingresamos a la página principal de CAPACIA
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Hacemos clic en "Calculá tu capacidad"
  cy.contains('Calculá tu capacidad')
    .click()

  // Paso 3: Ingresamos un valor negativo en capacidad instalada
  cy.get('#installedCapacity')
    .type('-100')

  // Paso 4: Verificamos que el campo acepte visualmente el valor ingresado
  cy.get('#installedCapacity')
    .should('have.value', '-100')

  // Paso 5: Completamos los demás campos con valores válidos
  cy.get('#usedCapacity')
    .type('760')

  cy.get('#electricityRate')
    .type('0.12')

  cy.get('#racks')
    .type('48')

  // Paso 6: Verificamos que el botón permanezca deshabilitado
  // porque la capacidad instalada no puede ser negativa
  cy.contains('Ver mi resultado')
    .should('be.disabled')

  // Evidencia: capturamos el estado de la pantalla
  cy.screenshot('TC008-validacion-valor-negativo')

})

// TC009 - Verificar validación de tipo de dato inválido
it('TC009 - Verificar validación de tipo de dato inválido', () => {

  // Paso 1: Ingresamos a la página principal de CAPACIA
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Hacemos clic en "Calculá tu capacidad"
  cy.contains('Calculá tu capacidad')
    .click()

  // Paso 3: Intentamos ingresar un dato no numérico en capacidad instalada
  cy.get('#installedCapacity')
    .type('abc')

  // Paso 4: Verificamos que el campo no contenga el texto ingresado
  cy.get('#installedCapacity')
    .should('have.value', '')

})

// TC010 - Verificar que el botón permanezca deshabilitado cuando falta un campo
it('TC010 - Verificar que el botón permanezca deshabilitado cuando falta un campo', () => {

  // Paso 1: Ingresamos a la página principal de CAPACIA
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Hacemos clic en "Calculá tu capacidad"
  cy.contains('Calculá tu capacidad')
    .click()

  // Paso 3: Completamos la capacidad instalada
  cy.get('#installedCapacity')
    .type('2000')

  // Paso 4: Completamos la capacidad promedio utilizada
  cy.get('#usedCapacity')
    .type('760')

  // Paso 5: Completamos la tarifa eléctrica
  cy.get('#electricityRate')
    .type('0.12')

  // Paso 6: Dejamos "Cantidad de racks" vacío
  // No ingresamos ningún valor en #racks

  // Paso 7: Verificamos que el botón permanezca deshabilitado
  cy.contains('Ver mi resultado')
    .should('be.disabled')

})

// TC011 - Verificar validación de capacidad instalada igual a 0
it('TC011 - Verificar validación de capacidad instalada igual a 0', () => {

  // Paso 1: Ingresamos a la página principal de CAPACIA
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Hacemos clic en "Calculá tu capacidad"
  cy.contains('Calculá tu capacidad')
    .click()

  // Paso 3: Ingresamos 0 en capacidad instalada
  cy.get('#installedCapacity')
    .type('0')

  // Paso 4: Completamos capacidad promedio utilizada con un valor válido
  cy.get('#usedCapacity')
    .type('1')

  // Paso 5: Completamos tarifa eléctrica con un valor válido
  cy.get('#electricityRate')
    .type('0.12')

  // Paso 6: Completamos cantidad de racks con un valor válido
  cy.get('#racks')
    .type('1')

  // Paso 7: Verificamos que el botón permanezca deshabilitado
  // porque la capacidad instalada igual a 0 no permite realizar el cálculo
  cy.contains('Ver mi resultado')
    .should('be.disabled')

})



// TC012 - Verificar aceptación de valor decimal válido en tarifa eléctrica
it('TC012 - Verificar aceptación de valor decimal válido en tarifa eléctrica', () => {

  // Paso 1: Ingresamos a la página principal de CAPACIA
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Hacemos clic en "Calculá tu capacidad"
  cy.contains('Calculá tu capacidad')
    .click()

  // Paso 3: Completamos la capacidad instalada
  cy.get('#installedCapacity')
    .type('2000')

  // Paso 4: Completamos la capacidad promedio utilizada
  cy.get('#usedCapacity')
    .type('760')

  // Paso 5: Ingresamos un valor decimal válido en la tarifa eléctrica
  cy.get('#electricityRate')
    .type('0.12')

  // Paso 6: Completamos la cantidad de racks
  cy.get('#racks')
    .type('48')

  // Paso 7: Verificamos que el valor decimal haya sido aceptado
  cy.get('#electricityRate')
    .should('have.value', '0.12')

  // Paso 8: Verificamos que el botón esté habilitado
  cy.contains('Ver mi resultado')
    .should('not.be.disabled')

})


// TC013 - Verificar validación de tarifa eléctrica negativa
it('TC013 - Verificar validación de tarifa eléctrica negativa', () => {

  // Paso 1: Ingresamos a la página principal de CAPACIA
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Hacemos clic en "Calculá tu capacidad"
  cy.contains('Calculá tu capacidad')
    .click()

  // Paso 3: Completamos la capacidad instalada con un valor válido
  cy.get('#installedCapacity')
    .type('2000')

  // Paso 4: Completamos la capacidad promedio utilizada con un valor válido
  cy.get('#usedCapacity')
    .type('760')

  // Paso 5: Ingresamos una tarifa eléctrica negativa
  cy.get('#electricityRate')
    .type('-0.12')

  // Paso 6: Completamos la cantidad de racks con un valor válido
  cy.get('#racks')
    .type('48')

  // Paso 7: Verificamos que la tarifa negativa haya sido ingresada
  cy.get('#electricityRate')
    .should('have.value', '-0.12')

  // Paso 8: Verificamos que el botón permanezca deshabilitado
  cy.contains('Ver mi resultado')
    .should('be.disabled')

})

// TC014 - Verificar validación cuando la capacidad utilizada supera la capacidad instalada
it('TC014 - Verificar validación cuando la capacidad utilizada supera la capacidad instalada', () => {

  // Paso 1: Ingresamos a la página principal de CAPACIA
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Hacemos clic en "Calculá tu capacidad"
  cy.contains('Calculá tu capacidad')
    .click()

  // Paso 3: Ingresamos una capacidad instalada de 100 kW
  cy.get('#installedCapacity')
    .type('100')

  // Paso 4: Ingresamos una capacidad utilizada mayor: 200 kW
  cy.get('#usedCapacity')
    .type('200')

  // Paso 5: Ingresamos una tarifa eléctrica válida
  cy.get('#electricityRate')
    .type('0.12')

  // Paso 6: Ingresamos una cantidad de racks válida
  cy.get('#racks')
    .type('1')

  // Evidencia: verificamos los valores ingresados
  cy.get('#installedCapacity')
    .should('have.value', '100')

  cy.get('#usedCapacity')
    .should('have.value', '200')

  cy.get('#electricityRate')
    .should('have.value', '0.12')

  cy.get('#racks')
    .should('have.value', '1')

  // Evidencia: capturamos el estado de la calculadora antes de la validación
  cy.screenshot('TC014-capacidad-utilizada-supera-capacidad-instalada')

  // Paso 7: Verificamos que el botón permanezca deshabilitado
  // El backend rechaza esta combinación con código 400
  // Esta assertion falla porque actualmente el frontend habilita el botón
  cy.contains('Ver mi resultado')
    .should('be.disabled')

})

// TC015 - Verificar validación de email obligatorio para desbloquear el resultado completo
it('TC015 - Verificar validación de email obligatorio para desbloquear el resultado completo', () => {

  // Paso 1: Ingresamos a la página principal de CAPACIA
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Hacemos clic en "Calculá tu capacidad"
  cy.contains('Calculá tu capacidad')
    .click()

  // Paso 3: Completamos la capacidad instalada
  cy.get('#installedCapacity')
    .type('2000')

  // Paso 4: Completamos la capacidad promedio utilizada
  cy.get('#usedCapacity')
    .type('760')

  // Paso 5: Completamos la tarifa eléctrica
  cy.get('#electricityRate')
    .type('0.12')

  // Paso 6: Completamos la cantidad de racks
  cy.get('#racks')
    .type('48')

  // Paso 7: Hacemos clic en "Ver mi resultado"
  cy.contains('Ver mi resultado')
    .click()

  // Paso 8: Verificamos que se haya cargado el resultado parcial
  cy.url({ timeout: 10000 })
  .should('include', '/calculadora/resultado-parcial')

  // Paso 9: Verificamos que el campo de email esté visible
  cy.get('#result-email')
    .should('be.visible')

  // Paso 10: Verificamos que el campo de email esté vacío
  cy.get('#result-email')
    .should('have.value', '')

  // Paso 11: Intentamos desbloquear el resultado completo sin ingresar email
  cy.contains('Desbloqueá el resultado completo')
    .click()

  // Paso 12: Verificamos que el campo de email sea obligatorio
  cy.get('#result-email')
    .should('have.attr', 'required')

  // Paso 13: Verificamos que no se haya navegado al resultado completo
  cy.url()
    .should('not.include', '/calculadora/resultado-completo')

  // Evidencia: capturamos el estado de la pantalla
  cy.screenshot('TC015-email-obligatorio-resultado-completo')

})

// TC016 - Verificar validación de formato de email
it('TC016 - Verificar validación de formato de email', () => {

  // Paso 1: Ingresamos a la página principal
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Accedemos a la calculadora
  cy.contains('Calculá tu capacidad')
    .click()

  // Paso 3: Completamos la capacidad instalada
  cy.get('#installedCapacity')
    .type('2000')

  // Paso 4: Completamos la capacidad promedio utilizada
  cy.get('#usedCapacity')
    .type('760')

  // Paso 5: Completamos la tarifa eléctrica
  cy.get('#electricityRate')
    .type('0.12')

  // Paso 6: Completamos la cantidad de racks
  cy.get('#racks')
    .type('48')

  // Paso 7: Generamos el resultado
  cy.contains('Ver mi resultado')
    .click()

  // Paso 8: Esperamos a que se cargue el resultado parcial
  cy.url({ timeout: 10000 })
  .should('include', '/calculadora/resultado-parcial')

  // Paso 9: Ingresamos un formato de email inválido
  cy.get('#result-email')
    .type('a@u')

  // Paso 10: Verificamos que el valor haya sido ingresado
  cy.get('#result-email')
    .should('have.value', 'a@u')

  // Paso 11: Intentamos desbloquear el resultado completo
  cy.contains('Desbloqueá el resultado completo')
    .click()

  // Paso 12: Verificamos que NO se navegue al resultado completo
  cy.url()
    .should('not.include', '/calculadora/resultado-completo')

  // Evidencia
  cy.screenshot('TC016-email-formato-invalido')

})

// TC017 - Verificar desbloqueo del resultado completo con email válido
it('TC017 - Verificar desbloqueo del resultado completo con email válido', () => {

  // Paso 1: Ingresamos a la página principal de CAPACIA
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Accedemos a la calculadora
  cy.contains('Calculá tu capacidad')
    .click()

  // Paso 3: Completamos la capacidad instalada
  cy.get('#installedCapacity')
    .type('2000')

  // Paso 4: Completamos la capacidad promedio utilizada
  cy.get('#usedCapacity')
    .type('760')

  // Paso 5: Completamos la tarifa eléctrica
  cy.get('#electricityRate')
    .type('0.12')

  // Paso 6: Completamos la cantidad de racks
  cy.get('#racks')
    .type('48')

  // Paso 7: Generamos el resultado
  cy.contains('Ver mi resultado')
    .click()

  // Paso 8: Esperamos el resultado parcial
  cy.url({ timeout: 10000 })
  .should('include', '/calculadora/resultado-parcial')

  // Paso 9: Ingresamos un email válido
  cy.get('#result-email')
    .clear()
    .type('test@example.com')

  // Paso 10: Verificamos que el email haya sido ingresado correctamente
  cy.get('#result-email')
    .should('have.value', 'test@example.com')

  // Paso 11: Hacemos clic en "Desbloquear resultado completo"
  cy.get('button[type="submit"]')
    .contains('Desbloquear resultado completo')
    .should('be.visible')
    .click()

  // Paso 12: Verificamos que se acceda al resultado completo
  cy.url({ timeout: 10000 })
    .should('include', '/calculadora/resultado-completo')

  // Evidencia
  cy.screenshot('TC017-resultado-completo-email-valido')

})

// TC018 - Verificar acceso al Maturity Benchmark
it('TC018 - Verificar acceso al Maturity Benchmark', () => {

  // Paso 1: Ingresamos al resultado completo
  cy.visit('https://capacia.vercel.app/calculadora/resultado-completo')

  // Paso 2: Verificamos que el botón sea visible
  cy.contains('Ir al Maturity Benchmark')
    .should('be.visible')

  // Paso 3: Hacemos clic en el botón
  cy.contains('Ir al Maturity Benchmark')
    .click()

  // Paso 4: Verificamos que se redirija al login
  cy.url()
    .should('include', '/login')

  // Paso 5: Verificamos que el destino original sea el Maturity Benchmark
  cy.url()
    .should('include', 'next=%2Fbenchmark')

  // Evidencia
  cy.screenshot('TC018-acceso-maturity-benchmark')

})

// TC019 - Verificar acceso al reporte
it('TC019 - Verificar acceso al reporte', () => {

  // Paso 1: Ingresamos a Home
  cy.visit('https://capacia.vercel.app/')

  // Paso 2: Verificamos que el enlace sea visible
  cy.contains('Visualiza el Report')
    .should('be.visible')

  // Paso 3: Accedemos al reporte
  cy.contains('Visualiza el Report')
    .click()

  // Paso 4: Verificamos la URL
  cy.url()
    .should('include', '/reporte')

  // Evidencia
  cy.screenshot('TC019-acceso-reporte')

})

// TC020 - Verificar agendamiento de reunión
it('TC020 - Verificar agendamiento de reunión', () => {

  // Paso 1: Ingresamos a la página de agendar reunión
  cy.visit('https://capacia.vercel.app/benchmark/reunion')

  // Paso 2: Seleccionamos la fecha
  cy.contains('div', '16')
    .click()

  // Paso 3: Verificamos que aparezcan los horarios disponibles
  cy.contains('Horarios disponibles')
    .should('be.visible')

  // Paso 4: Seleccionamos un horario
  cy.contains('09:00')
    .click()

  // Paso 5: Verificamos que aparezca el formulario
  cy.contains('Confirmá tus datos')
    .should('be.visible')

  // Paso 6: Completamos el nombre
  cy.get('input')
    .first()
    .type('Pamela Test')

  // Paso 7: Completamos el email
  cy.get('input[type="email"]')
    .type('test@example.com')

  // Paso 8: Completamos el mensaje
  cy.get('textarea')
    .type('Mensaje de prueba')

  // Paso 9: Verificamos que el botón esté habilitado
  cy.contains('Confirmar reunión')
    .should('not.be.disabled')

  // Paso 10: Confirmamos la reunión
  cy.contains('Confirmar reunión')
    .click()

  // Paso 11: Verificamos la página de confirmación
  cy.url()
    .should('include', '/benchmark/reunion/confirmacion')

  // Evidencia
  cy.screenshot('TC020-agendamiento-reunion')

})