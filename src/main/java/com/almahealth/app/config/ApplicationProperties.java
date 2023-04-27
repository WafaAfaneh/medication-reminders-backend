package com.almahealth.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Medication Reminder Server.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private String fcmServiceAccountFile;

    public String getFcmServiceAccountFile() {
        return fcmServiceAccountFile;
    }

    public void setFcmServiceAccountFile(String fcmServiceAccountFile) {
        this.fcmServiceAccountFile = fcmServiceAccountFile;
    }
}
