package com.dcplatform.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada. El mismo artefacto se despliega dos veces:
 *  - perfil por defecto: expone la API publica
 *  - perfil "worker": consume la cola de trabajos (PDF y correos)
 *
 * Se excluye UserDetailsServiceAutoConfiguration: la autenticacion es 100% JWT
 * propio (JwtAuthenticationFilter + UserAuthService), no se usa el
 * UserDetailsService/AuthenticationManager por defecto de Spring Security.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@ConfigurationPropertiesScan
@EnableScheduling
@EnableAsync
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
