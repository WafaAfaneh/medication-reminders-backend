package com.almahealth.app.service.mapper;

import com.almahealth.app.domain.Frequency;
import com.almahealth.app.service.dto.FrequencyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Frequency} and its DTO {@link FrequencyDTO}.
 */
@Mapper(componentModel = "spring")
public interface FrequencyMapper extends EntityMapper<FrequencyDTO, Frequency> {}
