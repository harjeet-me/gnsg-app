package org.gnsg.gms.service.impl;

import org.gnsg.gms.service.FileService;
import org.gnsg.gms.domain.File;
import org.gnsg.gms.repository.FileRepository;
import org.gnsg.gms.repository.search.FileSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link File}.
 */
@Service
@Transactional
public class FileServiceImpl implements FileService {

    private final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    private final FileRepository fileRepository;

    private final FileSearchRepository fileSearchRepository;

    public FileServiceImpl(FileRepository fileRepository, FileSearchRepository fileSearchRepository) {
        this.fileRepository = fileRepository;
        this.fileSearchRepository = fileSearchRepository;
    }

    /**
     * Save a file.
     *
     * @param file the entity to save.
     * @return the persisted entity.
     */
    @Override
    public File save(File file) {
        log.debug("Request to save File : {}", file);
        File result = fileRepository.save(file);
        fileSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the files.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<File> findAll() {
        log.debug("Request to get all Files");
        return fileRepository.findAll();
    }

    /**
     * Get one file by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<File> findOne(Long id) {
        log.debug("Request to get File : {}", id);
        return fileRepository.findById(id);
    }

    /**
     * Delete the file by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete File : {}", id);
        fileRepository.deleteById(id);
        fileSearchRepository.deleteById(id);
    }

    /**
     * Search for the file corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<File> search(String query) {
        log.debug("Request to search Files for query {}", query);
        return StreamSupport
            .stream(fileSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
