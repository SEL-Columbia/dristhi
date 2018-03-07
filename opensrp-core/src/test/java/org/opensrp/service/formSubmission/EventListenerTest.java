package org.opensrp.service.formSubmission;


import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.scheduler.domain.MotechEvent;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.AppStateToken;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.repository.couch.AllClients;
import org.opensrp.repository.couch.AllEvents;
import org.opensrp.service.ClientService;
import org.opensrp.service.ConfigService;
import org.opensrp.service.ErrorTraceService;
import org.opensrp.service.EventService;
import org.opensrp.service.formSubmission.handler.EventsHandler;
import org.opensrp.service.formSubmission.handler.EventsRouter;
import org.opensrp.service.formSubmission.handler.IHandlerMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

public class EventListenerTest {

    @Mock
    private ConfigService configService;
    @Mock
    private AllEvents allEvents;
    @Mock
    private AllClients allClients;
    @Mock
    private ErrorTraceService errorTraceService;
    @Mock
    private ClientService clientService;
    IHandlerMapper handlerMapper;

    private EventService eventService;
    private EventsRouter eventsRouter;
    private EventsListener eventsListener;

    @Before
    public void setUp() {
        configService = mock(ConfigService.class);
        allEvents = mock(AllEvents.class);
        clientService = mock(ClientService.class);
        allClients = mock(AllClients.class);
        handlerMapper = mock(IHandlerMapper.class);

        when(configService.registerAppStateToken(any(AllConstants.Config.class), Matchers.anyObject(), anyString(), anyBoolean()))
                .thenReturn(new AppStateToken("token", 01l, 02l));
        eventsRouter = new EventsRouter(handlerMapper, "/schedules/schedule-configs");
        eventService = new EventService(allEvents, clientService);
        eventsListener = new EventsListener(eventsRouter, configService, allEvents, eventService,
                errorTraceService, allClients);
    }

    @Test
    public void shouldHandleNewEvent() throws Exception {
        EventsHandler eventHandler = mock(EventsHandler.class);
        Map<String, EventsHandler> handlerMap = new HashMap<>();
        handlerMap.put("VaccinesScheduleHandler", eventHandler);


        List<Client> clients = asList(new Client("2222"));
        List<Event> events = asList(new Event()
                .withIdentifier(AllConstants.Client.ZEIR_ID.toUpperCase(), "2")
                .withEventType("Vaccination"), new Event());


        when(allClients.findByEmptyServerVersion()).thenReturn(clients, Collections.<Client>emptyList());
        when(configService.getAppStateTokenByName(AllConstants.Config.EVENTS_PARSER_LAST_PROCESSED_EVENT))
                .thenReturn(new AppStateToken("token", 1l, 0l));
        when(allEvents.findByEmptyServerVersion()).thenReturn(events, Collections.EMPTY_LIST);
        when(allEvents.findByServerVersion(1l)).thenReturn(events);
        when(clientService.findAllByIdentifier(AllConstants.Client.ZEIR_ID.toUpperCase(), "2"))
                .thenReturn(clients);
        when(allEvents.findByBaseEntityAndType("222", "Birth Registration")).thenReturn(events);

        when(handlerMapper.handlerMap()).thenReturn(handlerMap);


        EventsListener spyEventListener = spy(eventsListener);
        when(spyEventListener.getCurrentMilliseconds()).thenReturn(0l);

        spyEventListener.processEvent();


        InOrder inOrder = inOrder(allClients, allEvents, eventHandler);
        clients.get(0).setServerVersion(System.currentTimeMillis());
        events.get(0).setServerVersion(System.currentTimeMillis());
        inOrder.verify(allClients).update(clients.get(0));
        inOrder.verify(allEvents).update(events.get(0));
        inOrder.verify(eventHandler, atLeastOnce()).handle(eq(events.get(0)), any(JSONObject.class), eq("BCG"));

    }

    @Test
    public void testComparator() {

    }

}
