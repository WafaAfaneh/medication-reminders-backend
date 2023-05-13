package com.almahealth.app.service;

import com.almahealth.app.config.ApplicationProperties;
import com.almahealth.app.service.dto.FCMMessageDTO;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FcmClient {

    private final Logger log = LoggerFactory.getLogger(FcmClient.class);

    private FirebaseApp firebaseApp;

    private final FcmNotificationService fcmNotificationService;

    public FcmClient(ApplicationProperties settings, FcmNotificationService fcmNotificationService) {
        this.fcmNotificationService = fcmNotificationService;

        try {
            FileInputStream serviceAccount = new FileInputStream(settings.getFcmServiceAccountFile());

            FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

            firebaseApp = FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            log.error("init fcm", e);
        }
    }

    @Async
    public void sendNotificationDirect(String token, List<FCMMessageDTO> messageDTOs) {
        List<Message> messages = prepareNotificationsMessagesPerUser(token, messageDTOs);
        messages.forEach(message -> {
            fcmNotificationService.sendNotificationDirect(token, message, firebaseApp);
        });
    }

    private List<Message> prepareNotificationsMessagesPerUser(String token, List<FCMMessageDTO> FCMMessageDTOS) {
        return FCMMessageDTOS
            .stream()
            .map(item ->
                Message
                    .builder()
                    .setNotification(Notification.builder().build())
                    .setToken(token)
                    .setApnsConfig(prepareIos(item))
                    .setAndroidConfig(prepareAndroid(item))
                    .build()
            )
            .collect(Collectors.toList());
    }

    public AndroidConfig prepareAndroid(FCMMessageDTO FCMMessageDTO) {
        return AndroidConfig
            .builder()
            .setPriority(AndroidConfig.Priority.HIGH)
            .setNotification(
                AndroidNotification
                    .builder()
                    .setTitle(FCMMessageDTO.getTitle())
                    .setBody(FCMMessageDTO.getDate())
                    .setSound("default")
                    .build()
            )
            .build();
    }

    public ApnsConfig prepareIos(FCMMessageDTO FCMMessageDTO) {
        return ApnsConfig
            .builder()
            .setAps(
                Aps
                    .builder()
                    .setSound("default")
                    .setMutableContent(true)
                    .setAlert(ApsAlert.builder().setTitle(FCMMessageDTO.getTitle()).setBody(FCMMessageDTO.getDate()).build())
                    .build()
            )
            .build();
    }
}
