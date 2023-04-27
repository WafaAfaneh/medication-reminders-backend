package com.almahealth.app.service.dto;

import com.almahealth.app.domain.enumeration.MedicationType;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

/**
 * A DTO for the {@link com.almahealth.app.domain.Medication} entity.
 */
public class MedicationDTO implements Serializable {

    private Long id;

    @NotNull
    @NotBlank
    @Size(min = 2, message = "Too short name")
    @Size(max = 200, message = "Too long name")
    private String name;

    private Double dosageQuantity;

    private MedicationType type;

    @NotNull
    @Valid
    private NotificationDTO notification;

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

    public Double getDosageQuantity() {
        return dosageQuantity;
    }

    public void setDosageQuantity(Double dosageQuantity) {
        this.dosageQuantity = dosageQuantity;
    }

    public MedicationType getType() {
        return type;
    }

    public void setType(MedicationType type) {
        this.type = type;
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
        if (!(o instanceof MedicationDTO)) {
            return false;
        }

        MedicationDTO medicationDTO = (MedicationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, medicationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MedicationDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", dosageQuantity=" + getDosageQuantity() +
            ", type='" + getType() + "'" +
            ", notification=" + getNotification() +
            "}";
    }
}
