package com.almahealth.app.service;

import com.almahealth.app.domain.*;
import com.almahealth.app.domain.enumeration.FrequencyType;
import com.almahealth.app.domain.enumeration.Status;
import com.almahealth.app.repository.MedicationRepository;
import com.almahealth.app.repository.ReminderRepository;
import com.almahealth.app.service.dto.FCMMessageDTO;
import com.almahealth.app.service.dto.MedicationDTO;
import com.almahealth.app.service.dto.MedicationRemindersDTO;
import com.almahealth.app.service.mapper.MedicationMapper;
import com.almahealth.app.service.util.SystemCache;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Medication}.
 */
@Service
@Transactional
public class MedicationService {

    private final Logger log = LoggerFactory.getLogger(MedicationService.class);

    private final MedicationRepository medicationRepository;

    private final MedicationMapper medicationMapper;

    private final ReminderRepository reminderRepository;

    private final SimpMessageSendingOperations messagingTemplate;

    private final FcmClient fcmClient;

    private final SystemCache systemCache;

    @PersistenceContext
    private EntityManager entityManager;

    private List<Medication> medicationsList;

    private static final String FCM_TOKEN =
        "c3oSxPJfSu-795ik7CZg8h:APA91bHuIUYhv7KRIlt3Glvey53cY_vJVnwD8ZuGUjDT1zliXZ9Oz8NSQudxvLs4mqROiaeRVJc0B06muZBSTAA10DhNEhJjwcbPBcxPPq1pDbdllVWKwipRKaZ-yFhaoDK7Q2WY8Pih";

    public MedicationService(
        MedicationRepository medicationRepository,
        MedicationMapper medicationMapper,
        ReminderRepository reminderRepository,
        SimpMessageSendingOperations messagingTemplate,
        FcmClient fcmClient,
        @Lazy SystemCache systemCache
    ) {
        this.medicationRepository = medicationRepository;
        this.medicationMapper = medicationMapper;
        this.reminderRepository = reminderRepository;
        this.messagingTemplate = messagingTemplate;
        this.fcmClient = fcmClient;
        this.systemCache = systemCache;
    }

    /**
     * Save a medication.
     *
     * @param medicationDTO the entity to save.
     * @return the persisted entity.
     */
    public MedicationDTO save(MedicationDTO medicationDTO) {
        log.debug("Request to save Medication : {}", medicationDTO);
        Medication medication = medicationMapper.toEntity(medicationDTO);
        handleTodayNewlyMedicationReminder(medication);
        medication = medicationRepository.save(medication);
        return medicationMapper.toDto(medication);
    }

    /**
     * Update a medication.
     *
     * @param medicationDTO the entity to save.
     * @return the persisted entity.
     */
    public MedicationDTO update(MedicationDTO medicationDTO) {
        log.debug("Request to update Medication : {}", medicationDTO);
        Medication medication = medicationMapper.toEntity(medicationDTO);
        medication = medicationRepository.save(medication);
        return medicationMapper.toDto(medication);
    }

    /**
     * Partially update a medication.
     *
     * @param medicationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MedicationDTO> partialUpdate(MedicationDTO medicationDTO) {
        log.debug("Request to partially update Medication : {}", medicationDTO);

        return medicationRepository
            .findById(medicationDTO.getId())
            .map(existingMedication -> {
                medicationMapper.partialUpdate(existingMedication, medicationDTO);
                updateMedicationReminders(existingMedication.getNotification());
                handleTodayNewlyMedicationReminder(existingMedication);
                return existingMedication;
            })
            .map(medicationRepository::save)
            .map(medicationMapper::toDto);
    }

    /**
     * Get all the medications.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MedicationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Medications");
        return medicationRepository.findAll(pageable).map(medicationMapper::toDto);
    }

    /**
     * Get one medication by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MedicationDTO> findOne(Long id) {
        log.debug("Request to get Medication : {}", id);
        return medicationRepository.findById(id).map(medicationMapper::toDto);
    }

    /**
     * Delete the medication by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Medication : {}", id);
        medicationRepository.findById(id).map(medication -> medication.active(false)).map(medicationRepository::save);
    }

    public void createDayNotificationsReminders() {
        buildDayMedicationsQuery();
        createRemindersForDayMedications();
        medicationsList = medicationRepository.saveAll(medicationsList);
    }

    public List<MedicationRemindersDTO> findAllDayMedicationReminders() {
        if (medicationsList == null) {
            buildDayMedicationsQuery();
        }
        return medicationsList.stream().map(medicationMapper::toMedicationReminders).collect(Collectors.toList());
    }

    private void buildDayMedicationsQuery() {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Medication> query = cb.createQuery(Medication.class);
        Root<Medication> root = query.from(Medication.class);
        Join<Medication, Notification> medicationNotificationJoin = root.join(Medication_.notification);
        Join<Notification, Frequency> notificationFrequencyJoin = medicationNotificationJoin.join(Notification_.FREQUENCY);

        Predicate active = cb.equal(root.get(Medication_.ACTIVE), true);
        Predicate startDate = cb.lessThanOrEqualTo(medicationNotificationJoin.get(Notification_.startDate), LocalDate.now());
        Predicate dailyType = cb.equal(notificationFrequencyJoin.get(Frequency_.type), FrequencyType.DAILY);
        Predicate weeklyType = cb.and(
            cb.equal(notificationFrequencyJoin.get(Frequency_.type), FrequencyType.WEEKLY),
            cb.equal(notificationFrequencyJoin.get(dayOfWeek.toString().toLowerCase()), true)
        );

        query.select(root).where(cb.and(active, startDate, cb.or(dailyType, weeklyType)));

        medicationsList = entityManager.createQuery(query).getResultList();
    }

    private void createRemindersForDayMedications() {
        medicationsList.forEach(medication -> initializeMedicationReminders(medication.getNotification()));
    }

    private void updateMedicationReminders(Notification notification) {
        Instant date = LocalDateTime.now().with(LocalTime.MIDNIGHT).toInstant(OffsetDateTime.now().getOffset());

        reminderRepository.deleteAllByNotificationIdAndDateGreaterThanEqual(notification.getId(), date);
    }

    public void sendDayNotificationsReminders() {
        List<String> tokens = List.of(FCM_TOKEN);
        tokens.forEach(token -> {
            //get user from token (FcmDevice fcmDevice = fcmDevice.findByFcmToken(token))
            // instead of 1l fcmDevice.getUserId();
            List<FCMMessageDTO> fcmMessageDTOs = systemCache.getMedicationRemindersMap().get(1L) != null
                ? systemCache
                    .getMedicationRemindersMap()
                    .get(1L)
                    .stream()
                    .map(this::getFCMMessageForReminder)
                    .flatMap(List::stream)
                    .collect(Collectors.toList())
                : new ArrayList<>();

            fcmClient.sendNotificationDirect(token, fcmMessageDTOs);
        });
        //        systemCache.getMedicationRemindersMap().forEach((key, value) ->
        //            messagingTemplate.convertAndSend("/topic/reminders/" + key, value));
    }

    private List<FCMMessageDTO> getFCMMessageForReminder(MedicationRemindersDTO medicationRemindersDTO) {
        return medicationRemindersDTO
            .getReminders()
            .stream()
            .map(reminder -> new FCMMessageDTO().title(medicationRemindersDTO.getName() + " Reminder").date(reminder.getDate().toString()))
            .collect(Collectors.toList());
    }

    private void handleTodayNewlyMedicationReminder(Medication medication) {
        if (
            medication.getNotification().getStartDate().isEqual(LocalDate.now()) ||
            medication.getNotification().getStartDate().isBefore(LocalDate.now())
        ) {
            initializeMedicationReminders(medication.getNotification());
            sendNewlyMedicationReminders(medication);
        }
    }

    private void initializeMedicationReminders(Notification notification) {
        notification
            .getTimeOfDays()
            .forEach(timeOfDay -> {
                Reminder reminder = new Reminder()
                    .notification(notification)
                    .status(Status.NOT_INTERACTED)
                    .date(LocalDateTime.now().with(timeOfDay.getTime()).toInstant(OffsetDateTime.now().getOffset()));

                notification.addReminder(reminder);
            });
    }

    private void sendNewlyMedicationReminders(Medication medication) {
        List<FCMMessageDTO> fcmMessageDTOs = medication
            .getNotification()
            .getReminders()
            .stream()
            .map(reminder -> new FCMMessageDTO().title(medication.getName() + " Reminder").date(reminder.getDate().toString()))
            .collect(Collectors.toList());

        fcmClient.sendNotificationDirect(FCM_TOKEN, fcmMessageDTOs);
    }
}
