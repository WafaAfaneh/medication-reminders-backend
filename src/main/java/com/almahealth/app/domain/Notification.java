package com.almahealth.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Notification.
 */
@Entity
@Table(name = "notification")
public class Notification extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "notes")
    private String notes;

    @Column(name = "start_date")
    private LocalDate startDate;

    @JsonIgnoreProperties(value = { "notification" }, allowSetters = true)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "frequency_id", referencedColumnName = "id", unique = true)
    private Frequency frequency;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "notification" }, allowSetters = true)
    private Set<Reminder> reminders = new HashSet<>();

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "notification" }, allowSetters = true)
    private Set<TimeOfDay> timeOfDays = new HashSet<>();

    @JsonIgnoreProperties(value = { "notification" }, allowSetters = true)
    @OneToOne(mappedBy = "notification")
    private Medication medication;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Notification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Notification displayName(String displayName) {
        this.setDisplayName(displayName);
        return this;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNotes() {
        return this.notes;
    }

    public Notification notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public Notification startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Frequency getFrequency() {
        return this.frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Notification frequency(Frequency frequency) {
        this.setFrequency(frequency);
        return this;
    }

    public Set<Reminder> getReminders() {
        return this.reminders;
    }

    public void setReminders(Set<Reminder> reminders) {
        if (this.reminders != null) {
            this.reminders.forEach(i -> i.setNotification(null));
        }
        if (reminders != null) {
            reminders.forEach(i -> i.setNotification(this));
        }
        this.reminders = reminders;
    }

    public Notification reminders(Set<Reminder> reminders) {
        this.setReminders(reminders);
        return this;
    }

    public Notification addReminder(Reminder reminder) {
        this.reminders.add(reminder);
        reminder.setNotification(this);
        return this;
    }

    public Notification removeReminder(Reminder reminder) {
        this.reminders.remove(reminder);
        reminder.setNotification(null);
        return this;
    }

    public Set<TimeOfDay> getTimeOfDays() {
        return this.timeOfDays;
    }

    public void setTimeOfDays(Set<TimeOfDay> timeOfDays) {
        if (this.timeOfDays != null) {
            this.timeOfDays.forEach(i -> i.setNotification(null));
        }
        if (timeOfDays != null) {
            timeOfDays.forEach(i -> i.setNotification(this));
        }
        this.timeOfDays = timeOfDays;
    }

    public Notification timeOfDays(Set<TimeOfDay> timeOfDays) {
        this.setTimeOfDays(timeOfDays);
        return this;
    }

    public Notification addTimeOfDay(TimeOfDay timeOfDay) {
        this.timeOfDays.add(timeOfDay);
        timeOfDay.setNotification(this);
        return this;
    }

    public Notification removeTimeOfDay(TimeOfDay timeOfDay) {
        this.timeOfDays.remove(timeOfDay);
        timeOfDay.setNotification(null);
        return this;
    }

    public Medication getMedication() {
        return this.medication;
    }

    public void setMedication(Medication medication) {
        if (this.medication != null) {
            this.medication.setNotification(null);
        }
        if (medication != null) {
            medication.setNotification(this);
        }
        this.medication = medication;
    }

    public Notification medication(Medication medication) {
        this.setMedication(medication);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        return id != null && id.equals(((Notification) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notification{" +
            "id=" + getId() +
            ", displayName='" + getDisplayName() + "'" +
            ", notes='" + getNotes() + "'" +
            ", startDate='" + getStartDate() + "'" +
            "}";
    }
}
