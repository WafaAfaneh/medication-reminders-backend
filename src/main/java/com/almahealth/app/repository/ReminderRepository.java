package com.almahealth.app.repository;

import com.almahealth.app.domain.Reminder;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Reminder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long>, JpaSpecificationExecutor<Reminder> {
    void deleteAllByNotificationIdAndDateGreaterThanEqual(Long notificationId, Instant date);
}
