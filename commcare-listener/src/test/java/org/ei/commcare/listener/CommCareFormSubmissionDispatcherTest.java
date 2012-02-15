package org.ei.commcare.listener;

import org.ei.commcare.listener.event.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommCareFormSubmissionDispatcherTest {
    @Mock
    FakeDrishtiController drishtiController;

    private CommCareFormSubmissionDispatcher commCareFormSubmissionDispatcher;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        commCareFormSubmissionDispatcher = new CommCareFormSubmissionDispatcher();
        commCareFormSubmissionDispatcher.registerForDispatch(drishtiController);
    }

    @Test
    public void shouldDispatchToMethodSpecifiedByFormNameWithAnArgumentFromFormData() throws Exception {
        MotechEvent event = eventFor("registerMother", "{\"name\" : \"Mom\", \"age\" : \"23\"}");

        commCareFormSubmissionDispatcher.handle(event);

        verify(drishtiController).registerMother(new FakeMotherRegistrationRequest("Mom", 23));
    }

    @Test
    public void shouldBeAbleToCreateArgumentsWithDateInThemWhenDateIsInYYYYMMDDFormat() throws Exception {
        MotechEvent event = eventFor("methodWithArgumentHavingADate", "{\"date\" : \"2000-03-23\"}");

        commCareFormSubmissionDispatcher.handle(event);

        verify(drishtiController).methodWithArgumentHavingADate(new FakeRequestWithDate(new DateTime(2000, 3, 23, 0, 0).toDate()));
    }

    @Test
    public void shouldNotFailIfMethodForDispatchIsNotFound() throws Exception {
        MotechEvent event = eventFor("someMethodWhichDoesNotExist", "{\"name\" : \"Mom\", \"age\" : \"23\"}");

        commCareFormSubmissionDispatcher.handle(event);

        verifyZeroInteractions(drishtiController);
    }

    @Test
    public void shouldNotFailIfDataCannotBeConvertedToMethodParameterObject() throws Exception {
        MotechEvent event = eventFor("registerMother", "someWrongData");

        commCareFormSubmissionDispatcher.handle(event);

        verifyZeroInteractions(drishtiController);
    }

    @Test(expected = InvocationTargetException.class)
    public void shouldFailIfTheInvokedMethodThrowsAnException() throws Exception {
        CommCareFormSubmissionDispatcher dispatcher = new CommCareFormSubmissionDispatcher();
        dispatcher.registerForDispatch(new FakeDrishtiController());

        MotechEvent event = eventFor("methodWhichThrowsAnException", "{\"name\" : \"Mom\", \"age\" : \"23\"}");

        dispatcher.handle(event);
    }

    @Test
    public void shouldNotCallAMethodWithValidNameWithZeroArguments() throws Exception {
        MotechEvent event = eventFor("methodWithoutArguments", "{}");

        commCareFormSubmissionDispatcher.handle(event);

        verifyZeroInteractions(drishtiController);
    }

    @Test
    public void shouldNotCallAMethodWithValidNameWithTwoArguments() throws Exception {
        MotechEvent event = eventFor("methodWithTwoArguments", "{}");

        commCareFormSubmissionDispatcher.handle(event);

        verifyZeroInteractions(drishtiController);
    }

    @Test
    public void shouldDispatchEvenIfParameterClassDoesNotHaveADefaultConstructor() throws Exception {
        commCareFormSubmissionDispatcher.handle(eventFor("ancVisit", "{\"something\" : 3}"));

        verify(drishtiController).ancVisit(new FakeANCVisitRequestWithoutADefaultConstructor(3));
    }

    @Test
    public void shouldNotFailIfNoObjectToRouteToHasBeenSetup() throws Exception {
        CommCareFormSubmissionDispatcher dispatcher = new CommCareFormSubmissionDispatcher();

        dispatcher.registerForDispatch(null);

        dispatcher.handle(eventFor("registerMother", "{}"));
    }

    private MotechEvent eventFor(String name, String data) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(CommCareFormEvent.FORM_NAME_PARAMETER, name);
        parameters.put(CommCareFormEvent.FORM_DATA_PARAMETER, data);
        return new MotechEvent(CommCareFormEvent.EVENT_SUBJECT, parameters);
    }

}
