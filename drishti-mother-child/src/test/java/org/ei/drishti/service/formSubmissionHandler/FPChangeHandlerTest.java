package org.ei.drishti.service.formSubmissionHandler;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.ECService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FPChangeHandlerTest {
    @Mock
    private ECService ecService;

    private FPChangeHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new FPChangeHandler(ecService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToECService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "ec_registration", "entity id 1", null, 0L, 0L);

        handler.handle(submission);

        verify(ecService).reportFPChange(submission);
    }
}
