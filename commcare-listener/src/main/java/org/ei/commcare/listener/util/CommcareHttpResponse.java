package org.ei.commcare.listener.util;

import org.apache.http.Header;

public class CommcareHttpResponse {
    private final Header[] headers;
    private final String responseContent;

    public CommcareHttpResponse(Header[] headers, String content) {
        this.headers = headers;
        this.responseContent = content;
    }

    public String content() {
        return responseContent;
    }
}
