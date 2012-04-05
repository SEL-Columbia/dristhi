package org.ei.drishti.web.controller;

import org.ei.drishti.domain.AlertAction;
import org.ei.drishti.domain.AlertData;
import org.ei.drishti.service.AlertService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;

import static org.ei.drishti.web.controller.AlertController.AlertActionItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.delivery.schedule.util.SameItems.hasSameItemsAs;

public class AlertControllerTest {

    @Mock
    private AlertService alertService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGiveAlertActionForANMSinceTimeStamp() throws Exception {
        AlertAction alertAction = new AlertAction("Case X", "ANM 1", AlertData.create("Theresa", "Thaayi 1", "ANC 1", "due"));
        when(alertService.getNewAlertsForANM("ANM 1", 0L)).thenReturn(Arrays.asList(alertAction));

        AlertActionItem expectedAlertActionItem = AlertActionItem.from(alertAction);
        AlertController alertController = new AlertController(alertService);

        assertThat(Arrays.asList(expectedAlertActionItem), hasSameItemsAs(alertController.getNewAlertsForANM("ANM 1", 0L)));
    }
}
