package org.ei.commcare.listener.util;

import org.apache.http.Header;

public class CommCareHttpResponse {
    private final int statusCode;
    private final Header[] headers;
    private final String responseContent;

    public CommCareHttpResponse(int statusCode, Header[] headers, String content) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.responseContent = content;
    }

    public String content() {
        return responseContent;
    }

    public String tokenForNextExport() {
        return header("X-CommCareHQ-Export-Token");
    }

    private String header(String key) {
        for (Header header : headers) {
            if (header.getName().equals(key)) {
                return header.getValue();
            }
        }
        return "";
    }

    public boolean isValid() {
        return statusCode == 200 && !tokenForNextExport().isEmpty();
    }

    @Override
    public String toString() {
        return "Status code: " + statusCode + ", Token for next export: " + tokenForNextExport() + ", Content: " + content();
    }
}
