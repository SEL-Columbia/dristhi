package org.opensrp.common.util;

public class HttpResponse {
    private final boolean isSuccess;
    private final String body;
	private Integer statusCode;

	public HttpResponse(boolean isSuccess, String body) {
        this.isSuccess = isSuccess;
        this.body = body;
    }
	
    public HttpResponse(boolean isSuccess, int statusCode, String body) {
        this.isSuccess = isSuccess;
        this.body = body;
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String body() {
        return body;
    }
    
    public Integer statusCode() {
        return statusCode;
    }
}
