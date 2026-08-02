package com.dcplatform.api.security;

import com.dcplatform.api.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthFilter;
	private final SecurityExceptionDelegator exceptionDelegator;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, SecurityExceptionDelegator exceptionDelegator) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.exceptionDelegator = exceptionDelegator;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) {
		http.csrf(AbstractHttpConfigurer::disable)
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
						.requestMatchers(HttpMethod.POST, "/api/v1/public/calculator/estimate").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/public/calculator/defaults").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/public/leads").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/public/leads/verify").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/public/benchmark/instrument").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/public/industry/stats").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/public/webhooks/email/*").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/public/unsubscribe").permitAll()

						// endpoint híbrido, requiere validar contexto de autenticación en servicio o controlador
						.requestMatchers(HttpMethod.GET, "/api/v1/public/calculator/estimates/*").permitAll()

						// ---------------- ADMIN ----------------
						.requestMatchers(HttpMethod.POST, "/api/v1/admin/auth/login").permitAll() // público
						.requestMatchers("/api/v1/admin/outreach/**").hasRole("ADMIN")
						.requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN", "VIEWER")

						// el resto debe ser autenticado
						.anyRequest().authenticated()
				)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
