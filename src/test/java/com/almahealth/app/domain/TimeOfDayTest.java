package com.almahealth.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.almahealth.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimeOfDayTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeOfDay.class);
        TimeOfDay timeOfDay1 = new TimeOfDay();
        timeOfDay1.setId(1L);
        TimeOfDay timeOfDay2 = new TimeOfDay();
        timeOfDay2.setId(timeOfDay1.getId());
        assertThat(timeOfDay1).isEqualTo(timeOfDay2);
        timeOfDay2.setId(2L);
        assertThat(timeOfDay1).isNotEqualTo(timeOfDay2);
        timeOfDay1.setId(null);
        assertThat(timeOfDay1).isNotEqualTo(timeOfDay2);
    }
}
