package com.dcplatform.api.leads;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sendinblue.ApiException;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

@AllArgsConstructor
@Getter
@Setter
public class AdapterEmail implements INotification {

  private final TransactionalEmailsApi apiInstance;

  @Value("${app.email.provider}")
  private String senderEmail;

  @Value("${app.email.from-address}")
  private String senderName;

  @Override
  public void sendNotification(Lead lead, LeadAccessTokens leadAccessTokens) {
    try {
      SendSmtpEmailSender sender = new SendSmtpEmailSender()
          .email(senderEmail)
          .name(senderName);

      SendSmtpEmailTo recipient = new SendSmtpEmailTo()
          .email(lead.getEmail())
          .name(lead.getCompanyName());

      SendSmtpEmail sendSmtpEmail = new SendSmtpEmail()
          .sender(sender)
          .to(List.of(recipient))
          .htmlContent("<p>Hola " + lead.getCompanyName() + ", usá este token: " 
                                + leadAccessTokens.getToken_hash() + "</p>");

      
      CreateSmtpEmail response = apiInstance.sendTransacEmail(sendSmtpEmail);
      System.out.println("Email enviado con éxito. MessageId: " + response.getMessageId());

    } catch (ApiException e) {
      System.err.println("Error al enviar email mediante Brevo: " + e.getResponseBody());
      throw new RuntimeException("Error al procesar el envío de correo", e);
    }

  }

}
