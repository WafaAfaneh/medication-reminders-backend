package com.almahealth.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.almahealth.app.IntegrationTest;
import com.almahealth.app.domain.Frequency;
import com.almahealth.app.domain.Medication;
import com.almahealth.app.domain.Notification;
import com.almahealth.app.domain.Reminder;
import com.almahealth.app.domain.TimeOfDay;
import com.almahealth.app.repository.NotificationRepository;
import com.almahealth.app.service.criteria.NotificationCriteria;
import com.almahealth.app.service.dto.NotificationDTO;
import com.almahealth.app.service.mapper.NotificationMapper;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link NotificationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotificationResourceIT {

    private static final String DEFAULT_DISPLAY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DISPLAY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationMockMvc;

    private Notification notification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createEntity(EntityManager em) {
        Notification notification = new Notification().displayName(DEFAULT_DISPLAY_NAME).notes(DEFAULT_NOTES).startDate(DEFAULT_START_DATE);
        return notification;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createUpdatedEntity(EntityManager em) {
        Notification notification = new Notification().displayName(UPDATED_DISPLAY_NAME).notes(UPDATED_NOTES).startDate(UPDATED_START_DATE);
        return notification;
    }

    @BeforeEach
    public void initTest() {
        notification = createEntity(em);
    }

    @Test
    @Transactional
    void createNotification() throws Exception {
        int databaseSizeBeforeCreate = notificationRepository.findAll().size();
        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);
        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate + 1);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDisplayName()).isEqualTo(DEFAULT_DISPLAY_NAME);
        assertThat(testNotification.getNotes()).isEqualTo(DEFAULT_NOTES);
        assertThat(testNotification.getStartDate()).isEqualTo(DEFAULT_START_DATE);
    }

    @Test
    @Transactional
    void createNotificationWithExistingId() throws Exception {
        // Create the Notification with an existing ID
        notification.setId(1L);
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        int databaseSizeBeforeCreate = notificationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllNotifications() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].displayName").value(hasItem(DEFAULT_DISPLAY_NAME)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())));
    }

    @Test
    @Transactional
    void getNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get the notification
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL_ID, notification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notification.getId().intValue()))
            .andExpect(jsonPath("$.displayName").value(DEFAULT_DISPLAY_NAME))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()));
    }

    @Test
    @Transactional
    void getNotificationsByIdFiltering() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        Long id = notification.getId();

        defaultNotificationShouldBeFound("id.equals=" + id);
        defaultNotificationShouldNotBeFound("id.notEquals=" + id);

        defaultNotificationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultNotificationShouldNotBeFound("id.greaterThan=" + id);

        defaultNotificationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultNotificationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllNotificationsByDisplayNameIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where displayName equals to DEFAULT_DISPLAY_NAME
        defaultNotificationShouldBeFound("displayName.equals=" + DEFAULT_DISPLAY_NAME);

        // Get all the notificationList where displayName equals to UPDATED_DISPLAY_NAME
        defaultNotificationShouldNotBeFound("displayName.equals=" + UPDATED_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void getAllNotificationsByDisplayNameIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where displayName in DEFAULT_DISPLAY_NAME or UPDATED_DISPLAY_NAME
        defaultNotificationShouldBeFound("displayName.in=" + DEFAULT_DISPLAY_NAME + "," + UPDATED_DISPLAY_NAME);

        // Get all the notificationList where displayName equals to UPDATED_DISPLAY_NAME
        defaultNotificationShouldNotBeFound("displayName.in=" + UPDATED_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void getAllNotificationsByDisplayNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where displayName is not null
        defaultNotificationShouldBeFound("displayName.specified=true");

        // Get all the notificationList where displayName is null
        defaultNotificationShouldNotBeFound("displayName.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByDisplayNameContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where displayName contains DEFAULT_DISPLAY_NAME
        defaultNotificationShouldBeFound("displayName.contains=" + DEFAULT_DISPLAY_NAME);

        // Get all the notificationList where displayName contains UPDATED_DISPLAY_NAME
        defaultNotificationShouldNotBeFound("displayName.contains=" + UPDATED_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void getAllNotificationsByDisplayNameNotContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where displayName does not contain DEFAULT_DISPLAY_NAME
        defaultNotificationShouldNotBeFound("displayName.doesNotContain=" + DEFAULT_DISPLAY_NAME);

        // Get all the notificationList where displayName does not contain UPDATED_DISPLAY_NAME
        defaultNotificationShouldBeFound("displayName.doesNotContain=" + UPDATED_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notes equals to DEFAULT_NOTES
        defaultNotificationShouldBeFound("notes.equals=" + DEFAULT_NOTES);

        // Get all the notificationList where notes equals to UPDATED_NOTES
        defaultNotificationShouldNotBeFound("notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notes in DEFAULT_NOTES or UPDATED_NOTES
        defaultNotificationShouldBeFound("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES);

        // Get all the notificationList where notes equals to UPDATED_NOTES
        defaultNotificationShouldNotBeFound("notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notes is not null
        defaultNotificationShouldBeFound("notes.specified=true");

        // Get all the notificationList where notes is null
        defaultNotificationShouldNotBeFound("notes.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByNotesContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notes contains DEFAULT_NOTES
        defaultNotificationShouldBeFound("notes.contains=" + DEFAULT_NOTES);

        // Get all the notificationList where notes contains UPDATED_NOTES
        defaultNotificationShouldNotBeFound("notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notes does not contain DEFAULT_NOTES
        defaultNotificationShouldNotBeFound("notes.doesNotContain=" + DEFAULT_NOTES);

        // Get all the notificationList where notes does not contain UPDATED_NOTES
        defaultNotificationShouldBeFound("notes.doesNotContain=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllNotificationsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where startDate equals to DEFAULT_START_DATE
        defaultNotificationShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the notificationList where startDate equals to UPDATED_START_DATE
        defaultNotificationShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultNotificationShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the notificationList where startDate equals to UPDATED_START_DATE
        defaultNotificationShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where startDate is not null
        defaultNotificationShouldBeFound("startDate.specified=true");

        // Get all the notificationList where startDate is null
        defaultNotificationShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where startDate is greater than or equal to DEFAULT_START_DATE
        defaultNotificationShouldBeFound("startDate.greaterThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the notificationList where startDate is greater than or equal to UPDATED_START_DATE
        defaultNotificationShouldNotBeFound("startDate.greaterThanOrEqual=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByStartDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where startDate is less than or equal to DEFAULT_START_DATE
        defaultNotificationShouldBeFound("startDate.lessThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the notificationList where startDate is less than or equal to SMALLER_START_DATE
        defaultNotificationShouldNotBeFound("startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where startDate is less than DEFAULT_START_DATE
        defaultNotificationShouldNotBeFound("startDate.lessThan=" + DEFAULT_START_DATE);

        // Get all the notificationList where startDate is less than UPDATED_START_DATE
        defaultNotificationShouldBeFound("startDate.lessThan=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByStartDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where startDate is greater than DEFAULT_START_DATE
        defaultNotificationShouldNotBeFound("startDate.greaterThan=" + DEFAULT_START_DATE);

        // Get all the notificationList where startDate is greater than SMALLER_START_DATE
        defaultNotificationShouldBeFound("startDate.greaterThan=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByFrequencyIsEqualToSomething() throws Exception {
        Frequency frequency;
        if (TestUtil.findAll(em, Frequency.class).isEmpty()) {
            notificationRepository.saveAndFlush(notification);
            frequency = FrequencyResourceIT.createEntity(em);
        } else {
            frequency = TestUtil.findAll(em, Frequency.class).get(0);
        }
        em.persist(frequency);
        em.flush();
        notification.setFrequency(frequency);
        notificationRepository.saveAndFlush(notification);
        Long frequencyId = frequency.getId();

        // Get all the notificationList where frequency equals to frequencyId
        defaultNotificationShouldBeFound("frequencyId.equals=" + frequencyId);

        // Get all the notificationList where frequency equals to (frequencyId + 1)
        defaultNotificationShouldNotBeFound("frequencyId.equals=" + (frequencyId + 1));
    }

    @Test
    @Transactional
    void getAllNotificationsByReminderIsEqualToSomething() throws Exception {
        Reminder reminder;
        if (TestUtil.findAll(em, Reminder.class).isEmpty()) {
            notificationRepository.saveAndFlush(notification);
            reminder = ReminderResourceIT.createEntity(em);
        } else {
            reminder = TestUtil.findAll(em, Reminder.class).get(0);
        }
        em.persist(reminder);
        em.flush();
        notification.addReminder(reminder);
        notificationRepository.saveAndFlush(notification);
        Long reminderId = reminder.getId();

        // Get all the notificationList where reminder equals to reminderId
        defaultNotificationShouldBeFound("reminderId.equals=" + reminderId);

        // Get all the notificationList where reminder equals to (reminderId + 1)
        defaultNotificationShouldNotBeFound("reminderId.equals=" + (reminderId + 1));
    }

    @Test
    @Transactional
    void getAllNotificationsByTimeOfDayIsEqualToSomething() throws Exception {
        TimeOfDay timeOfDay;
        if (TestUtil.findAll(em, TimeOfDay.class).isEmpty()) {
            notificationRepository.saveAndFlush(notification);
            timeOfDay = TimeOfDayResourceIT.createEntity(em);
        } else {
            timeOfDay = TestUtil.findAll(em, TimeOfDay.class).get(0);
        }
        em.persist(timeOfDay);
        em.flush();
        notification.addTimeOfDay(timeOfDay);
        notificationRepository.saveAndFlush(notification);
        Long timeOfDayId = timeOfDay.getId();

        // Get all the notificationList where timeOfDay equals to timeOfDayId
        defaultNotificationShouldBeFound("timeOfDayId.equals=" + timeOfDayId);

        // Get all the notificationList where timeOfDay equals to (timeOfDayId + 1)
        defaultNotificationShouldNotBeFound("timeOfDayId.equals=" + (timeOfDayId + 1));
    }

    @Test
    @Transactional
    void getAllNotificationsByMedicationIsEqualToSomething() throws Exception {
        Medication medication;
        if (TestUtil.findAll(em, Medication.class).isEmpty()) {
            notificationRepository.saveAndFlush(notification);
            medication = MedicationResourceIT.createEntity(em);
        } else {
            medication = TestUtil.findAll(em, Medication.class).get(0);
        }
        em.persist(medication);
        em.flush();
        notification.setMedication(medication);
        medication.setNotification(notification);
        notificationRepository.saveAndFlush(notification);
        Long medicationId = medication.getId();

        // Get all the notificationList where medication equals to medicationId
        defaultNotificationShouldBeFound("medicationId.equals=" + medicationId);

        // Get all the notificationList where medication equals to (medicationId + 1)
        defaultNotificationShouldNotBeFound("medicationId.equals=" + (medicationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificationShouldBeFound(String filter) throws Exception {
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].displayName").value(hasItem(DEFAULT_DISPLAY_NAME)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())));

        // Check, that the count call also returns 1
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNotificationShouldNotBeFound(String filter) throws Exception {
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingNotification() throws Exception {
        // Get the notification
        restNotificationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();

        // Update the notification
        Notification updatedNotification = notificationRepository.findById(notification.getId()).get();
        // Disconnect from session so that the updates on updatedNotification are not directly saved in db
        em.detach(updatedNotification);
        updatedNotification.displayName(UPDATED_DISPLAY_NAME).notes(UPDATED_NOTES).startDate(UPDATED_START_DATE);
        NotificationDTO notificationDTO = notificationMapper.toDto(updatedNotification);

        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDisplayName()).isEqualTo(UPDATED_DISPLAY_NAME);
        assertThat(testNotification.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testNotification.getStartDate()).isEqualTo(UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void putNonExistingNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification.startDate(UPDATED_START_DATE);

        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotification))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDisplayName()).isEqualTo(DEFAULT_DISPLAY_NAME);
        assertThat(testNotification.getNotes()).isEqualTo(DEFAULT_NOTES);
        assertThat(testNotification.getStartDate()).isEqualTo(UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void fullUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification.displayName(UPDATED_DISPLAY_NAME).notes(UPDATED_NOTES).startDate(UPDATED_START_DATE);

        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotification))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDisplayName()).isEqualTo(UPDATED_DISPLAY_NAME);
        assertThat(testNotification.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testNotification.getStartDate()).isEqualTo(UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        int databaseSizeBeforeDelete = notificationRepository.findAll().size();

        // Delete the notification
        restNotificationMockMvc
            .perform(delete(ENTITY_API_URL_ID, notification.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
