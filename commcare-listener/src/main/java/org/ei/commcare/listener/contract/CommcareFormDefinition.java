package org.ei.commcare.listener.contract;

import java.util.Map;

public class CommcareFormDefinition {
    private CommCareExportUrl url;
    private String name;
    private final Map<String, String> mappings;

    public CommcareFormDefinition(String name, CommCareExportUrl url, Map<String, String> mappings) {
        this.url = url;
        this.name = name;
        this.mappings = mappings;
    }

    public String url(String previousToken) {
        return url.url(previousToken);
    }

    public String name() {
        return name;
    }

    public Map<String, String> mappings() {
        return mappings;
    }

    public String nameSpace() {
        return url.nameSpace();
    }
}
