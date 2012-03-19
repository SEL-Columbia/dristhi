package org.ei.commcare.api.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CommCareHttpClient {
    private DefaultHttpClient httpClient;
    private static Logger logger = LoggerFactory.getLogger(CommCareHttpClient.class.toString());

    public CommCareHttpClient() {
        this.httpClient = new DefaultHttpClient();
    }

    public CommCareHttpResponse get(String url, String userName, String password) throws IOException {
        logger.debug("Fetching URL: " + url + " with username: " + userName);

        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope("www.commcarehq.org", 443, "DJANGO", "digest"),
                new UsernamePasswordCredentials(userName, password));
        HttpResponse response = httpClient.execute(new HttpGet(url));
        Header[] headers = response.getAllHeaders();

        CommCareHttpResponse commCareHttpResponse = new CommCareHttpResponse(response.getStatusLine().getStatusCode(), headers, new byte[0]);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            commCareHttpResponse = new CommCareHttpResponse(response.getStatusLine().getStatusCode(), headers, IOUtils.toByteArray(entity.getContent()));
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Got response for URL: " + url + ": " + commCareHttpResponse);
        }

        return commCareHttpResponse;
    }
}
