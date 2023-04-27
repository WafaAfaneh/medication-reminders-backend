package com.almahealth.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.almahealth.app.IntegrationTest;
import com.almahealth.app.domain.Notification;
import com.almahealth.app.domain.Reminder;
import com.almahealth.app.domain.enumeration.Status;
import com.almahealth.app.repository.ReminderRepository;
import com.almahealth.app.service.criteria.ReminderCriteria;
import com.almahealth.app.service.dto.ReminderDTO;
import com.almahealth.app.service.mapper.ReminderMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link ReminderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReminderResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Status DEFAULT_STATUS = Status.TAKEN;
    private static final Status UPDATED_STATUS = Status.SKIPPED;

    private static final String ENTITY_API_URL = "/api/reminders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ReminderMapper reminderMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReminderMockMvc;

    private Reminder reminder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reminder createEntity(EntityManager em) {
        Reminder reminder = new Reminder().date(DEFAULT_DATE).status(DEFAULT_STATUS);
        return reminder;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reminder createUpdatedEntity(EntityManager em) {
        Reminder reminder = new Reminder().date(UPDATED_DATE).status(UPDATED_STATUS);
        return reminder;
    }

    @BeforeEach
    public void initTest() {
        reminder = createEntity(em);
    }

    @Test
    @Transactional
    void createReminder() throws Exception {
        int databaseSizeBeforeCreate = reminderRepository.findAll().size();
        // Create the Reminder
        ReminderDTO reminderDTO = reminderMapper.toDto(reminder);
        restReminderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reminderDTO)))
            .andExpect(status().isCreated());

        // Validate the Reminder in the database
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeCreate + 1);
        Reminder testReminder = reminderList.get(reminderList.size() - 1);
        assertThat(testReminder.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testReminder.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createReminderWithExistingId() throws Exception {
        // Create the Reminder with an existing ID
        reminder.setId(1L);
        ReminderDTO reminderDTO = reminderMapper.toDto(reminder);

        int databaseSizeBeforeCreate = reminderRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReminderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reminderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Reminder in the database
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllReminders() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        // Get all the reminderList
        restReminderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reminder.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getReminder() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        // Get the reminder
        restReminderMockMvc
            .perform(get(ENTITY_API_URL_ID, reminder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reminder.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getRemindersByIdFiltering() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        Long id = reminder.getId();

        defaultReminderShouldBeFound("id.equals=" + id);
        defaultReminderShouldNotBeFound("id.notEquals=" + id);

        defaultReminderShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultReminderShouldNotBeFound("id.greaterThan=" + id);

        defaultReminderShouldBeFound("id.lessThanOrEqual=" + id);
        defaultReminderShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRemindersByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        // Get all the reminderList where date equals to DEFAULT_DATE
        defaultReminderShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the reminderList where date equals to UPDATED_DATE
        defaultReminderShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllRemindersByDateIsInShouldWork() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        // Get all the reminderList where date in DEFAULT_DATE or UPDATED_DATE
        defaultReminderShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the reminderList where date equals to UPDATED_DATE
        defaultReminderShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllRemindersByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        // Get all the reminderList where date is not null
        defaultReminderShouldBeFound("date.specified=true");

        // Get all the reminderList where date is null
        defaultReminderShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllRemindersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        // Get all the reminderList where status equals to DEFAULT_STATUS
        defaultReminderShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the reminderList where status equals to UPDATED_STATUS
        defaultReminderShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllRemindersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        // Get all the reminderList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultReminderShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the reminderList where status equals to UPDATED_STATUS
        defaultReminderShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllRemindersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        // Get all the reminderList where status is not null
        defaultReminderShouldBeFound("status.specified=true");

        // Get all the reminderList where status is null
        defaultReminderShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllRemindersByNotificationIsEqualToSomething() throws Exception {
        Notification notification;
        if (TestUtil.findAll(em, Notification.class).isEmpty()) {
            reminderRepository.saveAndFlush(reminder);
            notification = NotificationResourceIT.createEntity(em);
        } else {
            notification = TestUtil.findAll(em, Notification.class).get(0);
        }
        em.persist(notification);
        em.flush();
        reminder.setNotification(notification);
        reminderRepository.saveAndFlush(reminder);
        Long notificationId = notification.getId();

        // Get all the reminderList where notification equals to notificationId
        defaultReminderShouldBeFound("notificationId.equals=" + notificationId);

        // Get all the reminderList where notification equals to (notificationId + 1)
        defaultReminderShouldNotBeFound("notificationId.equals=" + (notificationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReminderShouldBeFound(String filter) throws Exception {
        restReminderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reminder.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restReminderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReminderShouldNotBeFound(String filter) throws Exception {
        restReminderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReminderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReminder() throws Exception {
        // Get the reminder
        restReminderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReminder() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        int databaseSizeBeforeUpdate = reminderRepository.findAll().size();

        // Update the reminder
        Reminder updatedReminder = reminderRepository.findById(reminder.getId()).get();
        // Disconnect from session so that the updates on updatedReminder are not directly saved in db
        em.detach(updatedReminder);
        updatedReminder.date(UPDATED_DATE).status(UPDATED_STATUS);
        ReminderDTO reminderDTO = reminderMapper.toDto(updatedReminder);

        restReminderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reminderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reminderDTO))
            )
            .andExpect(status().isOk());

        // Validate the Reminder in the database
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeUpdate);
        Reminder testReminder = reminderList.get(reminderList.size() - 1);
        assertThat(testReminder.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testReminder.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingReminder() throws Exception {
        int databaseSizeBeforeUpdate = reminderRepository.findAll().size();
        reminder.setId(count.incrementAndGet());

        // Create the Reminder
        ReminderDTO reminderDTO = reminderMapper.toDto(reminder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReminderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reminderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reminderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reminder in the database
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReminder() throws Exception {
        int databaseSizeBeforeUpdate = reminderRepository.findAll().size();
        reminder.setId(count.incrementAndGet());

        // Create the Reminder
        ReminderDTO reminderDTO = reminderMapper.toDto(reminder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReminderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reminderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reminder in the database
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReminder() throws Exception {
        int databaseSizeBeforeUpdate = reminderRepository.findAll().size();
        reminder.setId(count.incrementAndGet());

        // Create the Reminder
        ReminderDTO reminderDTO = reminderMapper.toDto(reminder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReminderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reminderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reminder in the database
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReminderWithPatch() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        int databaseSizeBeforeUpdate = reminderRepository.findAll().size();

        // Update the reminder using partial update
        Reminder partialUpdatedReminder = new Reminder();
        partialUpdatedReminder.setId(reminder.getId());

        partialUpdatedReminder.status(UPDATED_STATUS);

        restReminderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReminder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReminder))
            )
            .andExpect(status().isOk());

        // Validate the Reminder in the database
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeUpdate);
        Reminder testReminder = reminderList.get(reminderList.size() - 1);
        assertThat(testReminder.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testReminder.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateReminderWithPatch() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        int databaseSizeBeforeUpdate = reminderRepository.findAll().size();

        // Update the reminder using partial update
        Reminder partialUpdatedReminder = new Reminder();
        partialUpdatedReminder.setId(reminder.getId());

        partialUpdatedReminder.date(UPDATED_DATE).status(UPDATED_STATUS);

        restReminderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReminder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReminder))
            )
            .andExpect(status().isOk());

        // Validate the Reminder in the database
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeUpdate);
        Reminder testReminder = reminderList.get(reminderList.size() - 1);
        assertThat(testReminder.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testReminder.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingReminder() throws Exception {
        int databaseSizeBeforeUpdate = reminderRepository.findAll().size();
        reminder.setId(count.incrementAndGet());

        // Create the Reminder
        ReminderDTO reminderDTO = reminderMapper.toDto(reminder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReminderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reminderDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reminderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reminder in the database
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReminder() throws Exception {
        int databaseSizeBeforeUpdate = reminderRepository.findAll().size();
        reminder.setId(count.incrementAndGet());

        // Create the Reminder
        ReminderDTO reminderDTO = reminderMapper.toDto(reminder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReminderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reminderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reminder in the database
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReminder() throws Exception {
        int databaseSizeBeforeUpdate = reminderRepository.findAll().size();
        reminder.setId(count.incrementAndGet());

        // Create the Reminder
        ReminderDTO reminderDTO = reminderMapper.toDto(reminder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReminderMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(reminderDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reminder in the database
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReminder() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        int databaseSizeBeforeDelete = reminderRepository.findAll().size();

        // Delete the reminder
        restReminderMockMvc
            .perform(delete(ENTITY_API_URL_ID, reminder.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
