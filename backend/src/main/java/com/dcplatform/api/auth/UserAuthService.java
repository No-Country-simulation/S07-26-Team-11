package com.dcplatform.api.auth;

import com.dcplatform.api.security.jwt.JwtService;
import com.dcplatform.api.shared.ApiException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Registro y login basicos (email + password). Emite los mismos JWT de acceso
 * que despues consume JwtAuthenticationFilter, para que el resto de la API (y el
 * futuro flujo de magic link) no tengan que distinguir de donde vino el token.
 */
@Service
public class UserAuthService {

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

        // El rol sale de la fila, no de una constante: un ADMIN se promueve en la base
        // (ver database/seeds/) y su token queda con ROLE_ADMIN sin tocar codigo.
        return AccessTokenResponse.of(jwtService.generateAccessToken(user.getEmail(), user.getRole()));
    }

    /**
     * Emite el token de acceso de un email ya verificado por otro medio (hoy, el canje de un
     * magic link) y garantiza que exista su fila en {@code users}.
     *
     * <p>Sin esa fila el token autentica pero no sirve: los modulos que resuelven al dueno
     * contra {@code users} —{@code /api/v1/documents} entre ellos— responden 401 porque la
     * identidad del token no existe para ellos. La cuenta se crea en el primer canje y se
     * reutiliza en los siguientes, de modo que el lead conserva sus documentos entre sesiones.
     *
     * <p>La cuenta queda <em>sin password utilizable</em>: se guarda el hash de un secreto
     * aleatorio que nadie conoce, asi que {@code POST /auth/login} nunca la deja entrar y el
     * unico camino de acceso sigue siendo el magic link. El rol es el de la fila (por defecto
     * {@code USER}), nunca uno fijo: un lead promovido a ADMIN en la base obtiene ROLE_ADMIN
     * sin tocar codigo, igual que en {@link #login}.
     *
     * @param email email ya verificado; quien llama es responsable de haberlo comprobado
     * @return el token de acceso firmado, listo para entregar al cliente
     */
    @Transactional
    public String issueAccessTokenForVerifiedEmail(String email) {
        User account = userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> userRepository.save(new User(email, unusablePasswordHash())));

        if (!account.isActive()) {
            throw ApiException.unauthorized("La cuenta asociada a ese email esta inactiva");
        }

        return jwtService.generateAccessToken(account.getEmail(), account.getRole());
    }

    /**
     * Hash de un secreto aleatorio que no se guarda en ningun lado: cumple el NOT NULL de
     * {@code password_hash} sin habilitar el login por password. No se usa un centinela fijo
     * a proposito, para que el fallo se vea igual que cualquier password incorrecto y no se
     * pueda distinguir por la respuesta que la cuenta nacio de un magic link.
     */
    private String unusablePasswordHash() {
        return passwordEncoder.encode(UUID.randomUUID().toString());
    }
}
