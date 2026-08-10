package com.dcplatform.api.leads.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TokenRequest(
        
        @NotBlank(message = "El token es obligatorio") 
        String token
      
    ) {

}
