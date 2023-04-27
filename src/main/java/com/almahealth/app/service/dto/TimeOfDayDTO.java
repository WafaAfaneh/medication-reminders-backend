package com.almahealth.app.service.dto;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * A DTO for the {@link com.almahealth.app.domain.TimeOfDay} entity.
 */
public class TimeOfDayDTO implements Serializable {

    private Long id;

    @NotNull
    @Valid
    private LocalTime time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeOfDayDTO)) {
            return false;
        }

        TimeOfDayDTO timeOfDayDTO = (TimeOfDayDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, timeOfDayDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimeOfDayDTO{" +
            "id=" + getId() +
            ", time='" + getTime() + "'" +
            "}";
    }
}
