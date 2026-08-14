package com.dcplatform.api.leads;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MagicLinkNotifer {

    private final List<INotification> observers;
    private static final Logger log = LoggerFactory.getLogger(MagicLinkNotifer.class);

    public MagicLinkNotifer(List<INotification> observers) {
        this.observers = observers != null ? observers : new ArrayList<>();
    }

    public void subscribe(INotification observer) {
        this.observers.add(observer);
    }

    @Async("mailExecutor")
    public void sendNotificacion(String email, String magicLink) {
        log.info("📧 [Hilo: {}] Notificando observadores para el correo {}",
                Thread.currentThread().getName(), email);

        for (INotification observer : observers) {
            try {
                observer.sendNotification(email, magicLink);
            } catch (Exception e) {
                System.err.println("Falló un canal de notificación: " + e.getMessage());
            }
        }
    }

}
