package com.almahealth.app.domain;

import com.almahealth.app.domain.enumeration.FrequencyType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A Frequency.
 */
@Entity
@Table(name = "frequency")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Frequency implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private FrequencyType type;

    @Column(name = "saturday")
    private Boolean saturday;

    @Column(name = "sunday")
    private Boolean sunday;

    @Column(name = "monday")
    private Boolean monday;

    @Column(name = "tuesday")
    private Boolean tuesday;

    @Column(name = "wednesday")
    private Boolean wednesday;

    @Column(name = "thursday")
    private Boolean thursday;

    @Column(name = "friday")
    private Boolean friday;

    @JsonIgnoreProperties(value = { "frequency", "reminders", "timeOfDays", "medication" }, allowSetters = true)
    @OneToOne(mappedBy = "frequency")
    private Notification notification;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Frequency id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FrequencyType getType() {
        return this.type;
    }

    public Frequency type(FrequencyType type) {
        this.setType(type);
        return this;
    }

    public void setType(FrequencyType type) {
        this.type = type;
    }

    public Boolean getSaturday() {
        return this.saturday;
    }

    public Frequency saturday(Boolean saturday) {
        this.setSaturday(saturday);
        return this;
    }

    public void setSaturday(Boolean saturday) {
        this.saturday = saturday;
    }

    public Boolean getSunday() {
        return this.sunday;
    }

    public Frequency sunday(Boolean sunday) {
        this.setSunday(sunday);
        return this;
    }

    public void setSunday(Boolean sunday) {
        this.sunday = sunday;
    }

    public Boolean getMonday() {
        return this.monday;
    }

    public Frequency monday(Boolean monday) {
        this.setMonday(monday);
        return this;
    }

    public void setMonday(Boolean monday) {
        this.monday = monday;
    }

    public Boolean getTuesday() {
        return this.tuesday;
    }

    public Frequency tuesday(Boolean tuesday) {
        this.setTuesday(tuesday);
        return this;
    }

    public void setTuesday(Boolean tuesday) {
        this.tuesday = tuesday;
    }

    public Boolean getWednesday() {
        return this.wednesday;
    }

    public Frequency wednesday(Boolean wednesday) {
        this.setWednesday(wednesday);
        return this;
    }

    public void setWednesday(Boolean wednesday) {
        this.wednesday = wednesday;
    }

    public Boolean getThursday() {
        return this.thursday;
    }

    public Frequency thursday(Boolean thursday) {
        this.setThursday(thursday);
        return this;
    }

    public void setThursday(Boolean thursday) {
        this.thursday = thursday;
    }

    public Boolean getFriday() {
        return this.friday;
    }

    public Frequency friday(Boolean friday) {
        this.setFriday(friday);
        return this;
    }

    public void setFriday(Boolean friday) {
        this.friday = friday;
    }

    public Notification getNotification() {
        return this.notification;
    }

    public void setNotification(Notification notification) {
        if (this.notification != null) {
            this.notification.setFrequency(null);
        }
        if (notification != null) {
            notification.setFrequency(this);
        }
        this.notification = notification;
    }

    public Frequency notification(Notification notification) {
        this.setNotification(notification);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Frequency)) {
            return false;
        }
        return id != null && id.equals(((Frequency) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Frequency{" +
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
