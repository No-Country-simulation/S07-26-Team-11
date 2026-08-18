package com.dcplatform.api.auth;

import com.dcplatform.api.shared.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Cuentas de email + password: registro, login y logout.
 *
 * <p><strong>El alta de cuentas nuevas ya no pasa por aca.</strong> Los usuarios del producto
 * entran por magic link ({@code POST /api/v1/public/leads}), que crea la cuenta sola en el
 * primer canje. El registro por password quedo reservado a ADMIN y existe solo para las cuentas
 * de manejo interno de la aplicacion.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class UserAuthController {

    private final UserAuthService userAuthService;
    private final TokenRevocationService tokenRevocationService;

    public UserAuthController(UserAuthService userAuthService, TokenRevocationService tokenRevocationService) {
        this.userAuthService = userAuthService;
        this.tokenRevocationService = tokenRevocationService;
    }

    /**
     * Crea una cuenta de manejo interno. <strong>Requiere rol ADMIN</strong>
     * (ver SecurityConfig): no es el alta de usuarios del producto, esa es el magic link.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userAuthService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userAuthService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        tokenRevocationService.revoke(extractBearerToken(request));
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint protegido de prueba: confirma que un JWT valido (de este login o,
     * mas adelante, de magic link) efectivamente autentica la request.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return ResponseEntity.ok(Map.of("email", auth.getName(), "roles", roles));
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw ApiException.unauthorized("Falta el token de acceso");
        }
        return header.substring(7);
    }
}
