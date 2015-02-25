package org.opensrp.reporting;

import org.opensrp.common.AllConstants;
import org.springframework.http.HttpHeaders;

public class HttpHeaderFactory {
    public static HttpHeaders allowOrigin(String origin) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AllConstants.HTTP.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, origin);
        return headers;
    }
}
