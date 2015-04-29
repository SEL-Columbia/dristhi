package org.opensrp.service.reporting;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import ch.lambdaj.util.NotUniqueItemException;

public class ReportDefinitionTest {

    @Test(expected = NotUniqueItemException.class)
    public void shouldThrowNotUniqueExceptionWhenMoreThanOneFormIndicatorDefinitionExistsForAForm() throws Exception {
        ReportDefinition reportDefinition = new ReportDefinition(asList(
                new FormIndicator("form 1", Collections.<ReportIndicator>emptyList()),
                new FormIndicator("form 1", Collections.<ReportIndicator>emptyList())));

        reportDefinition.getIndicatorsByFormName("form 1");
    }

    @Test
    public void shouldGetFormIndicatorForGivenForm() throws Exception {
        ReportDefinition reportDefinition = new ReportDefinition(
                asList(
                        new FormIndicator("form 1", asList(new ReportIndicator("form1Indicator"))),
                        new FormIndicator("form 2", asList(new ReportIndicator("form2Indicator")))));

        List<ReportIndicator> indicators = reportDefinition.getIndicatorsByFormName("form 1");

        assertEquals(asList(new ReportIndicator("form1Indicator")), indicators);

    }

    @Test
    public void shouldReturnEmptyFormIndicatorListWhenThereIsNoDefinitionForGivenForm() throws Exception {
        ReportDefinition reportDefinition = new ReportDefinition(
                asList(new FormIndicator("form 1", asList(new ReportIndicator("form1Indicator")))));

        List<ReportIndicator> indicators = reportDefinition.getIndicatorsByFormName("form without definition");

        assertTrue(indicators.isEmpty());
    }
}
