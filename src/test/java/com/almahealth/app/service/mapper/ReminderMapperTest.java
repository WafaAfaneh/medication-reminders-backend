package com.almahealth.app.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReminderMapperTest {

    private ReminderMapper reminderMapper;

    @BeforeEach
    public void setUp() {
        reminderMapper = new ReminderMapperImpl();
    }
}
