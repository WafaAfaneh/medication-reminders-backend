package com.almahealth.app.repository;

import com.almahealth.app.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {}
