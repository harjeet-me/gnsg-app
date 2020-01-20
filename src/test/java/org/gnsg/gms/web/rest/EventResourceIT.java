package org.gnsg.gms.web.rest;

import org.gnsg.gms.GmsApp;
import org.gnsg.gms.domain.Event;
import org.gnsg.gms.repository.EventRepository;
import org.gnsg.gms.repository.search.EventSearchRepository;
import org.gnsg.gms.service.EventService;
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
 * Integration tests for the {@link EventResource} REST controller.
 */
@SpringBootTest(classes = GmsApp.class)
public class EventResourceIT {

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
    private EventRepository eventRepository;

    @Autowired
    private EventService eventService;

    /**
     * This repository is mocked in the org.gnsg.gms.repository.search test package.
     *
     * @see org.gnsg.gms.repository.search.EventSearchRepositoryMockConfiguration
     */
    @Autowired
    private EventSearchRepository mockEventSearchRepository;

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

    private MockMvc restEventMockMvc;

    private Event event;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EventResource eventResource = new EventResource(eventService);
        this.restEventMockMvc = MockMvcBuilders.standaloneSetup(eventResource)
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
    public static Event createEntity(EntityManager em) {
        Event event = new Event()
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
        return event;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createUpdatedEntity(EntityManager em) {
        Event event = new Event()
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
        return event;
    }

    @BeforeEach
    public void initTest() {
        event = createEntity(em);
    }

    @Test
    @Transactional
    public void createEvent() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().size();

        // Create the Event
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isCreated());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate + 1);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getEventType()).isEqualTo(DEFAULT_EVENT_TYPE);
        assertThat(testEvent.getEventLocation()).isEqualTo(DEFAULT_EVENT_LOCATION);
        assertThat(testEvent.getEventDateTime()).isEqualTo(DEFAULT_EVENT_DATE_TIME);
        assertThat(testEvent.getFamily()).isEqualTo(DEFAULT_FAMILY);
        assertThat(testEvent.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testEvent.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testEvent.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testEvent.isWithLangar()).isEqualTo(DEFAULT_WITH_LANGAR);
        assertThat(testEvent.getLangarMenu()).isEqualTo(DEFAULT_LANGAR_MENU);
        assertThat(testEvent.getLangarTime()).isEqualTo(DEFAULT_LANGAR_TIME);
        assertThat(testEvent.getDueAmt()).isEqualTo(DEFAULT_DUE_AMT);
        assertThat(testEvent.getPaidAmt()).isEqualTo(DEFAULT_PAID_AMT);
        assertThat(testEvent.getBalAmt()).isEqualTo(DEFAULT_BAL_AMT);
        assertThat(testEvent.getRecieptNumber()).isEqualTo(DEFAULT_RECIEPT_NUMBER);
        assertThat(testEvent.getRemark()).isEqualTo(DEFAULT_REMARK);
        assertThat(testEvent.getBookingDate()).isEqualTo(DEFAULT_BOOKING_DATE);
        assertThat(testEvent.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the Event in Elasticsearch
        verify(mockEventSearchRepository, times(1)).save(testEvent);
    }

    @Test
    @Transactional
    public void createEventWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().size();

        // Create the Event with an existing ID
        event.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate);

        // Validate the Event in Elasticsearch
        verify(mockEventSearchRepository, times(0)).save(event);
    }


    @Test
    @Transactional
    public void getAllEvents() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList
        restEventMockMvc.perform(get("/api/events?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
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
    public void getEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get the event
        restEventMockMvc.perform(get("/api/events/{id}", event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(event.getId().intValue()))
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
    public void getNonExistingEvent() throws Exception {
        // Get the event
        restEventMockMvc.perform(get("/api/events/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEvent() throws Exception {
        // Initialize the database
        eventService.save(event);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockEventSearchRepository);

        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Update the event
        Event updatedEvent = eventRepository.findById(event.getId()).get();
        // Disconnect from session so that the updates on updatedEvent are not directly saved in db
        em.detach(updatedEvent);
        updatedEvent
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

        restEventMockMvc.perform(put("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedEvent)))
            .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getEventType()).isEqualTo(UPDATED_EVENT_TYPE);
        assertThat(testEvent.getEventLocation()).isEqualTo(UPDATED_EVENT_LOCATION);
        assertThat(testEvent.getEventDateTime()).isEqualTo(UPDATED_EVENT_DATE_TIME);
        assertThat(testEvent.getFamily()).isEqualTo(UPDATED_FAMILY);
        assertThat(testEvent.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testEvent.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testEvent.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testEvent.isWithLangar()).isEqualTo(UPDATED_WITH_LANGAR);
        assertThat(testEvent.getLangarMenu()).isEqualTo(UPDATED_LANGAR_MENU);
        assertThat(testEvent.getLangarTime()).isEqualTo(UPDATED_LANGAR_TIME);
        assertThat(testEvent.getDueAmt()).isEqualTo(UPDATED_DUE_AMT);
        assertThat(testEvent.getPaidAmt()).isEqualTo(UPDATED_PAID_AMT);
        assertThat(testEvent.getBalAmt()).isEqualTo(UPDATED_BAL_AMT);
        assertThat(testEvent.getRecieptNumber()).isEqualTo(UPDATED_RECIEPT_NUMBER);
        assertThat(testEvent.getRemark()).isEqualTo(UPDATED_REMARK);
        assertThat(testEvent.getBookingDate()).isEqualTo(UPDATED_BOOKING_DATE);
        assertThat(testEvent.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the Event in Elasticsearch
        verify(mockEventSearchRepository, times(1)).save(testEvent);
    }

    @Test
    @Transactional
    public void updateNonExistingEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Create the Event

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc.perform(put("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Event in Elasticsearch
        verify(mockEventSearchRepository, times(0)).save(event);
    }

    @Test
    @Transactional
    public void deleteEvent() throws Exception {
        // Initialize the database
        eventService.save(event);

        int databaseSizeBeforeDelete = eventRepository.findAll().size();

        // Delete the event
        restEventMockMvc.perform(delete("/api/events/{id}", event.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Event in Elasticsearch
        verify(mockEventSearchRepository, times(1)).deleteById(event.getId());
    }

    @Test
    @Transactional
    public void searchEvent() throws Exception {
        // Initialize the database
        eventService.save(event);
        when(mockEventSearchRepository.search(queryStringQuery("id:" + event.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(event), PageRequest.of(0, 1), 1));
        // Search the event
        restEventMockMvc.perform(get("/api/_search/events?query=id:" + event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
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
