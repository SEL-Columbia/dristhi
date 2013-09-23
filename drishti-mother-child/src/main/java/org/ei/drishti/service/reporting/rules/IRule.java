package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;

public interface IRule {
    boolean apply(SafeMap safeMap);
}

