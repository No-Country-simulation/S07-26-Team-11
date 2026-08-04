package com.dcplatform.api.leads;



import org.springframework.stereotype.Service;

import com.dcplatform.api.leads.DTO.MagicLinkDto;
import com.dcplatform.api.leads.DTO.MagicLinkRequest;

import lombok.AllArgsConstructor;


@AllArgsConstructor
@Service
public class MagicLinkService {

    private final MagicLinkNotifer magicLinkNotifier;

    public MagicLinkDto generateMagicLink(MagicLinkRequest magicLinkRequest) {

       //magicLinkNotifier.sendNotification(lead, token);

        return new MagicLinkDto("Si el correo es válido, recibirás un enlace de acceso en unos segundos.");
    }

}
