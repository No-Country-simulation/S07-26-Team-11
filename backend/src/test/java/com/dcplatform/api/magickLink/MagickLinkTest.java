package com.dcplatform.api.magickLink;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.dcplatform.api.shared.ApiException;

import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import com.dcplatform.api.leads.Lead;
import com.dcplatform.api.leads.LeadRepository;
import com.dcplatform.api.leads.LeandAccessTokenRepository;
import com.dcplatform.api.leads.MagicLinkNotifer;
import com.dcplatform.api.leads.MagicLinkService;
import com.dcplatform.api.leads.RateLimiterService;
import com.dcplatform.api.leads.Source;
import com.dcplatform.api.leads.TokenHasher;
import com.dcplatform.api.leads.DTO.MagicLinkRequest;
import com.dcplatform.api.leads.DTO.MagicLinkResponse;
import com.dcplatform.api.security.jwt.JwtService;
import com.dcplatform.api.shared.ApiException;

@ExtendWith(MockitoExtension.class)
public class MagickLinkTest {
    @Mock
    private RateLimiterService rateLimiterService;
    @Mock
    private MagicLinkNotifer magicLinkNotifier;
    @Mock
    private JwtService jwtService;
    @Mock
    private TokenHasher tokenHasher;
    @Mock
    private LeandAccessTokenRepository accessTokenRepository;
    @Mock
    private LeadRepository leadRepository;

    @InjectMocks
    private MagicLinkService magicLinkService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(magicLinkService, "frontendUrl", "http://localhost:3000");

    }

    @Test
    void testMagicLinkGeneration() {

        String email = "test@empresa.com";
        String clientIp = "192.168.1.1";
        MagicLinkRequest request = new MagicLinkRequest(
                email, "Empresa", "Dev", Source.CALCULATOR, null, "true", "v1.0");

        lenient().when(rateLimiterService.tryConsumeIp(any())).thenReturn(true);
        lenient().when(rateLimiterService.tryConsumeEmail(any())).thenReturn(true);
        when(leadRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(leadRepository.save(any(Lead.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateMagicLinkToken(email)).thenReturn("raw-jwt-token");
        when(tokenHasher.hash("raw-jwt-token")).thenReturn("hashed-token");

        MagicLinkResponse response = magicLinkService.generateMagicLink(request, clientIp);

        assertNotNull(response);
        assertEquals("Si el correo es válido, recibirás un enlace de acceso en unos segundos.", response.message());

        verify(magicLinkNotifier, times(1)).sendNotificacion(eq(email), anyString());
        verify(accessTokenRepository, times(1)).save(any());
    }


}
