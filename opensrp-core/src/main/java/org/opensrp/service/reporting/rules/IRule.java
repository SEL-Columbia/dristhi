package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;

public interface IRule {
    boolean apply(SafeMap reportFields);
}

