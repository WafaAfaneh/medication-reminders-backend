package com.almahealth.app.service.util;

import com.almahealth.app.service.MedicationQueryService;
import com.almahealth.app.service.MedicationService;
import com.almahealth.app.service.dto.MedicationRemindersDTO;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class SystemCache implements InitializingBean {

    private final MedicationService medicationService;

    private Map<Long, List<MedicationRemindersDTO>> medicationRemindersMap;

    @Autowired
    public SystemCache(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    @Override
    public void afterPropertiesSet() {
        refreshMedicationsRemindersMap();
    }

    @Transactional(readOnly = true)
    public void refreshJob() {
        refreshMedicationsRemindersMap();
    }

    public void refreshMedicationsRemindersMap() {
        this.setMedicationRemindersMap(
                medicationService.findAllDayMedicationReminders().stream().collect(Collectors.groupingBy(MedicationRemindersDTO::getUserId))
            );
    }

    public void setMedicationRemindersMap(Map<Long, List<MedicationRemindersDTO>> medicationRemindersMap) {
        this.medicationRemindersMap = medicationRemindersMap;
    }

    public Map<Long, List<MedicationRemindersDTO>> getMedicationRemindersMap() {
        return medicationRemindersMap;
    }
}
