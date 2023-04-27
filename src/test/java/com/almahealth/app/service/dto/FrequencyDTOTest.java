package com.almahealth.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.almahealth.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FrequencyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FrequencyDTO.class);
        FrequencyDTO frequencyDTO1 = new FrequencyDTO();
        frequencyDTO1.setId(1L);
        FrequencyDTO frequencyDTO2 = new FrequencyDTO();
        assertThat(frequencyDTO1).isNotEqualTo(frequencyDTO2);
        frequencyDTO2.setId(frequencyDTO1.getId());
        assertThat(frequencyDTO1).isEqualTo(frequencyDTO2);
        frequencyDTO2.setId(2L);
        assertThat(frequencyDTO1).isNotEqualTo(frequencyDTO2);
        frequencyDTO1.setId(null);
        assertThat(frequencyDTO1).isNotEqualTo(frequencyDTO2);
    }
}
