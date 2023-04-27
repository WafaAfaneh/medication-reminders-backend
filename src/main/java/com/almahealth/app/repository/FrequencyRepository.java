package com.almahealth.app.repository;

import com.almahealth.app.domain.Frequency;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Frequency entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FrequencyRepository extends JpaRepository<Frequency, Long> {}
