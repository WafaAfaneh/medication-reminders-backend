package com.almahealth.app.service.mapper;

import com.almahealth.app.domain.Medication;
import com.almahealth.app.security.SecurityUtils;
import com.almahealth.app.service.dto.MedicationDTO;
import com.almahealth.app.service.dto.MedicationRemindersDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Medication} and its DTO {@link MedicationDTO}.
 */
@Mapper(componentModel = "spring", uses = NotificationMapper.class)
public interface MedicationMapper extends EntityMapper<MedicationDTO, Medication> {
    @Named("update")
    @Mapping(target = "active", constant = "true")
    Medication toEntity(MedicationDTO dto);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "dosageQuantity", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "active", ignore = true)
    void partialUpdate(@MappingTarget Medication entity, MedicationDTO dto);

    @Mapping(target = "reminders", source = "notification.reminders")
    @Mapping(target = "userId", source = "user.id")
    MedicationRemindersDTO toMedicationReminders(Medication entity);

    @AfterMapping
    default void mapUser(@MappingTarget Medication entity) {
        entity.userId(SecurityUtils.getCurrentUser().getUserId());
    }
}
