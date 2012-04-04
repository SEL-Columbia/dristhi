package org.ei.drishti.service;

import org.ei.drishti.domain.AlertData;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.AlertAction;
import org.ei.drishti.repository.AllAlertActions;
import org.ei.drishti.repository.AllMothers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AlertServiceTest {
    @Mock
    private AllAlertActions allAlertActions;
    @Mock
    private AllMothers allMothers;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveReminder() throws Exception {
        AlertService service = new AlertService(allAlertActions, allMothers);
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "Thaayi 1", "Theresa").withAnmPhoneNumber("ANM phone no"));

        service.alertForMother("Case X", "ANC 1", "due");

        verify(allAlertActions).add(new AlertAction("Case X", "ANM phone no", AlertData.create("Theresa", "Thaayi 1", "ANC 1", "due")));
    }
}
