package com.dcplatform.api.leads;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dcplatform.api.leads.DTO.MagicLinkRequest;
import com.dcplatform.api.leads.DTO.MagicLinkResponse;
import com.dcplatform.api.leads.DTO.TokenRequest;
import com.dcplatform.api.leads.DTO.TokenResponse;
import com.dcplatform.api.leads.utils.GetClientIp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/public")
public class MagicLinkController {

    private final MagicLinkService magicLinkService;
    private final GetClientIp getClientIp = new GetClientIp();

    public MagicLinkController(MagicLinkService magicLinkService) {
        this.magicLinkService = magicLinkService;

    }

    @PostMapping("/leads")
    public ResponseEntity<MagicLinkResponse> generateMagicLink(@Valid @RequestBody MagicLinkRequest magicLinkRequest,
            HttpServletRequest request) {

        String clientIp = getClientIp.getClienwtIp(request);

        var response = magicLinkService.generateMagicLink(magicLinkRequest, clientIp);

        return ResponseEntity.status(202).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<TokenResponse> generateToken(
            @Valid @RequestBody TokenRequest tokenRequest) {

        TokenResponse response = magicLinkService.verifyAndExchange(tokenRequest);
        return ResponseEntity.ok(response);
    }
}
