package com.dcplatform.api.security;

import com.dcplatform.api.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) {
		http
				.cors(Customizer.withDefaults()) // deja pasar las peticiones OPTIONS preflight (ver CorsConfig.java)
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
						// único punto de entrada al sistema de auth: sin esto, nadie puede solicitar acceso
						.requestMatchers(HttpMethod.POST, "/api/v1/auth/request-access").permitAll()
						// registro/login básico de email+password (ver módulo auth) logout y /me
						// requieren token, quedan cubiertos por el anyRequest().authenticated() de abajo
						.requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login").permitAll()

						// ---------------- CALCULADORA ----------------
						.requestMatchers(HttpMethod.POST, "/api/v1/public/calculator/estimate").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/public/calculator/defaults").permitAll()

						// ---------------- LEAD y MAGIC LINK ----------------
						.requestMatchers(HttpMethod.POST, "/api/v1/public/leads").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/public/leads/verify").permitAll()

						// ---------------- BENCHMARK ----------------
						.requestMatchers(HttpMethod.GET, "/api/v1/public/benchmark/instrument").permitAll()

						.requestMatchers(HttpMethod.GET, "/api/v1/public/industry/stats").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/public/webhooks/email/*").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/public/unsubscribe").permitAll()

						// ---------------- ADMIN ----------------
						.requestMatchers(HttpMethod.POST, "/api/v1/admin/auth/login").permitAll() // público
						.requestMatchers("/api/v1/admin/outreach/**").hasRole("ADMIN")
						.requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN", "VIEWER")

						// el resto debe ser autenticado
						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
