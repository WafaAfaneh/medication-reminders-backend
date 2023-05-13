package com.almahealth.app.repository;

import com.almahealth.app.domain.Medication;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Medication entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long>, JpaSpecificationExecutor<Medication> {
    @Query("select medication from Medication medication where medication.user.login = ?#{principal.username} and medication.active = true")
    List<Medication> findByUserIsCurrentUser();
}
