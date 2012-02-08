package org.ei.commcare.listener.util;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CommCareHttpResponseTest {
    @Test
    public void shouldGiveEmptyValueIfTokenIsNotFound() {
        CommCareHttpResponse response = new CommCareHttpResponse(200, new Header[]{new BasicHeader("somename", "somevalue")}, "dont-care-about-this");
        assertThat(response.tokenForNextExport(), is(""));
    }

    @Test
    public void shouldFindTokenIfItExists() {
        CommCareHttpResponse response = response(200, "ThisIsItsValue");

        assertThat(response.tokenForNextExport(), is("ThisIsItsValue"));
    }

    @Test
    public void shouldConsiderAResponseAsValidIfStatusCodeIs200AndItHasATokenForNextExport() {
        assertThat(response(200, "SomeNonEmptyValue").isValid(), is(true));
        assertThat(response(200, "").isValid(), is(false));
        assertThat(response(302, "SomeNonEmptyValue").isValid(), is(false));
        assertThat(response(302, "").isValid(), is(false));
    }

    private CommCareHttpResponse response(int statusCode, String tokenValue) {
        return new CommCareHttpResponse(statusCode, new Header[]{
                new BasicHeader("somename", "somevalue"),
                new BasicHeader("X-CommCareHQ-Export-Token", tokenValue),
                new BasicHeader("X-Some-Other-Header", "SomeOtherValue")
        }, "dont-care-about-this");
    }
}
