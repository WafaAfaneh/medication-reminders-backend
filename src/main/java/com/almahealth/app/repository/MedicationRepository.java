package com.almahealth.app.repository;

import com.almahealth.app.domain.Medication;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Medication entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long>, JpaSpecificationExecutor<Medication> {
    @Query("select medication from Medication medication where medication.user.login = ?#{principal.username} and medication.active = true")
    List<Medication> findByUserIsCurrentUser();

    @Query(
        "select medication from Medication medication where medication.active = true " +
        "and medication.notification.frequency.type = 'DAILY' " +
        "or (medication.notification.frequency.type = 'WEEKLY' " +
        "and medication.notification.frequency.saturday = true)"
    )
    List<Medication> findAllSaturdayMedications();

    @Query(
        "select medication from Medication medication where medication.active = true " +
        "and medication.notification.frequency.type = 'DAILY' " +
        "or (medication.notification.frequency.type = 'WEEKLY' " +
        "and medication.notification.frequency.sunday = true)"
    )
    List<Medication> findAllSundayMedications();

    @Query(
        "select medication from Medication medication where medication.active = true " +
        "and medication.notification.startDate <= :date " +
        "and medication.notification.frequency.type = 'DAILY' " +
        "or (medication.notification.frequency.type = 'WEEKLY' " +
        "and medication.notification.frequency.monday = true)"
    )
    List<Medication> findAllMondayMedications(@Param("date") LocalDate date);

    @Query(
        "select medication from Medication medication where medication.active = true " +
        "and medication.notification.frequency.type = 'DAILY' " +
        "or (medication.notification.frequency.type = 'WEEKLY' " +
        "and medication.notification.frequency.tuesday = true)"
    )
    List<Medication> findAllTuesdayMedications();

    @Query(
        "select medication from Medication medication where medication.active = true " +
        "and medication.notification.frequency.type = 'DAILY' " +
        "or (medication.notification.frequency.type = 'WEEKLY' " +
        "and medication.notification.frequency.wednesday = true)"
    )
    List<Medication> findAllWednesdayMedications();

    @Query(
        "select medication from Medication medication where medication.active = true " +
        "and medication.notification.frequency.type = 'DAILY' " +
        "or (medication.notification.frequency.type = 'WEEKLY' " +
        "and medication.notification.frequency.thursday = true)"
    )
    List<Medication> findAllThursdayMedications();

    @Query(
        "select medication from Medication medication where medication.active = true " +
        "and medication.notification.frequency.type = 'DAILY' " +
        "or (medication.notification.frequency.type = 'WEEKLY' " +
        "and medication.notification.frequency.friday = true)"
    )
    List<Medication> findAllFridayMedications();
}
