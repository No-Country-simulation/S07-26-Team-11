package com.dcplatform.api.shared.annotations;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * Anotación de conveniencia para registrar servicios simulados (mocks) en el contexto de la aplicación.
 * <p>
 * Esta anotación encapsula la configuración necesaria para implementar un patrón de "Feature Flag"
 * que facilita el desarrollo de la arquitectura monolítica mientras se espera la liberación de
 * servicios de terceros o APIs externas. Permite alternar limpiamente entre implementaciones
 * ficticias y reales sin modificar el código de los controladores ni de las interfaces.
 * </p>
 *
 * <h3>Comportamiento interno:</h3>
 * <ul>
 *     <li><b>Definición de Bean:</b> Está meta-anotada con {@link Service @Service},
 *     lo que permite que el framework detecte e inyecte la clase automáticamente como un componente de servicio.</li>
 *     <li><b>Resolución de dependencias:</b> Incluye {@link Primary @Primary} para otorgarle preferencia de inyección.
 *     Esto evita excepciones de ambigüedad en tiempo de ejecución y suprime falsos positivos en el analizador estático
 *     del IDE cuando coexisten múltiples implementaciones de una misma interfaz.</li>
 *     <li><b>Activación condicional:</b> Utiliza {@link ConditionalOnProperty @ConditionalOnProperty} vinculada
 *     a la propiedad {@code client.api.mock.enabled}.
 *     El Bean solo se inicializará si esta propiedad tiene el valor {@code true}.</li>
 *     <li><b>Seguridad por defecto:</b> Al definir {@code matchIfMissing = true}, asegura que si la propiedad de
 *     configuración se omite accidentalmente en los archivos YAML o properties, el sistema utilizará los datos
 *     mockeados por defecto para prevenir llamadas fallidas a entornos reales.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * &#64;MockIntegration
 * public class MockCalculatorService implements CalculatorService {
 *     // Implementación que retorna datos estáticos o calculados localmente
 * }
 * </pre>
 *
 * @see ConditionalOnProperty
 * @see Primary
 * @see Service
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
@Primary
@ConditionalOnProperty(name = "client.api.mock.enabled", havingValue = "true", matchIfMissing = true)
public @interface MockIntegration {
}
