package com.almahealth.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.almahealth.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimeOfDayDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeOfDayDTO.class);
        TimeOfDayDTO timeOfDayDTO1 = new TimeOfDayDTO();
        timeOfDayDTO1.setId(1L);
        TimeOfDayDTO timeOfDayDTO2 = new TimeOfDayDTO();
        assertThat(timeOfDayDTO1).isNotEqualTo(timeOfDayDTO2);
        timeOfDayDTO2.setId(timeOfDayDTO1.getId());
        assertThat(timeOfDayDTO1).isEqualTo(timeOfDayDTO2);
        timeOfDayDTO2.setId(2L);
        assertThat(timeOfDayDTO1).isNotEqualTo(timeOfDayDTO2);
        timeOfDayDTO1.setId(null);
        assertThat(timeOfDayDTO1).isNotEqualTo(timeOfDayDTO2);
    }
}
