package com.almahealth.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.almahealth.app.IntegrationTest;
import com.almahealth.app.domain.Frequency;
import com.almahealth.app.domain.enumeration.FrequencyType;
import com.almahealth.app.repository.FrequencyRepository;
import com.almahealth.app.service.dto.FrequencyDTO;
import com.almahealth.app.service.mapper.FrequencyMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FrequencyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FrequencyResourceIT {

    private static final FrequencyType DEFAULT_TYPE = FrequencyType.DAILY;
    private static final FrequencyType UPDATED_TYPE = FrequencyType.WEEKLY;

    private static final Boolean DEFAULT_SATURDAY = false;
    private static final Boolean UPDATED_SATURDAY = true;

    private static final Boolean DEFAULT_SUNDAY = false;
    private static final Boolean UPDATED_SUNDAY = true;

    private static final Boolean DEFAULT_MONDAY = false;
    private static final Boolean UPDATED_MONDAY = true;

    private static final Boolean DEFAULT_TUESDAY = false;
    private static final Boolean UPDATED_TUESDAY = true;

    private static final Boolean DEFAULT_WEDNESDAY = false;
    private static final Boolean UPDATED_WEDNESDAY = true;

    private static final Boolean DEFAULT_THURSDAY = false;
    private static final Boolean UPDATED_THURSDAY = true;

    private static final Boolean DEFAULT_FRIDAY = false;
    private static final Boolean UPDATED_FRIDAY = true;

    private static final String ENTITY_API_URL = "/api/frequencies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FrequencyRepository frequencyRepository;

    @Autowired
    private FrequencyMapper frequencyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFrequencyMockMvc;

    private Frequency frequency;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Frequency createEntity(EntityManager em) {
        Frequency frequency = new Frequency()
            .type(DEFAULT_TYPE)
            .saturday(DEFAULT_SATURDAY)
            .sunday(DEFAULT_SUNDAY)
            .monday(DEFAULT_MONDAY)
            .tuesday(DEFAULT_TUESDAY)
            .wednesday(DEFAULT_WEDNESDAY)
            .thursday(DEFAULT_THURSDAY)
            .friday(DEFAULT_FRIDAY);
        return frequency;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Frequency createUpdatedEntity(EntityManager em) {
        Frequency frequency = new Frequency()
            .type(UPDATED_TYPE)
            .saturday(UPDATED_SATURDAY)
            .sunday(UPDATED_SUNDAY)
            .monday(UPDATED_MONDAY)
            .tuesday(UPDATED_TUESDAY)
            .wednesday(UPDATED_WEDNESDAY)
            .thursday(UPDATED_THURSDAY)
            .friday(UPDATED_FRIDAY);
        return frequency;
    }

    @BeforeEach
    public void initTest() {
        frequency = createEntity(em);
    }

    @Test
    @Transactional
    void createFrequency() throws Exception {
        int databaseSizeBeforeCreate = frequencyRepository.findAll().size();
        // Create the Frequency
        FrequencyDTO frequencyDTO = frequencyMapper.toDto(frequency);
        restFrequencyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(frequencyDTO)))
            .andExpect(status().isCreated());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeCreate + 1);
        Frequency testFrequency = frequencyList.get(frequencyList.size() - 1);
        assertThat(testFrequency.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testFrequency.getSaturday()).isEqualTo(DEFAULT_SATURDAY);
        assertThat(testFrequency.getSunday()).isEqualTo(DEFAULT_SUNDAY);
        assertThat(testFrequency.getMonday()).isEqualTo(DEFAULT_MONDAY);
        assertThat(testFrequency.getTuesday()).isEqualTo(DEFAULT_TUESDAY);
        assertThat(testFrequency.getWednesday()).isEqualTo(DEFAULT_WEDNESDAY);
        assertThat(testFrequency.getThursday()).isEqualTo(DEFAULT_THURSDAY);
        assertThat(testFrequency.getFriday()).isEqualTo(DEFAULT_FRIDAY);
    }

    @Test
    @Transactional
    void createFrequencyWithExistingId() throws Exception {
        // Create the Frequency with an existing ID
        frequency.setId(1L);
        FrequencyDTO frequencyDTO = frequencyMapper.toDto(frequency);

        int databaseSizeBeforeCreate = frequencyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFrequencyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(frequencyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFrequencies() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get all the frequencyList
        restFrequencyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(frequency.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].saturday").value(hasItem(DEFAULT_SATURDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].sunday").value(hasItem(DEFAULT_SUNDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].monday").value(hasItem(DEFAULT_MONDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].tuesday").value(hasItem(DEFAULT_TUESDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].wednesday").value(hasItem(DEFAULT_WEDNESDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].thursday").value(hasItem(DEFAULT_THURSDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].friday").value(hasItem(DEFAULT_FRIDAY.booleanValue())));
    }

    @Test
    @Transactional
    void getFrequency() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get the frequency
        restFrequencyMockMvc
            .perform(get(ENTITY_API_URL_ID, frequency.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(frequency.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.saturday").value(DEFAULT_SATURDAY.booleanValue()))
            .andExpect(jsonPath("$.sunday").value(DEFAULT_SUNDAY.booleanValue()))
            .andExpect(jsonPath("$.monday").value(DEFAULT_MONDAY.booleanValue()))
            .andExpect(jsonPath("$.tuesday").value(DEFAULT_TUESDAY.booleanValue()))
            .andExpect(jsonPath("$.wednesday").value(DEFAULT_WEDNESDAY.booleanValue()))
            .andExpect(jsonPath("$.thursday").value(DEFAULT_THURSDAY.booleanValue()))
            .andExpect(jsonPath("$.friday").value(DEFAULT_FRIDAY.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingFrequency() throws Exception {
        // Get the frequency
        restFrequencyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFrequency() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();

        // Update the frequency
        Frequency updatedFrequency = frequencyRepository.findById(frequency.getId()).get();
        // Disconnect from session so that the updates on updatedFrequency are not directly saved in db
        em.detach(updatedFrequency);
        updatedFrequency
            .type(UPDATED_TYPE)
            .saturday(UPDATED_SATURDAY)
            .sunday(UPDATED_SUNDAY)
            .monday(UPDATED_MONDAY)
            .tuesday(UPDATED_TUESDAY)
            .wednesday(UPDATED_WEDNESDAY)
            .thursday(UPDATED_THURSDAY)
            .friday(UPDATED_FRIDAY);
        FrequencyDTO frequencyDTO = frequencyMapper.toDto(updatedFrequency);

        restFrequencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, frequencyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(frequencyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
        Frequency testFrequency = frequencyList.get(frequencyList.size() - 1);
        assertThat(testFrequency.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testFrequency.getSaturday()).isEqualTo(UPDATED_SATURDAY);
        assertThat(testFrequency.getSunday()).isEqualTo(UPDATED_SUNDAY);
        assertThat(testFrequency.getMonday()).isEqualTo(UPDATED_MONDAY);
        assertThat(testFrequency.getTuesday()).isEqualTo(UPDATED_TUESDAY);
        assertThat(testFrequency.getWednesday()).isEqualTo(UPDATED_WEDNESDAY);
        assertThat(testFrequency.getThursday()).isEqualTo(UPDATED_THURSDAY);
        assertThat(testFrequency.getFriday()).isEqualTo(UPDATED_FRIDAY);
    }

    @Test
    @Transactional
    void putNonExistingFrequency() throws Exception {
        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();
        frequency.setId(count.incrementAndGet());

        // Create the Frequency
        FrequencyDTO frequencyDTO = frequencyMapper.toDto(frequency);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFrequencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, frequencyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(frequencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFrequency() throws Exception {
        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();
        frequency.setId(count.incrementAndGet());

        // Create the Frequency
        FrequencyDTO frequencyDTO = frequencyMapper.toDto(frequency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFrequencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(frequencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFrequency() throws Exception {
        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();
        frequency.setId(count.incrementAndGet());

        // Create the Frequency
        FrequencyDTO frequencyDTO = frequencyMapper.toDto(frequency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFrequencyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(frequencyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFrequencyWithPatch() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();

        // Update the frequency using partial update
        Frequency partialUpdatedFrequency = new Frequency();
        partialUpdatedFrequency.setId(frequency.getId());

        partialUpdatedFrequency.type(UPDATED_TYPE).tuesday(UPDATED_TUESDAY).thursday(UPDATED_THURSDAY).friday(UPDATED_FRIDAY);

        restFrequencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFrequency.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFrequency))
            )
            .andExpect(status().isOk());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
        Frequency testFrequency = frequencyList.get(frequencyList.size() - 1);
        assertThat(testFrequency.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testFrequency.getSaturday()).isEqualTo(DEFAULT_SATURDAY);
        assertThat(testFrequency.getSunday()).isEqualTo(DEFAULT_SUNDAY);
        assertThat(testFrequency.getMonday()).isEqualTo(DEFAULT_MONDAY);
        assertThat(testFrequency.getTuesday()).isEqualTo(UPDATED_TUESDAY);
        assertThat(testFrequency.getWednesday()).isEqualTo(DEFAULT_WEDNESDAY);
        assertThat(testFrequency.getThursday()).isEqualTo(UPDATED_THURSDAY);
        assertThat(testFrequency.getFriday()).isEqualTo(UPDATED_FRIDAY);
    }

    @Test
    @Transactional
    void fullUpdateFrequencyWithPatch() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();

        // Update the frequency using partial update
        Frequency partialUpdatedFrequency = new Frequency();
        partialUpdatedFrequency.setId(frequency.getId());

        partialUpdatedFrequency
            .type(UPDATED_TYPE)
            .saturday(UPDATED_SATURDAY)
            .sunday(UPDATED_SUNDAY)
            .monday(UPDATED_MONDAY)
            .tuesday(UPDATED_TUESDAY)
            .wednesday(UPDATED_WEDNESDAY)
            .thursday(UPDATED_THURSDAY)
            .friday(UPDATED_FRIDAY);

        restFrequencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFrequency.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFrequency))
            )
            .andExpect(status().isOk());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
        Frequency testFrequency = frequencyList.get(frequencyList.size() - 1);
        assertThat(testFrequency.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testFrequency.getSaturday()).isEqualTo(UPDATED_SATURDAY);
        assertThat(testFrequency.getSunday()).isEqualTo(UPDATED_SUNDAY);
        assertThat(testFrequency.getMonday()).isEqualTo(UPDATED_MONDAY);
        assertThat(testFrequency.getTuesday()).isEqualTo(UPDATED_TUESDAY);
        assertThat(testFrequency.getWednesday()).isEqualTo(UPDATED_WEDNESDAY);
        assertThat(testFrequency.getThursday()).isEqualTo(UPDATED_THURSDAY);
        assertThat(testFrequency.getFriday()).isEqualTo(UPDATED_FRIDAY);
    }

    @Test
    @Transactional
    void patchNonExistingFrequency() throws Exception {
        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();
        frequency.setId(count.incrementAndGet());

        // Create the Frequency
        FrequencyDTO frequencyDTO = frequencyMapper.toDto(frequency);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFrequencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, frequencyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(frequencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFrequency() throws Exception {
        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();
        frequency.setId(count.incrementAndGet());

        // Create the Frequency
        FrequencyDTO frequencyDTO = frequencyMapper.toDto(frequency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFrequencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(frequencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFrequency() throws Exception {
        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();
        frequency.setId(count.incrementAndGet());

        // Create the Frequency
        FrequencyDTO frequencyDTO = frequencyMapper.toDto(frequency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFrequencyMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(frequencyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFrequency() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        int databaseSizeBeforeDelete = frequencyRepository.findAll().size();

        // Delete the frequency
        restFrequencyMockMvc
            .perform(delete(ENTITY_API_URL_ID, frequency.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
