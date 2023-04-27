package com.almahealth.app.web.rest;

import static com.almahealth.app.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.almahealth.app.IntegrationTest;
import com.almahealth.app.domain.TimeOfDay;
import com.almahealth.app.repository.TimeOfDayRepository;
import com.almahealth.app.service.dto.TimeOfDayDTO;
import com.almahealth.app.service.mapper.TimeOfDayMapper;
import java.time.*;
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
 * Integration tests for the {@link TimeOfDayResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TimeOfDayResourceIT {

    private static final LocalTime DEFAULT_TIME = LocalTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final LocalTime UPDATED_TIME = LocalTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/time-of-days";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TimeOfDayRepository timeOfDayRepository;

    @Autowired
    private TimeOfDayMapper timeOfDayMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTimeOfDayMockMvc;

    private TimeOfDay timeOfDay;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimeOfDay createEntity(EntityManager em) {
        TimeOfDay timeOfDay = new TimeOfDay().time(DEFAULT_TIME);
        return timeOfDay;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimeOfDay createUpdatedEntity(EntityManager em) {
        TimeOfDay timeOfDay = new TimeOfDay().time(UPDATED_TIME);
        return timeOfDay;
    }

    @BeforeEach
    public void initTest() {
        timeOfDay = createEntity(em);
    }

    @Test
    @Transactional
    void createTimeOfDay() throws Exception {
        int databaseSizeBeforeCreate = timeOfDayRepository.findAll().size();
        // Create the TimeOfDay
        TimeOfDayDTO timeOfDayDTO = timeOfDayMapper.toDto(timeOfDay);
        restTimeOfDayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeOfDayDTO)))
            .andExpect(status().isCreated());

        // Validate the TimeOfDay in the database
        List<TimeOfDay> timeOfDayList = timeOfDayRepository.findAll();
        assertThat(timeOfDayList).hasSize(databaseSizeBeforeCreate + 1);
        TimeOfDay testTimeOfDay = timeOfDayList.get(timeOfDayList.size() - 1);
        assertThat(testTimeOfDay.getTime()).isEqualTo(DEFAULT_TIME);
    }

    @Test
    @Transactional
    void createTimeOfDayWithExistingId() throws Exception {
        // Create the TimeOfDay with an existing ID
        timeOfDay.setId(1L);
        TimeOfDayDTO timeOfDayDTO = timeOfDayMapper.toDto(timeOfDay);

        int databaseSizeBeforeCreate = timeOfDayRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimeOfDayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeOfDayDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TimeOfDay in the database
        List<TimeOfDay> timeOfDayList = timeOfDayRepository.findAll();
        assertThat(timeOfDayList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTimeOfDays() throws Exception {
        // Initialize the database
        timeOfDayRepository.saveAndFlush(timeOfDay);

        // Get all the timeOfDayList
        restTimeOfDayMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timeOfDay.getId().intValue())))
            .andExpect(jsonPath("$.[*].time").value(hasItem(sameInstant(DEFAULT_TIME))));
    }

    @Test
    @Transactional
    void getTimeOfDay() throws Exception {
        // Initialize the database
        timeOfDayRepository.saveAndFlush(timeOfDay);

        // Get the timeOfDay
        restTimeOfDayMockMvc
            .perform(get(ENTITY_API_URL_ID, timeOfDay.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(timeOfDay.getId().intValue()))
            .andExpect(jsonPath("$.time").value(sameInstant(DEFAULT_TIME)));
    }

    @Test
    @Transactional
    void getNonExistingTimeOfDay() throws Exception {
        // Get the timeOfDay
        restTimeOfDayMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTimeOfDay() throws Exception {
        // Initialize the database
        timeOfDayRepository.saveAndFlush(timeOfDay);

        int databaseSizeBeforeUpdate = timeOfDayRepository.findAll().size();

        // Update the timeOfDay
        TimeOfDay updatedTimeOfDay = timeOfDayRepository.findById(timeOfDay.getId()).get();
        // Disconnect from session so that the updates on updatedTimeOfDay are not directly saved in db
        em.detach(updatedTimeOfDay);
        updatedTimeOfDay.time(UPDATED_TIME);
        TimeOfDayDTO timeOfDayDTO = timeOfDayMapper.toDto(updatedTimeOfDay);

        restTimeOfDayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timeOfDayDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timeOfDayDTO))
            )
            .andExpect(status().isOk());

        // Validate the TimeOfDay in the database
        List<TimeOfDay> timeOfDayList = timeOfDayRepository.findAll();
        assertThat(timeOfDayList).hasSize(databaseSizeBeforeUpdate);
        TimeOfDay testTimeOfDay = timeOfDayList.get(timeOfDayList.size() - 1);
        assertThat(testTimeOfDay.getTime()).isEqualTo(UPDATED_TIME);
    }

    @Test
    @Transactional
    void putNonExistingTimeOfDay() throws Exception {
        int databaseSizeBeforeUpdate = timeOfDayRepository.findAll().size();
        timeOfDay.setId(count.incrementAndGet());

        // Create the TimeOfDay
        TimeOfDayDTO timeOfDayDTO = timeOfDayMapper.toDto(timeOfDay);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimeOfDayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timeOfDayDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timeOfDayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeOfDay in the database
        List<TimeOfDay> timeOfDayList = timeOfDayRepository.findAll();
        assertThat(timeOfDayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTimeOfDay() throws Exception {
        int databaseSizeBeforeUpdate = timeOfDayRepository.findAll().size();
        timeOfDay.setId(count.incrementAndGet());

        // Create the TimeOfDay
        TimeOfDayDTO timeOfDayDTO = timeOfDayMapper.toDto(timeOfDay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeOfDayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timeOfDayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeOfDay in the database
        List<TimeOfDay> timeOfDayList = timeOfDayRepository.findAll();
        assertThat(timeOfDayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTimeOfDay() throws Exception {
        int databaseSizeBeforeUpdate = timeOfDayRepository.findAll().size();
        timeOfDay.setId(count.incrementAndGet());

        // Create the TimeOfDay
        TimeOfDayDTO timeOfDayDTO = timeOfDayMapper.toDto(timeOfDay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeOfDayMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeOfDayDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimeOfDay in the database
        List<TimeOfDay> timeOfDayList = timeOfDayRepository.findAll();
        assertThat(timeOfDayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTimeOfDayWithPatch() throws Exception {
        // Initialize the database
        timeOfDayRepository.saveAndFlush(timeOfDay);

        int databaseSizeBeforeUpdate = timeOfDayRepository.findAll().size();

        // Update the timeOfDay using partial update
        TimeOfDay partialUpdatedTimeOfDay = new TimeOfDay();
        partialUpdatedTimeOfDay.setId(timeOfDay.getId());

        restTimeOfDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimeOfDay.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimeOfDay))
            )
            .andExpect(status().isOk());

        // Validate the TimeOfDay in the database
        List<TimeOfDay> timeOfDayList = timeOfDayRepository.findAll();
        assertThat(timeOfDayList).hasSize(databaseSizeBeforeUpdate);
        TimeOfDay testTimeOfDay = timeOfDayList.get(timeOfDayList.size() - 1);
        assertThat(testTimeOfDay.getTime()).isEqualTo(DEFAULT_TIME);
    }

    @Test
    @Transactional
    void fullUpdateTimeOfDayWithPatch() throws Exception {
        // Initialize the database
        timeOfDayRepository.saveAndFlush(timeOfDay);

        int databaseSizeBeforeUpdate = timeOfDayRepository.findAll().size();

        // Update the timeOfDay using partial update
        TimeOfDay partialUpdatedTimeOfDay = new TimeOfDay();
        partialUpdatedTimeOfDay.setId(timeOfDay.getId());

        partialUpdatedTimeOfDay.time(UPDATED_TIME);

        restTimeOfDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimeOfDay.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimeOfDay))
            )
            .andExpect(status().isOk());

        // Validate the TimeOfDay in the database
        List<TimeOfDay> timeOfDayList = timeOfDayRepository.findAll();
        assertThat(timeOfDayList).hasSize(databaseSizeBeforeUpdate);
        TimeOfDay testTimeOfDay = timeOfDayList.get(timeOfDayList.size() - 1);
        assertThat(testTimeOfDay.getTime()).isEqualTo(UPDATED_TIME);
    }

    @Test
    @Transactional
    void patchNonExistingTimeOfDay() throws Exception {
        int databaseSizeBeforeUpdate = timeOfDayRepository.findAll().size();
        timeOfDay.setId(count.incrementAndGet());

        // Create the TimeOfDay
        TimeOfDayDTO timeOfDayDTO = timeOfDayMapper.toDto(timeOfDay);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimeOfDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, timeOfDayDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timeOfDayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeOfDay in the database
        List<TimeOfDay> timeOfDayList = timeOfDayRepository.findAll();
        assertThat(timeOfDayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTimeOfDay() throws Exception {
        int databaseSizeBeforeUpdate = timeOfDayRepository.findAll().size();
        timeOfDay.setId(count.incrementAndGet());

        // Create the TimeOfDay
        TimeOfDayDTO timeOfDayDTO = timeOfDayMapper.toDto(timeOfDay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeOfDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timeOfDayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeOfDay in the database
        List<TimeOfDay> timeOfDayList = timeOfDayRepository.findAll();
        assertThat(timeOfDayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTimeOfDay() throws Exception {
        int databaseSizeBeforeUpdate = timeOfDayRepository.findAll().size();
        timeOfDay.setId(count.incrementAndGet());

        // Create the TimeOfDay
        TimeOfDayDTO timeOfDayDTO = timeOfDayMapper.toDto(timeOfDay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeOfDayMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(timeOfDayDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimeOfDay in the database
        List<TimeOfDay> timeOfDayList = timeOfDayRepository.findAll();
        assertThat(timeOfDayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTimeOfDay() throws Exception {
        // Initialize the database
        timeOfDayRepository.saveAndFlush(timeOfDay);

        int databaseSizeBeforeDelete = timeOfDayRepository.findAll().size();

        // Delete the timeOfDay
        restTimeOfDayMockMvc
            .perform(delete(ENTITY_API_URL_ID, timeOfDay.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TimeOfDay> timeOfDayList = timeOfDayRepository.findAll();
        assertThat(timeOfDayList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
