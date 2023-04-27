package com.almahealth.app.web.rest;

import com.almahealth.app.repository.TimeOfDayRepository;
import com.almahealth.app.service.TimeOfDayService;
import com.almahealth.app.service.dto.TimeOfDayDTO;
import com.almahealth.app.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.almahealth.app.domain.TimeOfDay}.
 */
@RestController
@RequestMapping("/api")
public class TimeOfDayResource {

    private final Logger log = LoggerFactory.getLogger(TimeOfDayResource.class);

    private static final String ENTITY_NAME = "timeOfDay";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TimeOfDayService timeOfDayService;

    private final TimeOfDayRepository timeOfDayRepository;

    public TimeOfDayResource(TimeOfDayService timeOfDayService, TimeOfDayRepository timeOfDayRepository) {
        this.timeOfDayService = timeOfDayService;
        this.timeOfDayRepository = timeOfDayRepository;
    }

    /**
     * {@code POST  /time-of-days} : Create a new timeOfDay.
     *
     * @param timeOfDayDTO the timeOfDayDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new timeOfDayDTO, or with status {@code 400 (Bad Request)} if the timeOfDay has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/time-of-days")
    public ResponseEntity<TimeOfDayDTO> createTimeOfDay(@RequestBody @Valid TimeOfDayDTO timeOfDayDTO) throws URISyntaxException {
        log.debug("REST request to save TimeOfDay : {}", timeOfDayDTO);
        if (timeOfDayDTO.getId() != null) {
            throw new BadRequestAlertException("A new timeOfDay cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TimeOfDayDTO result = timeOfDayService.save(timeOfDayDTO);
        return ResponseEntity
            .created(new URI("/api/time-of-days/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /time-of-days/:id} : Updates an existing timeOfDay.
     *
     * @param id the id of the timeOfDayDTO to save.
     * @param timeOfDayDTO the timeOfDayDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timeOfDayDTO,
     * or with status {@code 400 (Bad Request)} if the timeOfDayDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timeOfDayDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/time-of-days/{id}")
    public ResponseEntity<TimeOfDayDTO> updateTimeOfDay(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody @Valid TimeOfDayDTO timeOfDayDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TimeOfDay : {}, {}", id, timeOfDayDTO);
        if (timeOfDayDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timeOfDayDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timeOfDayRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TimeOfDayDTO result = timeOfDayService.update(timeOfDayDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timeOfDayDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /time-of-days/:id} : Partial updates given fields of an existing timeOfDay, field will ignore if it is null
     *
     * @param id the id of the timeOfDayDTO to save.
     * @param timeOfDayDTO the timeOfDayDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timeOfDayDTO,
     * or with status {@code 400 (Bad Request)} if the timeOfDayDTO is not valid,
     * or with status {@code 404 (Not Found)} if the timeOfDayDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the timeOfDayDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/time-of-days/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TimeOfDayDTO> partialUpdateTimeOfDay(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TimeOfDayDTO timeOfDayDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TimeOfDay partially : {}, {}", id, timeOfDayDTO);
        if (timeOfDayDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timeOfDayDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timeOfDayRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TimeOfDayDTO> result = timeOfDayService.partialUpdate(timeOfDayDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timeOfDayDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /time-of-days} : get all the timeOfDays.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of timeOfDays in body.
     */
    @GetMapping("/time-of-days")
    public ResponseEntity<List<TimeOfDayDTO>> getAllTimeOfDays(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of TimeOfDays");
        Page<TimeOfDayDTO> page = timeOfDayService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /time-of-days/:id} : get the "id" timeOfDay.
     *
     * @param id the id of the timeOfDayDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the timeOfDayDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/time-of-days/{id}")
    public ResponseEntity<TimeOfDayDTO> getTimeOfDay(@PathVariable Long id) {
        log.debug("REST request to get TimeOfDay : {}", id);
        Optional<TimeOfDayDTO> timeOfDayDTO = timeOfDayService.findOne(id);
        return ResponseUtil.wrapOrNotFound(timeOfDayDTO);
    }

    /**
     * {@code DELETE  /time-of-days/:id} : delete the "id" timeOfDay.
     *
     * @param id the id of the timeOfDayDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/time-of-days/{id}")
    public ResponseEntity<Void> deleteTimeOfDay(@PathVariable Long id) {
        log.debug("REST request to delete TimeOfDay : {}", id);
        timeOfDayService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
