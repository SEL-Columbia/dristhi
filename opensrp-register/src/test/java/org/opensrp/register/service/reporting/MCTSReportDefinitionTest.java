package org.opensrp.register.service.reporting;

import ch.lambdaj.util.NotUniqueItemException;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.opensrp.service.reporting.MCTSFormIndicator;
import org.opensrp.service.reporting.MCTSReportDefinition;
import org.opensrp.service.reporting.MCTSReportIndicator;

public class MCTSReportDefinitionTest {
    @Test(expected = NotUniqueItemException.class)
    public void shouldThrowNotUniqueExceptionWhenMoreThanOneMCTSFormIndicatorDefinitionExistsForAForm() throws Exception {
        MCTSReportDefinition reportDefinition = new MCTSReportDefinition(asList(
                new MCTSFormIndicator("form 1", Collections.<MCTSReportIndicator>emptyList()),
                new MCTSFormIndicator("form 1", Collections.<MCTSReportIndicator>emptyList())));

        reportDefinition.getIndicatorsByFormName("form 1");
    }

    @Test
    public void shouldGetMCTSFormIndicatorForGivenForm() throws Exception {
        MCTSReportDefinition reportDefinition = new MCTSReportDefinition(
                asList(
                        new MCTSFormIndicator("form 1", asList(new MCTSReportIndicator("form1Indicator"))),
                        new MCTSFormIndicator("form 2", asList(new MCTSReportIndicator("form2Indicator"))))
        );

        List<MCTSReportIndicator> indicators = reportDefinition.getIndicatorsByFormName("form 1");

        assertEquals(asList(new MCTSReportIndicator("form1Indicator")), indicators);

    }

    @Test
    public void shouldReturnEmptyMCTSFormIndicatorListWhenThereIsNoDefinitionForGivenForm() throws Exception {
        MCTSReportDefinition reportDefinition = new MCTSReportDefinition(
                asList(new MCTSFormIndicator("form 1", asList(new MCTSReportIndicator("form1Indicator")))));

        List<MCTSReportIndicator> indicators = reportDefinition.getIndicatorsByFormName("form without definition");

        assertTrue(indicators.isEmpty());
    }
}
