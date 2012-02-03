package org.ei.commcare.util;

import java.util.HashMap;
import java.util.Map;

public class Xml {
    private final String xmlContent;

    public Xml(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public Map<String, String> getValuesOfFieldsSpecifiedByPath(HashMap<String, String> fieldInResultingMapVersusXPathToUse) {
        return new HashMap<String, String>();
    }
}
