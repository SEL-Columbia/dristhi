package org.ei.commcare.contract;

import java.util.Map;

public class CommcareExportUrl {
    private String base;
    private Map<String, String> queryParams;

    public CommcareExportUrl(String base, Map<String, String> queryParams) {
        this.base = base;
        this.queryParams = queryParams;
    }

    public String url() {
        return base + "&export_tag=%22" + queryParams.get("nameSpace") + "%22&format=raw";
    }
}
