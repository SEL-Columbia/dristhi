package org.opensrp.web.controller;

import org.opensrp.scheduler.service.ActionService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.dto.Action;
import org.opensrp.dto.ActionData;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.delivery.schedule.util.SameItems.hasSameItemsAs;
import static org.opensrp.dto.AlertStatus.normal;
import static org.opensrp.dto.BeneficiaryType.mother;

import org.opensrp.web.controller.ActionController;
import org.opensrp.web.controller.ActionConvertor;

public class ActionControllerTest {

    @Mock
    private ActionService actionService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGiveAlertActionForANMSinceTimeStamp() throws Exception {
        org.opensrp.scheduler.Action alertAction = new org.opensrp.scheduler.Action("Case X", "ANM 1", ActionData.createAlert(mother.value(), "Ante Natal Care - Normal", "ANC 1", normal, DateTime.now(), DateTime.now().plusDays(3)));
        when(actionService.getNewAlertsForANM("ANM 1", 0L)).thenReturn(asList(alertAction));

        Action expectedAlertActionItem = ActionConvertor.from(alertAction);
        ActionController actionController = new ActionController(actionService);

        assertThat(asList(expectedAlertActionItem), hasSameItemsAs(actionController.getNewActionForANM("ANM 1", 0L)));
    }
}
