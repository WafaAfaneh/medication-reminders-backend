package com.almahealth.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.almahealth.app.IntegrationTest;
import com.almahealth.app.domain.Medication;
import com.almahealth.app.domain.Notification;
import com.almahealth.app.domain.User;
import com.almahealth.app.domain.enumeration.MedicationType;
import com.almahealth.app.repository.MedicationRepository;
import com.almahealth.app.service.criteria.MedicationCriteria;
import com.almahealth.app.service.dto.MedicationDTO;
import com.almahealth.app.service.mapper.MedicationMapper;
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
 * Integration tests for the {@link MedicationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MedicationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_DOSAGE_QUANTITY = 1D;
    private static final Double UPDATED_DOSAGE_QUANTITY = 2D;
    private static final Double SMALLER_DOSAGE_QUANTITY = 1D - 1D;

    private static final MedicationType DEFAULT_TYPE = MedicationType.CAPSULE;
    private static final MedicationType UPDATED_TYPE = MedicationType.TABLET;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/medications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private MedicationMapper medicationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMedicationMockMvc;

    private Medication medication;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Medication createEntity(EntityManager em) {
        Medication medication = new Medication()
            .name(DEFAULT_NAME)
            .dosageQuantity(DEFAULT_DOSAGE_QUANTITY)
            .type(DEFAULT_TYPE)
            .active(DEFAULT_ACTIVE);
        return medication;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Medication createUpdatedEntity(EntityManager em) {
        Medication medication = new Medication()
            .name(UPDATED_NAME)
            .dosageQuantity(UPDATED_DOSAGE_QUANTITY)
            .type(UPDATED_TYPE)
            .active(UPDATED_ACTIVE);
        return medication;
    }

    @BeforeEach
    public void initTest() {
        medication = createEntity(em);
    }

    @Test
    @Transactional
    void createMedication() throws Exception {
        int databaseSizeBeforeCreate = medicationRepository.findAll().size();
        // Create the Medication
        MedicationDTO medicationDTO = medicationMapper.toDto(medication);
        restMedicationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(medicationDTO)))
            .andExpect(status().isCreated());

        // Validate the Medication in the database
        List<Medication> medicationList = medicationRepository.findAll();
        assertThat(medicationList).hasSize(databaseSizeBeforeCreate + 1);
        Medication testMedication = medicationList.get(medicationList.size() - 1);
        assertThat(testMedication.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMedication.getDosageQuantity()).isEqualTo(DEFAULT_DOSAGE_QUANTITY);
        assertThat(testMedication.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testMedication.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createMedicationWithExistingId() throws Exception {
        // Create the Medication with an existing ID
        medication.setId(1L);
        MedicationDTO medicationDTO = medicationMapper.toDto(medication);

        int databaseSizeBeforeCreate = medicationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMedicationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(medicationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Medication in the database
        List<Medication> medicationList = medicationRepository.findAll();
        assertThat(medicationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMedications() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList
        restMedicationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(medication.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].dosageQuantity").value(hasItem(DEFAULT_DOSAGE_QUANTITY.doubleValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getMedication() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get the medication
        restMedicationMockMvc
            .perform(get(ENTITY_API_URL_ID, medication.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(medication.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.dosageQuantity").value(DEFAULT_DOSAGE_QUANTITY.doubleValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getMedicationsByIdFiltering() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        Long id = medication.getId();

        defaultMedicationShouldBeFound("id.equals=" + id);
        defaultMedicationShouldNotBeFound("id.notEquals=" + id);

        defaultMedicationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultMedicationShouldNotBeFound("id.greaterThan=" + id);

        defaultMedicationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultMedicationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMedicationsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where name equals to DEFAULT_NAME
        defaultMedicationShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the medicationList where name equals to UPDATED_NAME
        defaultMedicationShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMedicationsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where name in DEFAULT_NAME or UPDATED_NAME
        defaultMedicationShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the medicationList where name equals to UPDATED_NAME
        defaultMedicationShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMedicationsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where name is not null
        defaultMedicationShouldBeFound("name.specified=true");

        // Get all the medicationList where name is null
        defaultMedicationShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllMedicationsByNameContainsSomething() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where name contains DEFAULT_NAME
        defaultMedicationShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the medicationList where name contains UPDATED_NAME
        defaultMedicationShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMedicationsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where name does not contain DEFAULT_NAME
        defaultMedicationShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the medicationList where name does not contain UPDATED_NAME
        defaultMedicationShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMedicationsByDosageQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where dosageQuantity equals to DEFAULT_DOSAGE_QUANTITY
        defaultMedicationShouldBeFound("dosageQuantity.equals=" + DEFAULT_DOSAGE_QUANTITY);

        // Get all the medicationList where dosageQuantity equals to UPDATED_DOSAGE_QUANTITY
        defaultMedicationShouldNotBeFound("dosageQuantity.equals=" + UPDATED_DOSAGE_QUANTITY);
    }

    @Test
    @Transactional
    void getAllMedicationsByDosageQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where dosageQuantity in DEFAULT_DOSAGE_QUANTITY or UPDATED_DOSAGE_QUANTITY
        defaultMedicationShouldBeFound("dosageQuantity.in=" + DEFAULT_DOSAGE_QUANTITY + "," + UPDATED_DOSAGE_QUANTITY);

        // Get all the medicationList where dosageQuantity equals to UPDATED_DOSAGE_QUANTITY
        defaultMedicationShouldNotBeFound("dosageQuantity.in=" + UPDATED_DOSAGE_QUANTITY);
    }

    @Test
    @Transactional
    void getAllMedicationsByDosageQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where dosageQuantity is not null
        defaultMedicationShouldBeFound("dosageQuantity.specified=true");

        // Get all the medicationList where dosageQuantity is null
        defaultMedicationShouldNotBeFound("dosageQuantity.specified=false");
    }

    @Test
    @Transactional
    void getAllMedicationsByDosageQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where dosageQuantity is greater than or equal to DEFAULT_DOSAGE_QUANTITY
        defaultMedicationShouldBeFound("dosageQuantity.greaterThanOrEqual=" + DEFAULT_DOSAGE_QUANTITY);

        // Get all the medicationList where dosageQuantity is greater than or equal to UPDATED_DOSAGE_QUANTITY
        defaultMedicationShouldNotBeFound("dosageQuantity.greaterThanOrEqual=" + UPDATED_DOSAGE_QUANTITY);
    }

    @Test
    @Transactional
    void getAllMedicationsByDosageQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where dosageQuantity is less than or equal to DEFAULT_DOSAGE_QUANTITY
        defaultMedicationShouldBeFound("dosageQuantity.lessThanOrEqual=" + DEFAULT_DOSAGE_QUANTITY);

        // Get all the medicationList where dosageQuantity is less than or equal to SMALLER_DOSAGE_QUANTITY
        defaultMedicationShouldNotBeFound("dosageQuantity.lessThanOrEqual=" + SMALLER_DOSAGE_QUANTITY);
    }

    @Test
    @Transactional
    void getAllMedicationsByDosageQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where dosageQuantity is less than DEFAULT_DOSAGE_QUANTITY
        defaultMedicationShouldNotBeFound("dosageQuantity.lessThan=" + DEFAULT_DOSAGE_QUANTITY);

        // Get all the medicationList where dosageQuantity is less than UPDATED_DOSAGE_QUANTITY
        defaultMedicationShouldBeFound("dosageQuantity.lessThan=" + UPDATED_DOSAGE_QUANTITY);
    }

    @Test
    @Transactional
    void getAllMedicationsByDosageQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where dosageQuantity is greater than DEFAULT_DOSAGE_QUANTITY
        defaultMedicationShouldNotBeFound("dosageQuantity.greaterThan=" + DEFAULT_DOSAGE_QUANTITY);

        // Get all the medicationList where dosageQuantity is greater than SMALLER_DOSAGE_QUANTITY
        defaultMedicationShouldBeFound("dosageQuantity.greaterThan=" + SMALLER_DOSAGE_QUANTITY);
    }

    @Test
    @Transactional
    void getAllMedicationsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where type equals to DEFAULT_TYPE
        defaultMedicationShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the medicationList where type equals to UPDATED_TYPE
        defaultMedicationShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllMedicationsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultMedicationShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the medicationList where type equals to UPDATED_TYPE
        defaultMedicationShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllMedicationsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where type is not null
        defaultMedicationShouldBeFound("type.specified=true");

        // Get all the medicationList where type is null
        defaultMedicationShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllMedicationsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where active equals to DEFAULT_ACTIVE
        defaultMedicationShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the medicationList where active equals to UPDATED_ACTIVE
        defaultMedicationShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMedicationsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultMedicationShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the medicationList where active equals to UPDATED_ACTIVE
        defaultMedicationShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMedicationsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        // Get all the medicationList where active is not null
        defaultMedicationShouldBeFound("active.specified=true");

        // Get all the medicationList where active is null
        defaultMedicationShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllMedicationsByNotificationIsEqualToSomething() throws Exception {
        Notification notification;
        if (TestUtil.findAll(em, Notification.class).isEmpty()) {
            medicationRepository.saveAndFlush(medication);
            notification = NotificationResourceIT.createEntity(em);
        } else {
            notification = TestUtil.findAll(em, Notification.class).get(0);
        }
        em.persist(notification);
        em.flush();
        medication.setNotification(notification);
        medicationRepository.saveAndFlush(medication);
        Long notificationId = notification.getId();

        // Get all the medicationList where notification equals to notificationId
        defaultMedicationShouldBeFound("notificationId.equals=" + notificationId);

        // Get all the medicationList where notification equals to (notificationId + 1)
        defaultMedicationShouldNotBeFound("notificationId.equals=" + (notificationId + 1));
    }

    @Test
    @Transactional
    void getAllMedicationsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            medicationRepository.saveAndFlush(medication);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        medication.setUser(user);
        medicationRepository.saveAndFlush(medication);
        Long userId = user.getId();

        // Get all the medicationList where user equals to userId
        defaultMedicationShouldBeFound("userId.equals=" + userId);

        // Get all the medicationList where user equals to (userId + 1)
        defaultMedicationShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMedicationShouldBeFound(String filter) throws Exception {
        restMedicationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(medication.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].dosageQuantity").value(hasItem(DEFAULT_DOSAGE_QUANTITY.doubleValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restMedicationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMedicationShouldNotBeFound(String filter) throws Exception {
        restMedicationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMedicationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMedication() throws Exception {
        // Get the medication
        restMedicationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMedication() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        int databaseSizeBeforeUpdate = medicationRepository.findAll().size();

        // Update the medication
        Medication updatedMedication = medicationRepository.findById(medication.getId()).get();
        // Disconnect from session so that the updates on updatedMedication are not directly saved in db
        em.detach(updatedMedication);
        updatedMedication.name(UPDATED_NAME).dosageQuantity(UPDATED_DOSAGE_QUANTITY).type(UPDATED_TYPE).active(UPDATED_ACTIVE);
        MedicationDTO medicationDTO = medicationMapper.toDto(updatedMedication);

        restMedicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, medicationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(medicationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Medication in the database
        List<Medication> medicationList = medicationRepository.findAll();
        assertThat(medicationList).hasSize(databaseSizeBeforeUpdate);
        Medication testMedication = medicationList.get(medicationList.size() - 1);
        assertThat(testMedication.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMedication.getDosageQuantity()).isEqualTo(UPDATED_DOSAGE_QUANTITY);
        assertThat(testMedication.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testMedication.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingMedication() throws Exception {
        int databaseSizeBeforeUpdate = medicationRepository.findAll().size();
        medication.setId(count.incrementAndGet());

        // Create the Medication
        MedicationDTO medicationDTO = medicationMapper.toDto(medication);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMedicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, medicationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(medicationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Medication in the database
        List<Medication> medicationList = medicationRepository.findAll();
        assertThat(medicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMedication() throws Exception {
        int databaseSizeBeforeUpdate = medicationRepository.findAll().size();
        medication.setId(count.incrementAndGet());

        // Create the Medication
        MedicationDTO medicationDTO = medicationMapper.toDto(medication);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMedicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(medicationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Medication in the database
        List<Medication> medicationList = medicationRepository.findAll();
        assertThat(medicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMedication() throws Exception {
        int databaseSizeBeforeUpdate = medicationRepository.findAll().size();
        medication.setId(count.incrementAndGet());

        // Create the Medication
        MedicationDTO medicationDTO = medicationMapper.toDto(medication);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMedicationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(medicationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Medication in the database
        List<Medication> medicationList = medicationRepository.findAll();
        assertThat(medicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMedicationWithPatch() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        int databaseSizeBeforeUpdate = medicationRepository.findAll().size();

        // Update the medication using partial update
        Medication partialUpdatedMedication = new Medication();
        partialUpdatedMedication.setId(medication.getId());

        partialUpdatedMedication.name(UPDATED_NAME).type(UPDATED_TYPE).active(UPDATED_ACTIVE);

        restMedicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMedication.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMedication))
            )
            .andExpect(status().isOk());

        // Validate the Medication in the database
        List<Medication> medicationList = medicationRepository.findAll();
        assertThat(medicationList).hasSize(databaseSizeBeforeUpdate);
        Medication testMedication = medicationList.get(medicationList.size() - 1);
        assertThat(testMedication.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMedication.getDosageQuantity()).isEqualTo(DEFAULT_DOSAGE_QUANTITY);
        assertThat(testMedication.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testMedication.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateMedicationWithPatch() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        int databaseSizeBeforeUpdate = medicationRepository.findAll().size();

        // Update the medication using partial update
        Medication partialUpdatedMedication = new Medication();
        partialUpdatedMedication.setId(medication.getId());

        partialUpdatedMedication.name(UPDATED_NAME).dosageQuantity(UPDATED_DOSAGE_QUANTITY).type(UPDATED_TYPE).active(UPDATED_ACTIVE);

        restMedicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMedication.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMedication))
            )
            .andExpect(status().isOk());

        // Validate the Medication in the database
        List<Medication> medicationList = medicationRepository.findAll();
        assertThat(medicationList).hasSize(databaseSizeBeforeUpdate);
        Medication testMedication = medicationList.get(medicationList.size() - 1);
        assertThat(testMedication.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMedication.getDosageQuantity()).isEqualTo(UPDATED_DOSAGE_QUANTITY);
        assertThat(testMedication.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testMedication.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingMedication() throws Exception {
        int databaseSizeBeforeUpdate = medicationRepository.findAll().size();
        medication.setId(count.incrementAndGet());

        // Create the Medication
        MedicationDTO medicationDTO = medicationMapper.toDto(medication);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMedicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, medicationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(medicationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Medication in the database
        List<Medication> medicationList = medicationRepository.findAll();
        assertThat(medicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMedication() throws Exception {
        int databaseSizeBeforeUpdate = medicationRepository.findAll().size();
        medication.setId(count.incrementAndGet());

        // Create the Medication
        MedicationDTO medicationDTO = medicationMapper.toDto(medication);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMedicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(medicationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Medication in the database
        List<Medication> medicationList = medicationRepository.findAll();
        assertThat(medicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMedication() throws Exception {
        int databaseSizeBeforeUpdate = medicationRepository.findAll().size();
        medication.setId(count.incrementAndGet());

        // Create the Medication
        MedicationDTO medicationDTO = medicationMapper.toDto(medication);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMedicationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(medicationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Medication in the database
        List<Medication> medicationList = medicationRepository.findAll();
        assertThat(medicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMedication() throws Exception {
        // Initialize the database
        medicationRepository.saveAndFlush(medication);

        int databaseSizeBeforeDelete = medicationRepository.findAll().size();

        // Delete the medication
        restMedicationMockMvc
            .perform(delete(ENTITY_API_URL_ID, medication.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Medication> medicationList = medicationRepository.findAll();
        assertThat(medicationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
