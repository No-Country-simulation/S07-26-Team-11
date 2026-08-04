package com.dcplatform.api.leads;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
public class MagicLinkNotifer {

    List<INotification> observers = new ArrayList<>();

    public void subscribe(INotification observer) {
        this.observers.add(observer);
    }

    public void SendNotificacion(Lead lead, LeadAccessTokens leadAccessTokens) {

        for (INotification observer : observers) {
            try {
                observer.sendNotification(lead, leadAccessTokens);
            } catch (Exception e) {
                System.err.println("Falló un canal de notificación: " + e.getMessage());
            }
        }
    }

}
