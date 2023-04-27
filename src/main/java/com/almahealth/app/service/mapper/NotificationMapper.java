package com.almahealth.app.service.mapper;

import com.almahealth.app.domain.Frequency;
import com.almahealth.app.domain.Notification;
import com.almahealth.app.domain.User;
import com.almahealth.app.service.dto.FrequencyDTO;
import com.almahealth.app.service.dto.NotificationDTO;
import com.almahealth.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring", uses = { FrequencyMapper.class, TimeOfDayMapper.class })
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {}
