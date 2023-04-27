package com.almahealth.app.service;

import com.almahealth.app.domain.TimeOfDay;
import com.almahealth.app.repository.TimeOfDayRepository;
import com.almahealth.app.service.dto.TimeOfDayDTO;
import com.almahealth.app.service.mapper.TimeOfDayMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TimeOfDay}.
 */
@Service
@Transactional
public class TimeOfDayService {

    private final Logger log = LoggerFactory.getLogger(TimeOfDayService.class);

    private final TimeOfDayRepository timeOfDayRepository;

    private final TimeOfDayMapper timeOfDayMapper;

    public TimeOfDayService(TimeOfDayRepository timeOfDayRepository, TimeOfDayMapper timeOfDayMapper) {
        this.timeOfDayRepository = timeOfDayRepository;
        this.timeOfDayMapper = timeOfDayMapper;
    }

    /**
     * Save a timeOfDay.
     *
     * @param timeOfDayDTO the entity to save.
     * @return the persisted entity.
     */
    public TimeOfDayDTO save(TimeOfDayDTO timeOfDayDTO) {
        log.debug("Request to save TimeOfDay : {}", timeOfDayDTO);
        TimeOfDay timeOfDay = timeOfDayMapper.toEntity(timeOfDayDTO);
        timeOfDay = timeOfDayRepository.save(timeOfDay);
        return timeOfDayMapper.toDto(timeOfDay);
    }

    /**
     * Update a timeOfDay.
     *
     * @param timeOfDayDTO the entity to save.
     * @return the persisted entity.
     */
    public TimeOfDayDTO update(TimeOfDayDTO timeOfDayDTO) {
        log.debug("Request to update TimeOfDay : {}", timeOfDayDTO);
        TimeOfDay timeOfDay = timeOfDayMapper.toEntity(timeOfDayDTO);
        timeOfDay = timeOfDayRepository.save(timeOfDay);
        return timeOfDayMapper.toDto(timeOfDay);
    }

    /**
     * Partially update a timeOfDay.
     *
     * @param timeOfDayDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TimeOfDayDTO> partialUpdate(TimeOfDayDTO timeOfDayDTO) {
        log.debug("Request to partially update TimeOfDay : {}", timeOfDayDTO);

        return timeOfDayRepository
            .findById(timeOfDayDTO.getId())
            .map(existingTimeOfDay -> {
                timeOfDayMapper.partialUpdate(existingTimeOfDay, timeOfDayDTO);

                return existingTimeOfDay;
            })
            .map(timeOfDayRepository::save)
            .map(timeOfDayMapper::toDto);
    }

    /**
     * Get all the timeOfDays.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TimeOfDayDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TimeOfDays");
        return timeOfDayRepository.findAll(pageable).map(timeOfDayMapper::toDto);
    }

    /**
     * Get one timeOfDay by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TimeOfDayDTO> findOne(Long id) {
        log.debug("Request to get TimeOfDay : {}", id);
        return timeOfDayRepository.findById(id).map(timeOfDayMapper::toDto);
    }

    /**
     * Delete the timeOfDay by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TimeOfDay : {}", id);
        timeOfDayRepository.deleteById(id);
    }
}
