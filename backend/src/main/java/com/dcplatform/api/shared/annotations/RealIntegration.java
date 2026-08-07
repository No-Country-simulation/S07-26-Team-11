package com.dcplatform.api.shared.annotations;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * Anotación de conveniencia para registrar implementaciones reales de servicios o integraciones externas en el contexto de la aplicación.
 * <p>
 * Trabaja en conjunto con {@code @MockIntegration} para completar el patrón de "Feature Flag".
 * Esta anotación garantiza que la clase decorada solo se instancie y se inyecte cuando
 * el entorno esté configurado para consumir las APIs o servicios reales de terceros
 * (es decir, cuando el modo de simulación esté apagado).
 * </p>
 *
 * <h3>Comportamiento interno:</h3>
 * <ul>
 *     <li><b>Definición de Bean:</b> Está meta-anotada con {@link Service @Service}, lo que permite que el framework
 *     detecte e inyecte la clase automáticamente como un componente de servicio.</li>
 *     <li><b>Activación condicional:</b> Utiliza {@link ConditionalOnProperty @ConditionalOnProperty}
 *     evaluando la propiedad de configuración {@code client.api.mock.enabled}.</li>
 *     <li><b>Condición estricta:</b> A diferencia de su contraparte, el Bean solo se inicializará si la propiedad
 *     mencionada tiene explícitamente el valor {@code false}. Si la propiedad no existe en la configuración
 *     o es {@code true}, esta implementación real será ignorada por completo.</li>
 *     <li><b>Ciclo de vida:</b> La anotación aplica a nivel de clase ({@code ElementType.TYPE}) y su disponibilidad
 *     se mantiene en tiempo de ejecución ({@code RetentionPolicy.RUNTIME}).</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * &#64;RealIntegration
 * public class ExternalApiCalculatorService implements CalculatorService {
 *     // Implementación definitiva que realiza llamadas HTTP al servicio real del cliente
 * }
 * </pre>
 *
 * @see ConditionalOnProperty
 * @see Service
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
@ConditionalOnProperty(name = "client.api.mock.enabled", havingValue = "false")
public @interface RealIntegration {
}
