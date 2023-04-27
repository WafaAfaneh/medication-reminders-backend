package com.almahealth.app.service.mapper;

import com.almahealth.app.domain.Notification;
import com.almahealth.app.domain.TimeOfDay;
import com.almahealth.app.service.dto.NotificationDTO;
import com.almahealth.app.service.dto.TimeOfDayDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TimeOfDay} and its DTO {@link TimeOfDayDTO}.
 */
@Mapper(componentModel = "spring")
public interface TimeOfDayMapper extends EntityMapper<TimeOfDayDTO, TimeOfDay> {}
