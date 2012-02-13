package org.ei.commcare.api.util;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CommCareHttpResponseTest {
    @Test
    public void shouldGiveEmptyValueIfTokenIsNotFound() {
        CommCareHttpResponse response = new CommCareHttpResponse(200, new Header[]{new BasicHeader("somename", "somevalue")}, new byte[] {'A', 'B', 'C'});
        assertThat(response.tokenForNextExport(), is(""));
    }

    @Test
    public void shouldFindTokenIfItExists() {
        CommCareHttpResponse response = response(200, "ThisIsItsValue");

        assertThat(response.tokenForNextExport(), is("ThisIsItsValue"));
    }

    @Test
    public void shouldConsiderAResponseAsValidIfStatusCodeIs200AndItHasATokenForNextExport() {
        assertThat(response(200, "SomeNonEmptyValue").hasValidExportToken(), is(true));
        assertThat(response(200, "").hasValidExportToken(), is(false));
        assertThat(response(302, "SomeNonEmptyValue").hasValidExportToken(), is(false));
        assertThat(response(302, "").hasValidExportToken(), is(false));
    }

    private CommCareHttpResponse response(int statusCode, String tokenValue) {
        return new CommCareHttpResponse(statusCode, new Header[]{
                new BasicHeader("somename", "somevalue"),
                new BasicHeader("X-CommCareHQ-Export-Token", tokenValue),
                new BasicHeader("X-Some-Other-Header", "SomeOtherValue")
        }, new byte[] {'A', 'B', 'C'});
    }
}
