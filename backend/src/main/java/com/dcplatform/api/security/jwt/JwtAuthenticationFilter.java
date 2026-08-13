package com.dcplatform.api.security.jwt;

import com.dcplatform.api.auth.TokenRevocationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final TokenRevocationService tokenRevocationService;

	public JwtAuthenticationFilter(JwtService jwtService, TokenRevocationService tokenRevocationService) {
		this.jwtService = jwtService;
		this.tokenRevocationService = tokenRevocationService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		String jwt = null;
		final String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			jwt = authHeader.substring(7);
		}

		if (jwt == null) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			if (jwtService.isTokenValid(jwt) && jwtService.isAccessToken(jwt)
					&& !tokenRevocationService.isRevoked(jwt)) {
				String email = jwtService.extractEmail(jwt);
				String role = jwtService.extractRole(jwt);

				if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					List<SimpleGrantedAuthority> authorities = role != null
							? List.of(new SimpleGrantedAuthority("ROLE_" + role))
							: Collections.emptyList();

					var authToken = new UsernamePasswordAuthenticationToken(
							email, null, authorities);
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		} catch (Exception e) {
			SecurityContextHolder.clearContext();
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();
		// 🟢 Si es una ruta pública o una petición Preflight OPTIONS, saltear la
		// validación JWT
		return path.startsWith("/api/v1/public/")
				|| "OPTIONS".equalsIgnoreCase(request.getMethod());
	}
}