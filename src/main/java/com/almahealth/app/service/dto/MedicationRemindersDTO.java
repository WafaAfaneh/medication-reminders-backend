package com.almahealth.app.service.dto;

import com.almahealth.app.domain.enumeration.MedicationType;
import com.almahealth.app.domain.enumeration.Status;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A DTO for the {@link com.almahealth.app.domain.Reminder} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MedicationRemindersDTO implements Serializable {

    private Long id;

    private String name;

    private Long userId;

    private List<ReminderDTO> reminders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<ReminderDTO> getReminders() {
        return reminders;
    }

    public void setReminders(List<ReminderDTO> reminders) {
        this.reminders = reminders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MedicationRemindersDTO)) {
            return false;
        }

        MedicationRemindersDTO reminderDTO = (MedicationRemindersDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, reminderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
