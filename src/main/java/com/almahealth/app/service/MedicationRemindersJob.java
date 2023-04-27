package com.almahealth.app.service;

import com.almahealth.app.service.util.SystemCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MedicationRemindersJob {

    private final MedicationService medicationService;

    private final SystemCache systemCache;

    public MedicationRemindersJob(MedicationService medicationService, SystemCache systemCache) {
        this.medicationService = medicationService;
        this.systemCache = systemCache;
    }

    @Scheduled(cron = "0 0 0 * * *")
    //        @Scheduled(cron = "0 * * * * *")
    public void create() {
        medicationService.createDayNotificationsReminders();
        systemCache.refreshJob();
        medicationService.sendDayNotificationsReminders();
    }
}
