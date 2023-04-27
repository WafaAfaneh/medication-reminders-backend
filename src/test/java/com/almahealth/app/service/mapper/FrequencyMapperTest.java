package com.almahealth.app.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FrequencyMapperTest {

    private FrequencyMapper frequencyMapper;

    @BeforeEach
    public void setUp() {
        frequencyMapper = new FrequencyMapperImpl();
    }
}
