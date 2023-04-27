package com.almahealth.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FcmNotificationService {

    private final Logger log = LoggerFactory.getLogger(FcmNotificationService.class);

    private final ObjectMapper objectMapper;

    public FcmNotificationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Async
    public void sendData(
        Object data,
        Set<String> tokens,
        String notificationType,
        AndroidConfig androidConfig,
        ApnsConfig apnsConfig,
        FirebaseApp firebaseApp
    ) {
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
        Map<String, String> map = objectMapper.convertValue(data, typeRef);
        map.put("content-available", "1");
        map.put("notificationType", notificationType);
        if (map == null) {
            map = new HashMap<>();
        }
        for (String token : tokens) {
            try {
                Message message = Message
                    .builder()
                    .putAllData(map)
                    .setAndroidConfig(androidConfig)
                    .setApnsConfig(apnsConfig)
                    .setToken(token)
                    .build();
                String response = FirebaseMessaging.getInstance(firebaseApp).sendAsync(message).get();
                System.out.println("Sent message: " + response);
            } catch (Exception e) {
                log.error("Error while sending data to device with token: " + token, e);
            }
        }
    }

    @Async
    public void sendData(Object data, String topic, FirebaseApp firebaseApp) {
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
        Map<String, String> map = objectMapper.convertValue(data, typeRef);
        if (map == null) {
            map = new HashMap<>();
        }
        try {
            Message message = Message.builder().putAllData(map).setTopic(topic).build();

            String response = FirebaseMessaging.getInstance(firebaseApp).sendAsync(message).get();
            System.out.println("Sent message: " + response);
        } catch (Exception e) {
            log.error("Error while sending data to topic: " + topic, e);
        }
    }

    @Async
    public void sendNotification(String topic, AndroidConfig androidConfig, ApnsConfig apnsConfig, FirebaseApp firebaseApp)
        throws InterruptedException, ExecutionException {
        Notification notification = Notification.builder().setTitle("Hi from BE").setBody("Bye from BE").build();

        Message message = Message
            .builder()
            .setTopic(topic)
            .setNotification(notification)
            .setApnsConfig(apnsConfig)
            .setAndroidConfig(androidConfig)
            .build();

        String response = FirebaseMessaging.getInstance(firebaseApp).sendAsync(message).get();
        log.info("Sent message: " + response);
    }

    @Async
    public void sendNotificationDirect(String token, Message message, FirebaseApp firebaseApp) {
        try {
            String response = FirebaseMessaging.getInstance(firebaseApp).sendAsync(message).get();
            log.info("Sent message: " + response);
            log.info("message sent to token: " + token + "   , notification body");
        } catch (Exception e) {
            log.error("Error while sending push notification to device with token: " + token, e);
        }
    }
}
