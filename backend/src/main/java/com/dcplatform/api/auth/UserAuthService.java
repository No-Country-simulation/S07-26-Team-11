package com.dcplatform.api.auth;

import com.dcplatform.api.security.jwt.JwtService;
import com.dcplatform.api.shared.ApiException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registro y login basicos (email + password). Emite los mismos JWT de acceso
 * que despues consume JwtAuthenticationFilter, para que el resto de la API (y el
 * futuro flujo de magic link) no tengan que distinguir de donde vino el token.
 */
@Service
public class UserAuthService {

    private static final String ROLE = "USER";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw ApiException.conflict("Ya existe una cuenta con ese email");
        }
        User user = new User(request.getEmail(), passwordEncoder.encode(request.getPassword()));
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AccessTokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .filter(User::isActive)
                .orElseThrow(() -> ApiException.unauthorized("Email o contraseña invalidos"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw ApiException.unauthorized("Email o contraseña invalidos");
        }

        return AccessTokenResponse.of(jwtService.generateAccessToken(user.getEmail(), ROLE));
    }
}
