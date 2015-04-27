package org.opensrp.service.formSubmission.handler;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.service.ChildService;

public class VitaminAHandlerTest {
    @Mock
    private ChildService childService;

    private VitaminAHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new VitaminAHandler(childService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToChildService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "vitamin_a", "entity id 1", 0L, "1", null, 0L);

        handler.handle(submission);

        verify(childService).vitaminAProvided(submission);
    }
}

