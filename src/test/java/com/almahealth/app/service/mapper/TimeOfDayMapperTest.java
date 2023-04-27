package com.almahealth.app.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimeOfDayMapperTest {

    private TimeOfDayMapper timeOfDayMapper;

    @BeforeEach
    public void setUp() {
        timeOfDayMapper = new TimeOfDayMapperImpl();
    }
}
