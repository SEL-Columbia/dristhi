package org.ei.drishti.common.util;

public class HttpResponse {
    private final boolean isSuccess;
    private final String body;

    public HttpResponse(boolean isSuccess, String body) {
        this.isSuccess = isSuccess;
        this.body = body;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String body() {
        return body;
    }
}
