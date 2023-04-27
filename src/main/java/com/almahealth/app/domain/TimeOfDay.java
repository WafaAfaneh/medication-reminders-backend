package com.almahealth.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalTime;
import javax.persistence.*;
import org.hibernate.annotations.Cascade;

/**
 * A TimeOfDay.
 */
@Entity
@Table(name = "time_of_day")
public class TimeOfDay implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "time")
    private LocalTime time;

    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "notification_id")
    @JsonIgnoreProperties(value = { "frequency", "reminders", "timeOfDays", "user", "medication" }, allowSetters = true)
    private Notification notification;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TimeOfDay id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getTime() {
        return this.time;
    }

    public TimeOfDay time(LocalTime time) {
        this.setTime(time);
        return this;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Notification getNotification() {
        return this.notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public TimeOfDay notification(Notification notification) {
        this.setNotification(notification);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeOfDay)) {
            return false;
        }
        return id != null && id.equals(((TimeOfDay) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimeOfDay{" +
            "id=" + getId() +
            ", time='" + getTime() + "'" +
            "}";
    }
}
