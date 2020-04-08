package org.gnsg.gms.web.rest;

import org.gnsg.gms.GmsApp;
import org.gnsg.gms.domain.Sevadar;
import org.gnsg.gms.repository.SevadarRepository;
import org.gnsg.gms.repository.search.SevadarSearchRepository;
import org.gnsg.gms.service.SevadarService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link SevadarResource} REST controller.
 */
@SpringBootTest(classes = GmsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class SevadarResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final Instant DEFAULT_SEVA_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SEVA_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_SEVA_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SEVA_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_VALID = false;
    private static final Boolean UPDATED_IS_VALID = true;

    @Autowired
    private SevadarRepository sevadarRepository;

    @Autowired
    private SevadarService sevadarService;

    /**
     * This repository is mocked in the org.gnsg.gms.repository.search test package.
     *
     * @see org.gnsg.gms.repository.search.SevadarSearchRepositoryMockConfiguration
     */
    @Autowired
    private SevadarSearchRepository mockSevadarSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSevadarMockMvc;

    private Sevadar sevadar;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sevadar createEntity(EntityManager em) {
        Sevadar sevadar = new Sevadar()
            .name(DEFAULT_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .sevaStartDate(DEFAULT_SEVA_START_DATE)
            .sevaEndDate(DEFAULT_SEVA_END_DATE)
            .isValid(DEFAULT_IS_VALID);
        return sevadar;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sevadar createUpdatedEntity(EntityManager em) {
        Sevadar sevadar = new Sevadar()
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .sevaStartDate(UPDATED_SEVA_START_DATE)
            .sevaEndDate(UPDATED_SEVA_END_DATE)
            .isValid(UPDATED_IS_VALID);
        return sevadar;
    }

    @BeforeEach
    public void initTest() {
        sevadar = createEntity(em);
    }

    @Test
    @Transactional
    public void createSevadar() throws Exception {
        int databaseSizeBeforeCreate = sevadarRepository.findAll().size();

        // Create the Sevadar
        restSevadarMockMvc.perform(post("/api/sevadars")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sevadar)))
            .andExpect(status().isCreated());

        // Validate the Sevadar in the database
        List<Sevadar> sevadarList = sevadarRepository.findAll();
        assertThat(sevadarList).hasSize(databaseSizeBeforeCreate + 1);
        Sevadar testSevadar = sevadarList.get(sevadarList.size() - 1);
        assertThat(testSevadar.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSevadar.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testSevadar.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testSevadar.getSevaStartDate()).isEqualTo(DEFAULT_SEVA_START_DATE);
        assertThat(testSevadar.getSevaEndDate()).isEqualTo(DEFAULT_SEVA_END_DATE);
        assertThat(testSevadar.isIsValid()).isEqualTo(DEFAULT_IS_VALID);

        // Validate the Sevadar in Elasticsearch
        verify(mockSevadarSearchRepository, times(1)).save(testSevadar);
    }

    @Test
    @Transactional
    public void createSevadarWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = sevadarRepository.findAll().size();

        // Create the Sevadar with an existing ID
        sevadar.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSevadarMockMvc.perform(post("/api/sevadars")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sevadar)))
            .andExpect(status().isBadRequest());

        // Validate the Sevadar in the database
        List<Sevadar> sevadarList = sevadarRepository.findAll();
        assertThat(sevadarList).hasSize(databaseSizeBeforeCreate);

        // Validate the Sevadar in Elasticsearch
        verify(mockSevadarSearchRepository, times(0)).save(sevadar);
    }


    @Test
    @Transactional
    public void getAllSevadars() throws Exception {
        // Initialize the database
        sevadarRepository.saveAndFlush(sevadar);

        // Get all the sevadarList
        restSevadarMockMvc.perform(get("/api/sevadars?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sevadar.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].sevaStartDate").value(hasItem(DEFAULT_SEVA_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].sevaEndDate").value(hasItem(DEFAULT_SEVA_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].isValid").value(hasItem(DEFAULT_IS_VALID.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getSevadar() throws Exception {
        // Initialize the database
        sevadarRepository.saveAndFlush(sevadar);

        // Get the sevadar
        restSevadarMockMvc.perform(get("/api/sevadars/{id}", sevadar.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sevadar.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.sevaStartDate").value(DEFAULT_SEVA_START_DATE.toString()))
            .andExpect(jsonPath("$.sevaEndDate").value(DEFAULT_SEVA_END_DATE.toString()))
            .andExpect(jsonPath("$.isValid").value(DEFAULT_IS_VALID.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingSevadar() throws Exception {
        // Get the sevadar
        restSevadarMockMvc.perform(get("/api/sevadars/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSevadar() throws Exception {
        // Initialize the database
        sevadarService.save(sevadar);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockSevadarSearchRepository);

        int databaseSizeBeforeUpdate = sevadarRepository.findAll().size();

        // Update the sevadar
        Sevadar updatedSevadar = sevadarRepository.findById(sevadar.getId()).get();
        // Disconnect from session so that the updates on updatedSevadar are not directly saved in db
        em.detach(updatedSevadar);
        updatedSevadar
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .sevaStartDate(UPDATED_SEVA_START_DATE)
            .sevaEndDate(UPDATED_SEVA_END_DATE)
            .isValid(UPDATED_IS_VALID);

        restSevadarMockMvc.perform(put("/api/sevadars")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedSevadar)))
            .andExpect(status().isOk());

        // Validate the Sevadar in the database
        List<Sevadar> sevadarList = sevadarRepository.findAll();
        assertThat(sevadarList).hasSize(databaseSizeBeforeUpdate);
        Sevadar testSevadar = sevadarList.get(sevadarList.size() - 1);
        assertThat(testSevadar.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSevadar.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testSevadar.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testSevadar.getSevaStartDate()).isEqualTo(UPDATED_SEVA_START_DATE);
        assertThat(testSevadar.getSevaEndDate()).isEqualTo(UPDATED_SEVA_END_DATE);
        assertThat(testSevadar.isIsValid()).isEqualTo(UPDATED_IS_VALID);

        // Validate the Sevadar in Elasticsearch
        verify(mockSevadarSearchRepository, times(1)).save(testSevadar);
    }

    @Test
    @Transactional
    public void updateNonExistingSevadar() throws Exception {
        int databaseSizeBeforeUpdate = sevadarRepository.findAll().size();

        // Create the Sevadar

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSevadarMockMvc.perform(put("/api/sevadars")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sevadar)))
            .andExpect(status().isBadRequest());

        // Validate the Sevadar in the database
        List<Sevadar> sevadarList = sevadarRepository.findAll();
        assertThat(sevadarList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sevadar in Elasticsearch
        verify(mockSevadarSearchRepository, times(0)).save(sevadar);
    }

    @Test
    @Transactional
    public void deleteSevadar() throws Exception {
        // Initialize the database
        sevadarService.save(sevadar);

        int databaseSizeBeforeDelete = sevadarRepository.findAll().size();

        // Delete the sevadar
        restSevadarMockMvc.perform(delete("/api/sevadars/{id}", sevadar.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Sevadar> sevadarList = sevadarRepository.findAll();
        assertThat(sevadarList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Sevadar in Elasticsearch
        verify(mockSevadarSearchRepository, times(1)).deleteById(sevadar.getId());
    }

    @Test
    @Transactional
    public void searchSevadar() throws Exception {
        // Initialize the database
        sevadarService.save(sevadar);
        when(mockSevadarSearchRepository.search(queryStringQuery("id:" + sevadar.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(sevadar), PageRequest.of(0, 1), 1));
        // Search the sevadar
        restSevadarMockMvc.perform(get("/api/_search/sevadars?query=id:" + sevadar.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sevadar.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].sevaStartDate").value(hasItem(DEFAULT_SEVA_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].sevaEndDate").value(hasItem(DEFAULT_SEVA_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].isValid").value(hasItem(DEFAULT_IS_VALID.booleanValue())));
    }
}
