package com.almahealth.app.service.dto;

import com.almahealth.app.domain.enumeration.Status;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.almahealth.app.domain.Reminder} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReminderDTO implements Serializable {

    private Long id;

    private Instant date;

    private Status status;

    private NotificationDTO notification;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public NotificationDTO getNotification() {
        return notification;
    }

    public void setNotification(NotificationDTO notification) {
        this.notification = notification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReminderDTO)) {
            return false;
        }

        ReminderDTO reminderDTO = (ReminderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, reminderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReminderDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", notification=" + getNotification() +
            "}";
    }
}
