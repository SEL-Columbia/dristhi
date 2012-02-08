package org.ei.commcare.listener.util;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommCareJsonFormContent {
    private List<String> headers;
    private List<String> values;

    public CommCareJsonFormContent(List<String> headers, List<String> values) {
        this.headers = headers;
        this.values = values;
    }

    public static CommCareJsonFormContent from(String jsonContent) {
        return new Gson().fromJson(jsonContent, CommCareJsonFormContent.class);
    }

    public List<String> headers() {
        return headers == null ? new ArrayList<String>() : headers;
    }

    public List<String> values() {
        return values == null ? new ArrayList<String>() : values;
    }

    public Map<String, String> getValuesOfFieldsSpecifiedByPath(Map<String, String> mappings) {
        HashMap<String, String> map = new HashMap<String, String>();

        for (int i = 0; i < headers().size(); i++) {
            if (mappings.containsKey(headers().get(i))) {
                map.put(mappings.get(headers().get(i)), values().get(i));
            }
        }

        return map;
    }
}
