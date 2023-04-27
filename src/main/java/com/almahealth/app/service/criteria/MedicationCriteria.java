package com.almahealth.app.service.criteria;

import com.almahealth.app.domain.enumeration.MedicationType;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.almahealth.app.domain.Medication} entity. This class is used
 * in {@link com.almahealth.app.web.rest.MedicationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /medications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class MedicationCriteria implements Serializable, Criteria {

    /**
     * Class for filtering MedicationType
     */
    public static class MedicationTypeFilter extends Filter<MedicationType> {

        public MedicationTypeFilter() {}

        public MedicationTypeFilter(MedicationTypeFilter filter) {
            super(filter);
        }

        @Override
        public MedicationTypeFilter copy() {
            return new MedicationTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private DoubleFilter dosageQuantity;

    private MedicationTypeFilter type;

    private BooleanFilter active;

    private LongFilter notificationId;

    private LongFilter userId;

    private Boolean distinct;

    public MedicationCriteria() {}

    public MedicationCriteria(MedicationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.dosageQuantity = other.dosageQuantity == null ? null : other.dosageQuantity.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.notificationId = other.notificationId == null ? null : other.notificationId.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public MedicationCriteria copy() {
        return new MedicationCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public DoubleFilter getDosageQuantity() {
        return dosageQuantity;
    }

    public DoubleFilter dosageQuantity() {
        if (dosageQuantity == null) {
            dosageQuantity = new DoubleFilter();
        }
        return dosageQuantity;
    }

    public void setDosageQuantity(DoubleFilter dosageQuantity) {
        this.dosageQuantity = dosageQuantity;
    }

    public MedicationTypeFilter getType() {
        return type;
    }

    public MedicationTypeFilter type() {
        if (type == null) {
            type = new MedicationTypeFilter();
        }
        return type;
    }

    public void setType(MedicationTypeFilter type) {
        this.type = type;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public BooleanFilter active() {
        if (active == null) {
            active = new BooleanFilter();
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
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

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final MedicationCriteria that = (MedicationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(dosageQuantity, that.dosageQuantity) &&
            Objects.equals(type, that.type) &&
            Objects.equals(active, that.active) &&
            Objects.equals(notificationId, that.notificationId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dosageQuantity, type, active, notificationId, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MedicationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (dosageQuantity != null ? "dosageQuantity=" + dosageQuantity + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            (notificationId != null ? "notificationId=" + notificationId + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
