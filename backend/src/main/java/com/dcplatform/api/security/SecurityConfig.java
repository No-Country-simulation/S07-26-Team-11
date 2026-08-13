package com.dcplatform.api.security;

import com.dcplatform.api.security.jwt.JwtAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthFilter;
	private final SecurityExceptionDelegator exceptionDelegator;

	@Value("${app.cors.allowed-origins}")
	private String allowedOrigins;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, SecurityExceptionDelegator exceptionDelegator) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.exceptionDelegator = exceptionDelegator;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(exceptionDelegator) // 401 UNAUTHORIZED
						.accessDeniedHandler(exceptionDelegator) // 403 FORBIDDEN
				)
				.authorizeHttpRequests(auth -> auth
						// rutas de operación y documentación (públicas)
						.requestMatchers("/actuator/health", "/actuator/info").permitAll()
						.requestMatchers("/api/v1/public/ping", "/api/v1/public/db-status").permitAll()
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

						// endpoints públicos explícitos
						// unico punto de entrada al sistema de auth: sin esto, nadie puede solicitar
						// acceso
						.requestMatchers(HttpMethod.POST, "/api/v1/auth/request-access").permitAll()
						// registro/login basico de email+password (ver modulo auth). logout y /me
						// requieren token, quedan cubiertos por el anyRequest().authenticated() de
						// abajo
						.requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/public/calculator/estimate").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/public/calculator/defaults").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/public/leads").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/public/leads/verify").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/public/benchmark/instrument").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/public/industry/stats").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/public/webhooks/email/*").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/public/unsubscribe").permitAll()

						// endpoint híbrido, requiere validar contexto de autenticación en servicio o
						// controlador
						.requestMatchers(HttpMethod.GET, "/api/v1/public/calculator/estimates/*").permitAll()

						// ---------------- ADMIN ----------------
						.requestMatchers(HttpMethod.POST, "/api/v1/admin/auth/login").permitAll() // público
						.requestMatchers("/api/v1/admin/outreach/**").hasRole("ADMIN")
						.requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN", "VIEWER")

						// el resto debe ser autenticado
						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		List<String> origins = Arrays.stream(allowedOrigins.split(","))
				.map(String::trim)
				.toList();
		config.setAllowedOrigins(origins);

		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "X-Lead-Token"));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L); // Cachea la respuesta del preflight por 1 hora

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}
