package com.dcplatform.api.leads;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dcplatform.api.leads.DTO.MagicLinkRequest;
import com.dcplatform.api.leads.DTO.MagicLinkResponse;
import com.dcplatform.api.leads.DTO.TokenRequest;
import com.dcplatform.api.leads.DTO.TokenResponse;

@RestController
@RequestMapping("/api/v1/public")
public class MagicLinkController {

    private final MagicLinkService magicLinkService;

	public MagicLinkController(MagicLinkService magicLinkService) {
		this.magicLinkService = magicLinkService;
	}

	@PostMapping("/leads")
    public ResponseEntity<MagicLinkResponse> generateMagicLink(@RequestBody MagicLinkRequest magicLinkRequest) {

        var response = magicLinkService.generateMagicLink(magicLinkRequest);

        return ResponseEntity.status(202).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<TokenResponse> generateToken(@RequestBody TokenRequest tokenRequest) {

        TokenResponse response = magicLinkService.verifyAndExchange(tokenRequest.token());
        return ResponseEntity.ok(response);
    }
}
