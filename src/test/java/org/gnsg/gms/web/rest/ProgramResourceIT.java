package org.gnsg.gms.web.rest;

import org.gnsg.gms.GmsApp;
import org.gnsg.gms.domain.Program;
import org.gnsg.gms.repository.ProgramRepository;
import org.gnsg.gms.repository.search.ProgramSearchRepository;
import org.gnsg.gms.service.ProgramService;
import org.gnsg.gms.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.gnsg.gms.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.gnsg.gms.domain.enumeration.EVENTTYPE;
import org.gnsg.gms.domain.enumeration.EVENTLOCATION;
import org.gnsg.gms.domain.enumeration.LANGARMENU;
import org.gnsg.gms.domain.enumeration.EventStatus;
/**
 * Integration tests for the {@link ProgramResource} REST controller.
 */
@SpringBootTest(classes = GmsApp.class)
public class ProgramResourceIT {

    private static final EVENTTYPE DEFAULT_EVENT_TYPE = EVENTTYPE.SUKHMANI_SAHIB;
    private static final EVENTTYPE UPDATED_EVENT_TYPE = EVENTTYPE.SUKHMANI_SAHIB_AT_HOME;

    private static final EVENTLOCATION DEFAULT_EVENT_LOCATION = EVENTLOCATION.HALL_2_GNSG;
    private static final EVENTLOCATION UPDATED_EVENT_LOCATION = EVENTLOCATION.HALL_3_GNSG;

    private static final Instant DEFAULT_EVENT_DATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EVENT_DATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_FAMILY = "AAAAAAAAAA";
    private static final String UPDATED_FAMILY = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_WITH_LANGAR = false;
    private static final Boolean UPDATED_WITH_LANGAR = true;

    private static final LANGARMENU DEFAULT_LANGAR_MENU = LANGARMENU.SIMPLE_JALEBI_SHAHIPANEER;
    private static final LANGARMENU UPDATED_LANGAR_MENU = LANGARMENU.SIMPLE_JALEBI_MATARPANEER;

    private static final Instant DEFAULT_LANGAR_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LANGAR_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_DUE_AMT = new BigDecimal(1);
    private static final BigDecimal UPDATED_DUE_AMT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_PAID_AMT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PAID_AMT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_BAL_AMT = new BigDecimal(1);
    private static final BigDecimal UPDATED_BAL_AMT = new BigDecimal(2);

    private static final Long DEFAULT_RECIEPT_NUMBER = 1L;
    private static final Long UPDATED_RECIEPT_NUMBER = 2L;

    private static final String DEFAULT_REMARK = "AAAAAAAAAA";
    private static final String UPDATED_REMARK = "BBBBBBBBBB";

    private static final Instant DEFAULT_BOOKING_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BOOKING_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final EventStatus DEFAULT_STATUS = EventStatus.BOOKED;
    private static final EventStatus UPDATED_STATUS = EventStatus.COMPLETED;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ProgramService programService;

    /**
     * This repository is mocked in the org.gnsg.gms.repository.search test package.
     *
     * @see org.gnsg.gms.repository.search.ProgramSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProgramSearchRepository mockProgramSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restProgramMockMvc;

    private Program program;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProgramResource programResource = new ProgramResource(programService);
        this.restProgramMockMvc = MockMvcBuilders.standaloneSetup(programResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Program createEntity(EntityManager em) {
        Program program = new Program()
            .eventType(DEFAULT_EVENT_TYPE)
            .eventLocation(DEFAULT_EVENT_LOCATION)
            .eventDateTime(DEFAULT_EVENT_DATE_TIME)
            .family(DEFAULT_FAMILY)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .email(DEFAULT_EMAIL)
            .address(DEFAULT_ADDRESS)
            .withLangar(DEFAULT_WITH_LANGAR)
            .langarMenu(DEFAULT_LANGAR_MENU)
            .langarTime(DEFAULT_LANGAR_TIME)
            .dueAmt(DEFAULT_DUE_AMT)
            .paidAmt(DEFAULT_PAID_AMT)
            .balAmt(DEFAULT_BAL_AMT)
            .recieptNumber(DEFAULT_RECIEPT_NUMBER)
            .remark(DEFAULT_REMARK)
            .bookingDate(DEFAULT_BOOKING_DATE)
            .status(DEFAULT_STATUS);
        return program;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Program createUpdatedEntity(EntityManager em) {
        Program program = new Program()
            .eventType(UPDATED_EVENT_TYPE)
            .eventLocation(UPDATED_EVENT_LOCATION)
            .eventDateTime(UPDATED_EVENT_DATE_TIME)
            .family(UPDATED_FAMILY)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .email(UPDATED_EMAIL)
            .address(UPDATED_ADDRESS)
            .withLangar(UPDATED_WITH_LANGAR)
            .langarMenu(UPDATED_LANGAR_MENU)
            .langarTime(UPDATED_LANGAR_TIME)
            .dueAmt(UPDATED_DUE_AMT)
            .paidAmt(UPDATED_PAID_AMT)
            .balAmt(UPDATED_BAL_AMT)
            .recieptNumber(UPDATED_RECIEPT_NUMBER)
            .remark(UPDATED_REMARK)
            .bookingDate(UPDATED_BOOKING_DATE)
            .status(UPDATED_STATUS);
        return program;
    }

    @BeforeEach
    public void initTest() {
        program = createEntity(em);
    }

    @Test
    @Transactional
    public void createProgram() throws Exception {
        int databaseSizeBeforeCreate = programRepository.findAll().size();

        // Create the Program
        restProgramMockMvc.perform(post("/api/programs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(program)))
            .andExpect(status().isCreated());

        // Validate the Program in the database
        List<Program> programList = programRepository.findAll();
        assertThat(programList).hasSize(databaseSizeBeforeCreate + 1);
        Program testProgram = programList.get(programList.size() - 1);
        assertThat(testProgram.getEventType()).isEqualTo(DEFAULT_EVENT_TYPE);
        assertThat(testProgram.getEventLocation()).isEqualTo(DEFAULT_EVENT_LOCATION);
        assertThat(testProgram.getEventDateTime()).isEqualTo(DEFAULT_EVENT_DATE_TIME);
        assertThat(testProgram.getFamily()).isEqualTo(DEFAULT_FAMILY);
        assertThat(testProgram.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testProgram.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testProgram.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testProgram.isWithLangar()).isEqualTo(DEFAULT_WITH_LANGAR);
        assertThat(testProgram.getLangarMenu()).isEqualTo(DEFAULT_LANGAR_MENU);
        assertThat(testProgram.getLangarTime()).isEqualTo(DEFAULT_LANGAR_TIME);
        assertThat(testProgram.getDueAmt()).isEqualTo(DEFAULT_DUE_AMT);
        assertThat(testProgram.getPaidAmt()).isEqualTo(DEFAULT_PAID_AMT);
        assertThat(testProgram.getBalAmt()).isEqualTo(DEFAULT_BAL_AMT);
        assertThat(testProgram.getRecieptNumber()).isEqualTo(DEFAULT_RECIEPT_NUMBER);
        assertThat(testProgram.getRemark()).isEqualTo(DEFAULT_REMARK);
        assertThat(testProgram.getBookingDate()).isEqualTo(DEFAULT_BOOKING_DATE);
        assertThat(testProgram.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the Program in Elasticsearch
        verify(mockProgramSearchRepository, times(1)).save(testProgram);
    }

    @Test
    @Transactional
    public void createProgramWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = programRepository.findAll().size();

        // Create the Program with an existing ID
        program.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProgramMockMvc.perform(post("/api/programs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(program)))
            .andExpect(status().isBadRequest());

        // Validate the Program in the database
        List<Program> programList = programRepository.findAll();
        assertThat(programList).hasSize(databaseSizeBeforeCreate);

        // Validate the Program in Elasticsearch
        verify(mockProgramSearchRepository, times(0)).save(program);
    }


    @Test
    @Transactional
    public void getAllPrograms() throws Exception {
        // Initialize the database
        programRepository.saveAndFlush(program);

        // Get all the programList
        restProgramMockMvc.perform(get("/api/programs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(program.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventType").value(hasItem(DEFAULT_EVENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].eventLocation").value(hasItem(DEFAULT_EVENT_LOCATION.toString())))
            .andExpect(jsonPath("$.[*].eventDateTime").value(hasItem(DEFAULT_EVENT_DATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].family").value(hasItem(DEFAULT_FAMILY)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].withLangar").value(hasItem(DEFAULT_WITH_LANGAR.booleanValue())))
            .andExpect(jsonPath("$.[*].langarMenu").value(hasItem(DEFAULT_LANGAR_MENU.toString())))
            .andExpect(jsonPath("$.[*].langarTime").value(hasItem(DEFAULT_LANGAR_TIME.toString())))
            .andExpect(jsonPath("$.[*].dueAmt").value(hasItem(DEFAULT_DUE_AMT.intValue())))
            .andExpect(jsonPath("$.[*].paidAmt").value(hasItem(DEFAULT_PAID_AMT.intValue())))
            .andExpect(jsonPath("$.[*].balAmt").value(hasItem(DEFAULT_BAL_AMT.intValue())))
            .andExpect(jsonPath("$.[*].recieptNumber").value(hasItem(DEFAULT_RECIEPT_NUMBER.intValue())))
            .andExpect(jsonPath("$.[*].remark").value(hasItem(DEFAULT_REMARK)))
            .andExpect(jsonPath("$.[*].bookingDate").value(hasItem(DEFAULT_BOOKING_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getProgram() throws Exception {
        // Initialize the database
        programRepository.saveAndFlush(program);

        // Get the program
        restProgramMockMvc.perform(get("/api/programs/{id}", program.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(program.getId().intValue()))
            .andExpect(jsonPath("$.eventType").value(DEFAULT_EVENT_TYPE.toString()))
            .andExpect(jsonPath("$.eventLocation").value(DEFAULT_EVENT_LOCATION.toString()))
            .andExpect(jsonPath("$.eventDateTime").value(DEFAULT_EVENT_DATE_TIME.toString()))
            .andExpect(jsonPath("$.family").value(DEFAULT_FAMILY))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.withLangar").value(DEFAULT_WITH_LANGAR.booleanValue()))
            .andExpect(jsonPath("$.langarMenu").value(DEFAULT_LANGAR_MENU.toString()))
            .andExpect(jsonPath("$.langarTime").value(DEFAULT_LANGAR_TIME.toString()))
            .andExpect(jsonPath("$.dueAmt").value(DEFAULT_DUE_AMT.intValue()))
            .andExpect(jsonPath("$.paidAmt").value(DEFAULT_PAID_AMT.intValue()))
            .andExpect(jsonPath("$.balAmt").value(DEFAULT_BAL_AMT.intValue()))
            .andExpect(jsonPath("$.recieptNumber").value(DEFAULT_RECIEPT_NUMBER.intValue()))
            .andExpect(jsonPath("$.remark").value(DEFAULT_REMARK))
            .andExpect(jsonPath("$.bookingDate").value(DEFAULT_BOOKING_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProgram() throws Exception {
        // Get the program
        restProgramMockMvc.perform(get("/api/programs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProgram() throws Exception {
        // Initialize the database
        programService.save(program);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockProgramSearchRepository);

        int databaseSizeBeforeUpdate = programRepository.findAll().size();

        // Update the program
        Program updatedProgram = programRepository.findById(program.getId()).get();
        // Disconnect from session so that the updates on updatedProgram are not directly saved in db
        em.detach(updatedProgram);
        updatedProgram
            .eventType(UPDATED_EVENT_TYPE)
            .eventLocation(UPDATED_EVENT_LOCATION)
            .eventDateTime(UPDATED_EVENT_DATE_TIME)
            .family(UPDATED_FAMILY)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .email(UPDATED_EMAIL)
            .address(UPDATED_ADDRESS)
            .withLangar(UPDATED_WITH_LANGAR)
            .langarMenu(UPDATED_LANGAR_MENU)
            .langarTime(UPDATED_LANGAR_TIME)
            .dueAmt(UPDATED_DUE_AMT)
            .paidAmt(UPDATED_PAID_AMT)
            .balAmt(UPDATED_BAL_AMT)
            .recieptNumber(UPDATED_RECIEPT_NUMBER)
            .remark(UPDATED_REMARK)
            .bookingDate(UPDATED_BOOKING_DATE)
            .status(UPDATED_STATUS);

        restProgramMockMvc.perform(put("/api/programs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProgram)))
            .andExpect(status().isOk());

        // Validate the Program in the database
        List<Program> programList = programRepository.findAll();
        assertThat(programList).hasSize(databaseSizeBeforeUpdate);
        Program testProgram = programList.get(programList.size() - 1);
        assertThat(testProgram.getEventType()).isEqualTo(UPDATED_EVENT_TYPE);
        assertThat(testProgram.getEventLocation()).isEqualTo(UPDATED_EVENT_LOCATION);
        assertThat(testProgram.getEventDateTime()).isEqualTo(UPDATED_EVENT_DATE_TIME);
        assertThat(testProgram.getFamily()).isEqualTo(UPDATED_FAMILY);
        assertThat(testProgram.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testProgram.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testProgram.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testProgram.isWithLangar()).isEqualTo(UPDATED_WITH_LANGAR);
        assertThat(testProgram.getLangarMenu()).isEqualTo(UPDATED_LANGAR_MENU);
        assertThat(testProgram.getLangarTime()).isEqualTo(UPDATED_LANGAR_TIME);
        assertThat(testProgram.getDueAmt()).isEqualTo(UPDATED_DUE_AMT);
        assertThat(testProgram.getPaidAmt()).isEqualTo(UPDATED_PAID_AMT);
        assertThat(testProgram.getBalAmt()).isEqualTo(UPDATED_BAL_AMT);
        assertThat(testProgram.getRecieptNumber()).isEqualTo(UPDATED_RECIEPT_NUMBER);
        assertThat(testProgram.getRemark()).isEqualTo(UPDATED_REMARK);
        assertThat(testProgram.getBookingDate()).isEqualTo(UPDATED_BOOKING_DATE);
        assertThat(testProgram.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the Program in Elasticsearch
        verify(mockProgramSearchRepository, times(1)).save(testProgram);
    }

    @Test
    @Transactional
    public void updateNonExistingProgram() throws Exception {
        int databaseSizeBeforeUpdate = programRepository.findAll().size();

        // Create the Program

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProgramMockMvc.perform(put("/api/programs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(program)))
            .andExpect(status().isBadRequest());

        // Validate the Program in the database
        List<Program> programList = programRepository.findAll();
        assertThat(programList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Program in Elasticsearch
        verify(mockProgramSearchRepository, times(0)).save(program);
    }

    @Test
    @Transactional
    public void deleteProgram() throws Exception {
        // Initialize the database
        programService.save(program);

        int databaseSizeBeforeDelete = programRepository.findAll().size();

        // Delete the program
        restProgramMockMvc.perform(delete("/api/programs/{id}", program.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Program> programList = programRepository.findAll();
        assertThat(programList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Program in Elasticsearch
        verify(mockProgramSearchRepository, times(1)).deleteById(program.getId());
    }

    @Test
    @Transactional
    public void searchProgram() throws Exception {
        // Initialize the database
        programService.save(program);
        when(mockProgramSearchRepository.search(queryStringQuery("id:" + program.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(program), PageRequest.of(0, 1), 1));
        // Search the program
        restProgramMockMvc.perform(get("/api/_search/programs?query=id:" + program.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(program.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventType").value(hasItem(DEFAULT_EVENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].eventLocation").value(hasItem(DEFAULT_EVENT_LOCATION.toString())))
            .andExpect(jsonPath("$.[*].eventDateTime").value(hasItem(DEFAULT_EVENT_DATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].family").value(hasItem(DEFAULT_FAMILY)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].withLangar").value(hasItem(DEFAULT_WITH_LANGAR.booleanValue())))
            .andExpect(jsonPath("$.[*].langarMenu").value(hasItem(DEFAULT_LANGAR_MENU.toString())))
            .andExpect(jsonPath("$.[*].langarTime").value(hasItem(DEFAULT_LANGAR_TIME.toString())))
            .andExpect(jsonPath("$.[*].dueAmt").value(hasItem(DEFAULT_DUE_AMT.intValue())))
            .andExpect(jsonPath("$.[*].paidAmt").value(hasItem(DEFAULT_PAID_AMT.intValue())))
            .andExpect(jsonPath("$.[*].balAmt").value(hasItem(DEFAULT_BAL_AMT.intValue())))
            .andExpect(jsonPath("$.[*].recieptNumber").value(hasItem(DEFAULT_RECIEPT_NUMBER.intValue())))
            .andExpect(jsonPath("$.[*].remark").value(hasItem(DEFAULT_REMARK)))
            .andExpect(jsonPath("$.[*].bookingDate").value(hasItem(DEFAULT_BOOKING_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
