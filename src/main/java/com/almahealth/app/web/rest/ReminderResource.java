package com.almahealth.app.web.rest;

import com.almahealth.app.repository.ReminderRepository;
import com.almahealth.app.service.ReminderQueryService;
import com.almahealth.app.service.ReminderService;
import com.almahealth.app.service.criteria.ReminderCriteria;
import com.almahealth.app.service.dto.ReminderDTO;
import com.almahealth.app.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.almahealth.app.domain.Reminder}.
 */
@RestController
@RequestMapping("/api")
public class ReminderResource {

    private final Logger log = LoggerFactory.getLogger(ReminderResource.class);

    private static final String ENTITY_NAME = "reminder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReminderService reminderService;

    private final ReminderRepository reminderRepository;

    private final ReminderQueryService reminderQueryService;

    public ReminderResource(
        ReminderService reminderService,
        ReminderRepository reminderRepository,
        ReminderQueryService reminderQueryService
    ) {
        this.reminderService = reminderService;
        this.reminderRepository = reminderRepository;
        this.reminderQueryService = reminderQueryService;
    }

    /**
     * {@code POST  /reminders} : Create a new reminder.
     *
     * @param reminderDTO the reminderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reminderDTO, or with status {@code 400 (Bad Request)} if the reminder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/reminders")
    public ResponseEntity<ReminderDTO> createReminder(@RequestBody ReminderDTO reminderDTO) throws URISyntaxException {
        log.debug("REST request to save Reminder : {}", reminderDTO);
        if (reminderDTO.getId() != null) {
            throw new BadRequestAlertException("A new reminder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ReminderDTO result = reminderService.save(reminderDTO);
        return ResponseEntity
            .created(new URI("/api/reminders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /reminders/:id} : Updates an existing reminder.
     *
     * @param id the id of the reminderDTO to save.
     * @param reminderDTO the reminderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reminderDTO,
     * or with status {@code 400 (Bad Request)} if the reminderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reminderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reminders/{id}")
    public ResponseEntity<ReminderDTO> updateReminder(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ReminderDTO reminderDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Reminder : {}, {}", id, reminderDTO);
        if (reminderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reminderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reminderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ReminderDTO result = reminderService.update(reminderDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reminderDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /reminders/:id} : Partial updates given fields of an existing reminder, field will ignore if it is null
     *
     * @param id the id of the reminderDTO to save.
     * @param reminderDTO the reminderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reminderDTO,
     * or with status {@code 400 (Bad Request)} if the reminderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the reminderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the reminderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/reminders/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReminderDTO> partialUpdateReminder(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ReminderDTO reminderDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Reminder partially : {}, {}", id, reminderDTO);
        if (reminderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reminderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reminderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReminderDTO> result = reminderService.partialUpdate(reminderDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reminderDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /reminders} : get all the reminders.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reminders in body.
     */
    @GetMapping("/reminders")
    public ResponseEntity<List<ReminderDTO>> getAllReminders(
        ReminderCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Reminders by criteria: {}", criteria);
        Page<ReminderDTO> page = reminderQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /reminders/count} : count all the reminders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/reminders/count")
    public ResponseEntity<Long> countReminders(ReminderCriteria criteria) {
        log.debug("REST request to count Reminders by criteria: {}", criteria);
        return ResponseEntity.ok().body(reminderQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /reminders/:id} : get the "id" reminder.
     *
     * @param id the id of the reminderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reminderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/reminders/{id}")
    public ResponseEntity<ReminderDTO> getReminder(@PathVariable Long id) {
        log.debug("REST request to get Reminder : {}", id);
        Optional<ReminderDTO> reminderDTO = reminderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reminderDTO);
    }

    /**
     * {@code DELETE  /reminders/:id} : delete the "id" reminder.
     *
     * @param id the id of the reminderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/reminders/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        log.debug("REST request to delete Reminder : {}", id);
        reminderService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
