package com.dcplatform.api.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/*
* Delega los estados 401 y 403 al controlador global de excepciones (GlobalExceptionHandler)
* */
@Component
public class SecurityExceptionDelegator implements AuthenticationEntryPoint, AccessDeniedHandler {

	private final HandlerExceptionResolver resolver;

	public SecurityExceptionDelegator(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public void commence(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
	                     @NonNull AuthenticationException authException) {
		resolver.resolveException(request, response, null, authException);
	}

	@Override
	public void handle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
	                   @NonNull AccessDeniedException accessDeniedException) {
		resolver.resolveException(request, response, null, accessDeniedException);
	}
}
