package org.ei.drishti.service.formSubmissionHandler;

import org.ei.drishti.domain.form.FormSubmission;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormSubmissionRouterTest {
    @Mock
    private ECRegistrationHandler ecRegistrationHandler;
    @Mock
    private FPComplicationsHandler fpComplicationsHandler;

    private FormSubmissionRouter router;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        router = new FormSubmissionRouter(ecRegistrationHandler, fpComplicationsHandler);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToHandlerBasedOnFormName() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "ec_registration", "entity id 1", null, 0L);

        router.route(formSubmission);

        verify(ecRegistrationHandler).handle(formSubmission);

        formSubmission = new FormSubmission("anm id 1", "instance id 2", "fp_complications", "entity id 2", null, 0L);

        router.route(formSubmission);

        verify(fpComplicationsHandler).handle(formSubmission);
    }
}
