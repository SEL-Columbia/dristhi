package org.opensrp.common.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class HttpResponseTest {

    @Test
    public void isSuccessTest() throws Exception {
        boolean isSuccess = true;
        String body = "hi";
        HttpResponse httpResponse = new HttpResponse(isSuccess, body);
        assertEquals(true, httpResponse.isSuccess());
    }

    @Test
    public void bodyTest() throws Exception {
        boolean isSuccess = true;
        String body = "hi";
        HttpResponse httpResponse = new HttpResponse(isSuccess, body);
        assertEquals("hi", httpResponse.body());
    }

    @Test
    public void statusCode() throws Exception {
        boolean isSuccess = true;
        String body = "hi";
        Integer statusCode = 11111;
        HttpResponse httpResponse = new HttpResponse(isSuccess, 11111, body);
        assertEquals(statusCode, httpResponse.statusCode());
    }
}
