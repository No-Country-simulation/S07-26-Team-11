package com.dcplatform.api.leads;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class MagicLinkNotifer {

    List<INotification> observers = new ArrayList<>();

    public void subscribe(INotification observer) {
        this.observers.add(observer);
    }

    public void sendNotificacion(String email, String magicLink) {

        for (INotification observer : observers) {
            try {
                observer.sendNotification(email, magicLink);
            } catch (Exception e) {
                System.err.println("Falló un canal de notificación: " + e.getMessage());
            }
        }
    }

}
