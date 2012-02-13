package org.ei.commcare.api.contract;

import java.util.Map;

public class CommCareExportUrl {
    private String base;
    private Map<String, String> queryParams;

    public CommCareExportUrl(String base, Map<String, String> queryParams) {
        this.base = base;
        this.queryParams = queryParams;
    }

    public String url(String previousToken) {
        return base + "?export_tag=%22" + nameSpace() + "%22&format=json&previous_export=" + previousToken;
    }

    public String nameSpace() {
        return queryParams.get("nameSpace");
    }
}
