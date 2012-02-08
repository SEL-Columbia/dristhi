package org.ei.commcare.listener.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.ei.commcare.listener.domain.CommcareForm;
import org.ei.commcare.listener.util.CommcareHttpClient;
import org.ei.commcare.listener.util.CommcareHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class CommCareFormExportServiceTest {
    @Mock
    private CommcareHttpClient httpClient;
    @Mock
    private CredentialsProvider provider;
    @Mock
    private HttpResponse response;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldFetchOneXMLFromCommCare() throws Exception {
        String urlOfExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22http://openrosa.org/formdesigner/4FBE07FF-2434-40B3-B151-D2EBE2F4FB4F%22&format=json";
        Properties properties = new Properties();
        properties.setProperty(CommCareFormExportService.COMMCARE_EXPORT_DEFINITION_FILE, "/commcare-export.json");

        when(httpClient.get(urlOfExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse("/form.1.dump.json"));

        CommCareFormExportService formExportService = new CommCareFormExportService(httpClient, properties);
        List<CommcareForm> forms = formExportService.fetchForms();

        verify(httpClient).get(urlOfExport, "someUser@gmail.com", "somePassword");
        assertEquals(2, forms.size());
        assertForm(forms.get(0), new String[]{"form-1-instance-1-value-1", "form-1-instance-1-value-2"}, urlOfExport);
        assertForm(forms.get(1), new String[]{"form-1-instance-2-value-1", "form-1-instance-2-value-2"}, urlOfExport);
    }

    @Test
    public void shouldFetchMultipleXMLsFromCommCare() throws Exception {
        String urlOfFirstExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22http://openrosa.org/formdesigner/UUID-OF-FIRST-FORM%22&format=json";
        String urlOfSecondExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22http://openrosa.org/formdesigner/UUID-OF-SECOND-FORM%22&format=json";

        Properties properties = new Properties();
        properties.setProperty(CommCareFormExportService.COMMCARE_EXPORT_DEFINITION_FILE, "/commcare-export-with-two-urls.json");
        when(httpClient.get(urlOfFirstExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse("/form.1.dump.json"));
        when(httpClient.get(urlOfSecondExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse("/form.2.dump.json"));

        CommCareFormExportService formExportService = new CommCareFormExportService(httpClient, properties);
        List<CommcareForm> forms = formExportService.fetchForms();

        verify(httpClient).get(urlOfFirstExport, "someUser@gmail.com", "somePassword");
        verify(httpClient).get(urlOfSecondExport, "someUser@gmail.com", "somePassword");
        assertEquals(4, forms.size());
        assertForm(forms.get(0), new String[]{"form-1-instance-1-value-1", "form-1-instance-1-value-2"}, urlOfFirstExport);
        assertForm(forms.get(1), new String[]{"form-1-instance-2-value-1", "form-1-instance-2-value-2"}, urlOfFirstExport);
        assertForm(forms.get(2), new String[]{"form-2-instance-1-value-1", "form-2-instance-1-value-2"}, urlOfSecondExport);
        assertForm(forms.get(3), new String[]{"form-2-instance-2-value-1", "form-2-instance-2-value-2"}, urlOfSecondExport);
    }

    private CommcareHttpResponse formResponse(String jsonDump) throws IOException {
        return new CommcareHttpResponse(new Header[] {new BasicHeader("X-Abc-Header", "Def")},
                IOUtils.toString(getClass().getResourceAsStream(jsonDump)));
    }

    private void assertForm(CommcareForm actualForm, String[] expectedValuesOfForm, String urlOfExport) {
        Type typeOfMapForJson = new TypeToken<Map<String, List<String>>>() { }.getType();

        String expectedJson = "{ \"headers\" : [\"header.col.1\", \"header.col.2\"], \"values\" : " + Arrays.toString(expectedValuesOfForm) + " }";

        Map<String, String[]> actualContent = new Gson().fromJson(actualForm.content(), typeOfMapForJson);
        Map<String, String[]> expectedContent = new Gson().fromJson(expectedJson, typeOfMapForJson);

        assertEquals(urlOfExport, actualForm.definition().url());
        assertEquals(expectedContent, actualContent);
    }
}
