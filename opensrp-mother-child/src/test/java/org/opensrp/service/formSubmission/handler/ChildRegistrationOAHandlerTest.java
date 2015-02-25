package org.opensrp.service.formSubmission.handler;

import org.opensrp.form.domain.FormSubmission;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.opensrp.service.ChildService;
import org.opensrp.service.PNCService;
import org.opensrp.service.formSubmission.handler.ChildRegistrationOAHandler;

public class ChildRegistrationOAHandlerTest {
    @Mock
    private ChildService childService;

    private ChildRegistrationOAHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new ChildRegistrationOAHandler(childService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToChildService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "child_registration_oa", "entity id 1", 0L, "1", null, 0L);

        handler.handle(submission);

        verify(childService).registerChildrenForOA(submission);
    }
}
