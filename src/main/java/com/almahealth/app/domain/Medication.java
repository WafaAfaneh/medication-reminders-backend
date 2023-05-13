package com.almahealth.app.domain;

import com.almahealth.app.domain.enumeration.MedicationType;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A Medication.
 */
@Entity
@Table(name = "medication")
public class Medication extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "dosage_quantity")
    private Double dosageQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private MedicationType type;

    @Column(name = "active")
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "notification_id", referencedColumnName = "id", unique = true)
    private Notification notification;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Medication id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Medication name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDosageQuantity() {
        return this.dosageQuantity;
    }

    public Medication dosageQuantity(Double dosageQuantity) {
        this.setDosageQuantity(dosageQuantity);
        return this;
    }

    public void setDosageQuantity(Double dosageQuantity) {
        this.dosageQuantity = dosageQuantity;
    }

    public MedicationType getType() {
        return this.type;
    }

    public Medication type(MedicationType type) {
        this.setType(type);
        return this;
    }

    public void setType(MedicationType type) {
        this.type = type;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Medication active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public User getUser() {
        return this.user;
    }

    public Long getUserId() {
        return this.user.getId();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Medication user(User user) {
        this.setUser(user);
        return this;
    }

    public Medication userId(Long userId) {
        this.setUser(new User().id(userId));
        return this;
    }

    public Notification getNotification() {
        return this.notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Medication notification(Notification notification) {
        this.setNotification(notification);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Medication)) {
            return false;
        }
        return id != null && id.equals(((Medication) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Medication{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", dosageQuantity=" + getDosageQuantity() +
            ", type='" + getType() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
