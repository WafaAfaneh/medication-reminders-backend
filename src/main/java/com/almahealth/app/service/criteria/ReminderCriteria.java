package com.almahealth.app.service.criteria;

import com.almahealth.app.domain.enumeration.Status;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.almahealth.app.domain.Reminder} entity. This class is used
 * in {@link com.almahealth.app.web.rest.ReminderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /reminders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class ReminderCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Status
     */
    public static class StatusFilter extends Filter<Status> {

        public StatusFilter() {}

        public StatusFilter(StatusFilter filter) {
            super(filter);
        }

        @Override
        public StatusFilter copy() {
            return new StatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter date;

    private StatusFilter status;

    private LongFilter notificationId;

    private Boolean distinct;

    public ReminderCriteria() {}

    public ReminderCriteria(ReminderCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.notificationId = other.notificationId == null ? null : other.notificationId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ReminderCriteria copy() {
        return new ReminderCriteria(this);
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

    public InstantFilter getDate() {
        return date;
    }

    public InstantFilter date() {
        if (date == null) {
            date = new InstantFilter();
        }
        return date;
    }

    public void setDate(InstantFilter date) {
        this.date = date;
    }

    public StatusFilter getStatus() {
        return status;
    }

    public StatusFilter status() {
        if (status == null) {
            status = new StatusFilter();
        }
        return status;
    }

    public void setStatus(StatusFilter status) {
        this.status = status;
    }

    public LongFilter getNotificationId() {
        return notificationId;
    }

    public LongFilter notificationId() {
        if (notificationId == null) {
            notificationId = new LongFilter();
        }
        return notificationId;
    }

    public void setNotificationId(LongFilter notificationId) {
        this.notificationId = notificationId;
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
        final ReminderCriteria that = (ReminderCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(date, that.date) &&
            Objects.equals(status, that.status) &&
            Objects.equals(notificationId, that.notificationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, status, notificationId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReminderCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (date != null ? "date=" + date + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (notificationId != null ? "notificationId=" + notificationId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
