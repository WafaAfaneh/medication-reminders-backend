package com.almahealth.app.service;

import com.almahealth.app.domain.Frequency;
import com.almahealth.app.repository.FrequencyRepository;
import com.almahealth.app.service.dto.FrequencyDTO;
import com.almahealth.app.service.mapper.FrequencyMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Frequency}.
 */
@Service
@Transactional
public class FrequencyService {

    private final Logger log = LoggerFactory.getLogger(FrequencyService.class);

    private final FrequencyRepository frequencyRepository;

    private final FrequencyMapper frequencyMapper;

    public FrequencyService(FrequencyRepository frequencyRepository, FrequencyMapper frequencyMapper) {
        this.frequencyRepository = frequencyRepository;
        this.frequencyMapper = frequencyMapper;
    }

    /**
     * Save a frequency.
     *
     * @param frequencyDTO the entity to save.
     * @return the persisted entity.
     */
    public FrequencyDTO save(FrequencyDTO frequencyDTO) {
        log.debug("Request to save Frequency : {}", frequencyDTO);
        Frequency frequency = frequencyMapper.toEntity(frequencyDTO);
        frequency = frequencyRepository.save(frequency);
        return frequencyMapper.toDto(frequency);
    }

    /**
     * Update a frequency.
     *
     * @param frequencyDTO the entity to save.
     * @return the persisted entity.
     */
    public FrequencyDTO update(FrequencyDTO frequencyDTO) {
        log.debug("Request to update Frequency : {}", frequencyDTO);
        Frequency frequency = frequencyMapper.toEntity(frequencyDTO);
        frequency = frequencyRepository.save(frequency);
        return frequencyMapper.toDto(frequency);
    }

    /**
     * Partially update a frequency.
     *
     * @param frequencyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FrequencyDTO> partialUpdate(FrequencyDTO frequencyDTO) {
        log.debug("Request to partially update Frequency : {}", frequencyDTO);

        return frequencyRepository
            .findById(frequencyDTO.getId())
            .map(existingFrequency -> {
                frequencyMapper.partialUpdate(existingFrequency, frequencyDTO);

                return existingFrequency;
            })
            .map(frequencyRepository::save)
            .map(frequencyMapper::toDto);
    }

    /**
     * Get all the frequencies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FrequencyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Frequencies");
        return frequencyRepository.findAll(pageable).map(frequencyMapper::toDto);
    }

    /**
     *  Get all the frequencies where Notification is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FrequencyDTO> findAllWhereNotificationIsNull() {
        log.debug("Request to get all frequencies where Notification is null");
        return StreamSupport
            .stream(frequencyRepository.findAll().spliterator(), false)
            .filter(frequency -> frequency.getNotification() == null)
            .map(frequencyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one frequency by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FrequencyDTO> findOne(Long id) {
        log.debug("Request to get Frequency : {}", id);
        return frequencyRepository.findById(id).map(frequencyMapper::toDto);
    }

    /**
     * Delete the frequency by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Frequency : {}", id);
        frequencyRepository.deleteById(id);
    }
}
