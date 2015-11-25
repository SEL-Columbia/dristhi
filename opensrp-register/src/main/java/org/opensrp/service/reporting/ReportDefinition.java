package org.opensrp.service.reporting;

import ch.lambdaj.util.NotUniqueItemException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

public class ReportDefinition {
    private static Logger logger = LoggerFactory.getLogger(ReportDefinition.class.toString());

    private List<FormIndicator> formIndicators;

    public ReportDefinition(List<FormIndicator> formIndicators) {
        this.formIndicators = formIndicators;
    }

    public List<ReportIndicator> getIndicatorsByFormName(String formName) {
        try {
            FormIndicator formIndicator = selectUnique(formIndicators,
                    having(on(FormIndicator.class).form(), equalTo(formName)));
            return formIndicator == null ? new ArrayList<ReportIndicator>() : formIndicator.indicators();
        } catch (NotUniqueItemException e) {
            logger.error("There are more than one form indicator definition for the form: " + formName);
            throw e;
        }
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}