package com.almahealth.app.service.mapper;

import com.almahealth.app.domain.Notification;
import com.almahealth.app.domain.Reminder;
import com.almahealth.app.service.dto.NotificationDTO;
import com.almahealth.app.service.dto.ReminderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reminder} and its DTO {@link ReminderDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReminderMapper extends EntityMapper<ReminderDTO, Reminder> {
    @Mapping(target = "notification", source = "notification", qualifiedByName = "notificationId")
    ReminderDTO toDto(Reminder s);

    @Named("notificationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NotificationDTO toDtoNotificationId(Notification notification);
}
