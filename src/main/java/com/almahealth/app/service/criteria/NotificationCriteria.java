package com.almahealth.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.almahealth.app.domain.Notification} entity. This class is used
 * in {@link com.almahealth.app.web.rest.NotificationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /notifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class NotificationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter displayName;

    private StringFilter notes;

    private LocalDateFilter startDate;

    private LongFilter frequencyId;

    private LongFilter reminderId;

    private LongFilter timeOfDayId;

    private LongFilter medicationId;

    private Boolean distinct;

    public NotificationCriteria() {}

    public NotificationCriteria(NotificationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.displayName = other.displayName == null ? null : other.displayName.copy();
        this.notes = other.notes == null ? null : other.notes.copy();
        this.startDate = other.startDate == null ? null : other.startDate.copy();
        this.frequencyId = other.frequencyId == null ? null : other.frequencyId.copy();
        this.reminderId = other.reminderId == null ? null : other.reminderId.copy();
        this.timeOfDayId = other.timeOfDayId == null ? null : other.timeOfDayId.copy();
        this.medicationId = other.medicationId == null ? null : other.medicationId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public NotificationCriteria copy() {
        return new NotificationCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getDisplayName() {
        return displayName;
    }

    public StringFilter displayName() {
        if (displayName == null) {
            displayName = new StringFilter();
        }
        return displayName;
    }

    public void setDisplayName(StringFilter displayName) {
        this.displayName = displayName;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public StringFilter notes() {
        if (notes == null) {
            notes = new StringFilter();
        }
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
    }

    public LocalDateFilter getStartDate() {
        return startDate;
    }

    public LocalDateFilter startDate() {
        if (startDate == null) {
            startDate = new LocalDateFilter();
        }
        return startDate;
    }

    public void setStartDate(LocalDateFilter startDate) {
        this.startDate = startDate;
    }

    public LongFilter getFrequencyId() {
        return frequencyId;
    }

    public LongFilter frequencyId() {
        if (frequencyId == null) {
            frequencyId = new LongFilter();
        }
        return frequencyId;
    }

    public void setFrequencyId(LongFilter frequencyId) {
        this.frequencyId = frequencyId;
    }

    public LongFilter getReminderId() {
        return reminderId;
    }

    public LongFilter reminderId() {
        if (reminderId == null) {
            reminderId = new LongFilter();
        }
        return reminderId;
    }

    public void setReminderId(LongFilter reminderId) {
        this.reminderId = reminderId;
    }

    public LongFilter getTimeOfDayId() {
        return timeOfDayId;
    }

    public LongFilter timeOfDayId() {
        if (timeOfDayId == null) {
            timeOfDayId = new LongFilter();
        }
        return timeOfDayId;
    }

    public void setTimeOfDayId(LongFilter timeOfDayId) {
        this.timeOfDayId = timeOfDayId;
    }

    public LongFilter getMedicationId() {
        return medicationId;
    }

    public LongFilter medicationId() {
        if (medicationId == null) {
            medicationId = new LongFilter();
        }
        return medicationId;
    }

    public void setMedicationId(LongFilter medicationId) {
        this.medicationId = medicationId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NotificationCriteria that = (NotificationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(displayName, that.displayName) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(frequencyId, that.frequencyId) &&
            Objects.equals(reminderId, that.reminderId) &&
            Objects.equals(timeOfDayId, that.timeOfDayId) &&
            Objects.equals(medicationId, that.medicationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, displayName, notes, startDate, frequencyId, reminderId, timeOfDayId, medicationId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (displayName != null ? "displayName=" + displayName + ", " : "") +
            (notes != null ? "notes=" + notes + ", " : "") +
            (startDate != null ? "startDate=" + startDate + ", " : "") +
            (frequencyId != null ? "frequencyId=" + frequencyId + ", " : "") +
            (reminderId != null ? "reminderId=" + reminderId + ", " : "") +
            (timeOfDayId != null ? "timeOfDayId=" + timeOfDayId + ", " : "") +
            (medicationId != null ? "medicationId=" + medicationId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
