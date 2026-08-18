package com.dcplatform.api.magickLink;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import com.dcplatform.api.magiclink.repository.MagicLinkAccessTokenRepository;
import com.dcplatform.api.magiclink.service.MagicLinkNotifier;
import com.dcplatform.api.magiclink.service.MagicLinkServiceImpl;
import com.dcplatform.api.magiclink.service.RateLimiterService;
import com.dcplatform.api.magiclink.utils.TokenHasher;
import com.dcplatform.api.security.jwt.JwtService;

@ExtendWith(MockitoExtension.class)
public class MagickLinkTest {
    @Mock
    private RateLimiterService rateLimiterService;
    @Mock
    private MagicLinkNotifier magicLinkNotifier;
    @Mock
    private JwtService jwtService;
    @Mock
    private TokenHasher tokenHasher;
    @Mock
    private MagicLinkAccessTokenRepository accessTokenRepository;

    @InjectMocks
    private MagicLinkServiceImpl magicLinkService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(magicLinkService, "frontendUrl", "http://localhost:3000");

    }

    @Test
    void testMagicLinkGeneration() {

        String email = "test@empresa.com";
        String clientIp = "192.168.1.1";

        lenient().when(rateLimiterService.tryConsumeIp(any())).thenReturn(true);
        lenient().when(rateLimiterService.tryConsumeEmail(any())).thenReturn(true);
        when(jwtService.generateMagicLinkToken(email)).thenReturn("raw-jwt-tokenFromMagicLink");
        when(tokenHasher.hash("raw-jwt-tokenFromMagicLink")).thenReturn("hashed-tokenFromMagicLink");

        String response = magicLinkService.generateMagicLinkAndInform(email, UUID.randomUUID(), clientIp);

        assertNotNull(response);
        assertEquals("Si el correo es válido, recibirás un enlace de acceso en unos segundos.", response);

        verify(magicLinkNotifier, times(1)).sendNotificacion(eq(email), anyString());
        verify(accessTokenRepository, times(1)).save(any());
    }


}
