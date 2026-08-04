package com.dcplatform.api.leads;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dcplatform.api.leads.DTO.MagicLinkDto;
import com.dcplatform.api.leads.DTO.MagicLinkRequest;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/public")
public class MagicLinkController {

    private final MagicLinkService magicLinkService;

    @PostMapping("/leads")
    public ResponseEntity<MagicLinkDto> generateMagicLink(@RequestBody MagicLinkRequest magicLinkRequest) {

        var response = magicLinkService.generateMagicLink(magicLinkRequest);

        return ResponseEntity.status(202).body(response);
    }
}
