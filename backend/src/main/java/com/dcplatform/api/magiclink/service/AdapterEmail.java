package com.dcplatform.api.magiclink.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import sendinblue.ApiException;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

@Component
public class AdapterEmail implements INotification {

  private final TransactionalEmailsApi apiInstance;
  

public AdapterEmail(TransactionalEmailsApi apiInstance) {
        this.apiInstance = apiInstance;
    }


  @Value("${app.email.from-address}")
  private String senderEmail;

  @Value("${app.email.from-name:Capacia}")
  private String senderName;



  @Override
  public void sendNotification(String email, String mensaje) {
    try {
      SendSmtpEmailSender sender = new SendSmtpEmailSender()
          .email(senderEmail)
          .name(senderName);

      SendSmtpEmailTo recipient = new SendSmtpEmailTo()
          .email(email);


    SendSmtpEmail sendSmtpEmail = new SendSmtpEmail()
                    .sender(sender)
                    .to(List.of(recipient))
                    .subject("Tu enlace de acceso - Capacia") 
                    .htmlContent("<p>Hola, tu magick link: <strong>" + mensaje + "</strong></p>");


      CreateSmtpEmail response = apiInstance.sendTransacEmail(sendSmtpEmail);
      System.out.println("Email enviado con éxito. MessageId: " + response.getMessageId());

    } catch (ApiException e) {
      System.err.println("Error al enviar email mediante Brevo: " + e.getResponseBody());
      throw new RuntimeException("Error al procesar el envío de correo", e);
    }

  }

}
