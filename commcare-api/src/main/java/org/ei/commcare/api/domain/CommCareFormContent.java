package org.ei.commcare.api.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommCareFormContent {
    private List<String> headers;
    private List<String> values;

    public CommCareFormContent(List<String> headers, List<String> values) {
        this.headers = headers;
        this.values = values;
    }

    public Map<String, String> getValuesOfFieldsSpecifiedByPath(Map<String, String> mappingFromFieldNameInFormToKeyInResult) {
        HashMap<String, String> map = new HashMap<String, String>();

        for (int i = 0; i < headers().size(); i++) {
            if (mappingFromFieldNameInFormToKeyInResult.containsKey(headers().get(i))) {
                map.put(mappingFromFieldNameInFormToKeyInResult.get(headers().get(i)), values().get(i));
            }
        }

        return map;
    }

    private List<String> headers() {
        return headers == null ? new ArrayList<String>() : headers;
    }

    private List<String> values() {
        return values == null ? new ArrayList<String>() : values;
    }
}
