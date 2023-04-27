package com.almahealth.app.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A DTO for the {@link com.almahealth.app.domain.Notification} entity.
 */
public class NotificationDTO implements Serializable {

    private Long id;

    @Size(max = 200, message = "Too long name")
    private String displayName;

    @Size(max = 200, message = "Too long notes")
    private String notes;

    @NotNull(message = "Start date may not be null")
    @Valid
    private LocalDate startDate;

    @NotNull
    @Valid
    private FrequencyDTO frequency;

    @NotEmpty(message = "Time of day list may not be empty")
    @Valid
    private List<TimeOfDayDTO> timeOfDays;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public FrequencyDTO getFrequency() {
        return frequency;
    }

    public void setFrequency(FrequencyDTO frequency) {
        this.frequency = frequency;
    }

    public List<TimeOfDayDTO> getTimeOfDays() {
        return timeOfDays;
    }

    public void setTimeOfDays(List<TimeOfDayDTO> timeOfDays) {
        this.timeOfDays = timeOfDays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationDTO)) {
            return false;
        }

        NotificationDTO notificationDTO = (NotificationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notificationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationDTO{" +
            "id=" + getId() +
            ", displayName='" + getDisplayName() + "'" +
            ", notes='" + getNotes() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", frequency=" + getFrequency() +
            "}";
    }
}
