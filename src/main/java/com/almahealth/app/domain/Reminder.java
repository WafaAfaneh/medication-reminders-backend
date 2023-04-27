package com.almahealth.app.domain;

import com.almahealth.app.domain.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import org.hibernate.annotations.Cascade;

/**
 * A Reminder.
 */
@Entity
@Table(name = "reminder")
public class Reminder extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private Instant date;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "notification_id")
    // @JsonIgnoreProperties(value = { "frequency", "reminders", "timeOfDays", "user", "medication" }, allowSetters = true)
    private Notification notification;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Reminder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return this.date;
    }

    public Reminder date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Status getStatus() {
        return this.status;
    }

    public Reminder status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Notification getNotification() {
        return this.notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Reminder notification(Notification notification) {
        this.setNotification(notification);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reminder)) {
            return false;
        }
        return id != null && id.equals(((Reminder) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reminder{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
