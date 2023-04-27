package com.almahealth.app.service.dto;

import com.almahealth.app.domain.enumeration.FrequencyType;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.almahealth.app.domain.Frequency} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FrequencyDTO implements Serializable {

    private Long id;

    private FrequencyType type;

    private Boolean saturday;

    private Boolean sunday;

    private Boolean monday;

    private Boolean tuesday;

    private Boolean wednesday;

    private Boolean thursday;

    private Boolean friday;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FrequencyType getType() {
        return type;
    }

    public void setType(FrequencyType type) {
        this.type = type;
    }

    public Boolean getSaturday() {
        return saturday;
    }

    public void setSaturday(Boolean saturday) {
        this.saturday = saturday;
    }

    public Boolean getSunday() {
        return sunday;
    }

    public void setSunday(Boolean sunday) {
        this.sunday = sunday;
    }

    public Boolean getMonday() {
        return monday;
    }

    public void setMonday(Boolean monday) {
        this.monday = monday;
    }

    public Boolean getTuesday() {
        return tuesday;
    }

    public void setTuesday(Boolean tuesday) {
        this.tuesday = tuesday;
    }

    public Boolean getWednesday() {
        return wednesday;
    }

    public void setWednesday(Boolean wednesday) {
        this.wednesday = wednesday;
    }

    public Boolean getThursday() {
        return thursday;
    }

    public void setThursday(Boolean thursday) {
        this.thursday = thursday;
    }

    public Boolean getFriday() {
        return friday;
    }

    public void setFriday(Boolean friday) {
        this.friday = friday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FrequencyDTO)) {
            return false;
        }

        FrequencyDTO frequencyDTO = (FrequencyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, frequencyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FrequencyDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", saturday='" + getSaturday() + "'" +
            ", sunday='" + getSunday() + "'" +
            ", monday='" + getMonday() + "'" +
            ", tuesday='" + getTuesday() + "'" +
            ", wednesday='" + getWednesday() + "'" +
            ", thursday='" + getThursday() + "'" +
            ", friday='" + getFriday() + "'" +
            "}";
    }
}
