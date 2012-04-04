package org.ei.drishti.service;

import org.ei.drishti.domain.AlertData;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.AlertAction;
import org.ei.drishti.repository.AllAlertActions;
import org.ei.drishti.repository.AllMothers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AlertServiceTest {
    @Mock
    private AllAlertActions allAlertActions;
    @Mock
    private AllMothers allMothers;
    private AlertService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new AlertService(allAlertActions, allMothers);
    }

    @Test
    public void shouldSaveAlertAction() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "Thaayi 1", "Theresa").withAnmPhoneNumber("ANM phone no"));

        service.alertForMother("Case X", "ANC 1", "due");

        verify(allAlertActions).add(new AlertAction("Case X", "ANM phone no", AlertData.create("Theresa", "Thaayi 1", "ANC 1", "due")));
    }

    @Test
    public void shouldReturnAlertsBasedOnANMIDAndTimeStamp() throws Exception {
        List<AlertAction> alertActions = Arrays.asList(new AlertAction("Case X", "ANM 1", AlertData.create("Theresa", "Thaayi 1", "ANC 1", "due")));
        when(allAlertActions.findByANMIDAndTimeStamp("ANM 1", 1010101)).thenReturn(alertActions);

        List<AlertAction> alerts = service.getAlerts("ANM 1", 1010101);

        assertEquals(1, alerts.size());
        assertEquals(alertActions, alerts);
    }
}
