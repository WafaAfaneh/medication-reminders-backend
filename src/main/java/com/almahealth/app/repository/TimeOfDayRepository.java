package com.almahealth.app.repository;

import com.almahealth.app.domain.TimeOfDay;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TimeOfDay entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TimeOfDayRepository extends JpaRepository<TimeOfDay, Long> {}
