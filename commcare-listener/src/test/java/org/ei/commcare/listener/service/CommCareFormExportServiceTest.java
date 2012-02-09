package org.ei.commcare.listener.service;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.ei.commcare.listener.dao.AllExportTokens;
import org.ei.commcare.listener.domain.CommcareForm;
import org.ei.commcare.listener.util.CommCareHttpClient;
import org.ei.commcare.listener.util.CommCareHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class CommCareFormExportServiceTest {
    @Mock
    private CommCareHttpClient httpClient;
    @Mock
    private CredentialsProvider provider;
    @Mock
    private HttpResponse response;
    @Mock
    private AllExportTokens allExportTokens;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldFetchOneFormWithTwoInstancesFromCommCare() throws Exception {
        String nameSpace = "http://openrosa.org/formdesigner/4FBE07FF-2434-40B3-B151-D2EBE2F4FB4F";
        String urlOfExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22" + nameSpace + "%22&format=json&previous_export=";
        Properties properties = new Properties();
        properties.setProperty(CommCareFormExportService.COMMCARE_EXPORT_DEFINITION_FILE, "/commcare-export.json");

        when(allExportTokens.findByNamespace(nameSpace)).thenReturn("");
        when(httpClient.get(urlOfExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse(200, "/form.1.dump.json", "NEW-TOKEN"));

        CommCareFormExportService formExportService = new CommCareFormExportService(httpClient, properties, allExportTokens);
        List<CommcareForm> forms = formExportService.fetchForms();

        verify(httpClient).get(urlOfExport, "someUser@gmail.com", "somePassword");
        assertEquals(2, forms.size());
        assertForm(forms.get(0), new String[]{"form-1-instance-1-value-1", "form-1-instance-1-value-2"}, "Registration");
        assertForm(forms.get(1), new String[]{"form-1-instance-2-value-1", "form-1-instance-2-value-2"}, "Registration");
    }

    @Test
    public void shouldFetchMultipleFormsWithMultipleInstancesFromCommCare() throws Exception {
        String nameSpaceOfFirstExport = "http://openrosa.org/formdesigner/UUID-OF-FIRST-FORM";
        String nameSpaceOfSecondExport = "http://openrosa.org/formdesigner/UUID-OF-SECOND-FORM";

        String urlOfFirstExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22" + nameSpaceOfFirstExport + "%22&format=json&previous_export=";
        String urlOfSecondExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22" + nameSpaceOfSecondExport + "%22&format=json&previous_export=";

        Properties properties = new Properties();
        properties.setProperty(CommCareFormExportService.COMMCARE_EXPORT_DEFINITION_FILE, "/commcare-export-with-two-urls.json");
        when(allExportTokens.findByNamespace(nameSpaceOfFirstExport)).thenReturn("");
        when(httpClient.get(urlOfFirstExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse(200, "/form.1.dump.json", "NEW-TOKEN"));

        when(allExportTokens.findByNamespace(nameSpaceOfSecondExport)).thenReturn("");
        when(httpClient.get(urlOfSecondExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse(200, "/form.2.dump.json", "NEW-TOKEN"));

        CommCareFormExportService formExportService = new CommCareFormExportService(httpClient, properties, allExportTokens);
        List<CommcareForm> forms = formExportService.fetchForms();

        verify(httpClient).get(urlOfFirstExport, "someUser@gmail.com", "somePassword");
        verify(httpClient).get(urlOfSecondExport, "someUser@gmail.com", "somePassword");
        verify(allExportTokens).updateToken(nameSpaceOfFirstExport, "NEW-TOKEN");
        verify(allExportTokens).updateToken(nameSpaceOfSecondExport, "NEW-TOKEN");

        assertEquals(4, forms.size());
        assertForm(forms.get(0), new String[]{"form-1-instance-1-value-1", "form-1-instance-1-value-2"}, "Registration");
        assertForm(forms.get(1), new String[]{"form-1-instance-2-value-1", "form-1-instance-2-value-2"}, "Registration");
        assertForm(forms.get(2), new String[]{"form-2-instance-1-value-1", "form-2-instance-1-value-2"}, "SomeOtherForm");
        assertForm(forms.get(3), new String[]{"form-2-instance-2-value-1", "form-2-instance-2-value-2"}, "SomeOtherForm");
    }

    @Test
    public void shouldUseURLWithoutPreviousTokenWhenThereIsNoToken() throws Exception {
        String nameSpace = "http://openrosa.org/formdesigner/4FBE07FF-2434-40B3-B151-D2EBE2F4FB4F";
        String urlOfExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22" + nameSpace + "%22&format=json&previous_export=";
        Properties properties = new Properties();
        properties.setProperty(CommCareFormExportService.COMMCARE_EXPORT_DEFINITION_FILE, "/commcare-export.json");

        when(allExportTokens.findByNamespace(nameSpace)).thenReturn("");
        when(httpClient.get(urlOfExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse(200, "/form.1.dump.json", "NEW-TOKEN"));

        CommCareFormExportService formExportService = new CommCareFormExportService(httpClient, properties, allExportTokens);
        formExportService.fetchForms();

        verify(httpClient).get(urlOfExport, "someUser@gmail.com", "somePassword");
    }

    @Test
    public void shouldUseURLWithPreviousTokenWhenThereIsAToken() throws Exception {
        String nameSpace = "http://openrosa.org/formdesigner/4FBE07FF-2434-40B3-B151-D2EBE2F4FB4F";
        String urlOfExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22" + nameSpace + "%22&format=json&previous_export=OLD-TOKEN";
        Properties properties = new Properties();
        properties.setProperty(CommCareFormExportService.COMMCARE_EXPORT_DEFINITION_FILE, "/commcare-export.json");

        when(allExportTokens.findByNamespace(nameSpace)).thenReturn("OLD-TOKEN");
        when(httpClient.get(urlOfExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse(200, "/form.1.dump.json", "NEW-TOKEN"));

        CommCareFormExportService formExportService = new CommCareFormExportService(httpClient, properties, allExportTokens);
        formExportService.fetchForms();

        verify(httpClient).get(urlOfExport, "someUser@gmail.com", "somePassword");
    }

    @Test
    public void shouldSaveTheExportTokenAfterFetchingData() throws Exception {
        String nameSpace = "http://openrosa.org/formdesigner/4FBE07FF-2434-40B3-B151-D2EBE2F4FB4F";
        String urlOfExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22" + nameSpace + "%22&format=json&previous_export=OLD-TOKEN";
        Properties properties = new Properties();
        properties.setProperty(CommCareFormExportService.COMMCARE_EXPORT_DEFINITION_FILE, "/commcare-export.json");

        when(allExportTokens.findByNamespace(nameSpace)).thenReturn("OLD-TOKEN");
        when(httpClient.get(urlOfExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse(200, "/form.1.dump.json", "NEW-TOKEN"));

        CommCareFormExportService formExportService = new CommCareFormExportService(httpClient, properties, allExportTokens);
        formExportService.fetchForms();

        verify(allExportTokens).updateToken(nameSpace, "NEW-TOKEN");
    }

    @Test
    public void shouldNotProcessFormOrUpdateTokenWhenResponseSaysThatThereIsNoNewData() throws Exception {
        String nameSpace = "http://openrosa.org/formdesigner/4FBE07FF-2434-40B3-B151-D2EBE2F4FB4F";
        String urlOfExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22" + nameSpace + "%22&format=json&previous_export=OLD-TOKEN";
        Properties properties = new Properties();
        properties.setProperty(CommCareFormExportService.COMMCARE_EXPORT_DEFINITION_FILE, "/commcare-export.json");

        when(allExportTokens.findByNamespace(nameSpace)).thenReturn("OLD-TOKEN");
        when(httpClient.get(urlOfExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse(302, "/form.with.empty.data.json", null));

        CommCareFormExportService formExportService = new CommCareFormExportService(httpClient, properties, allExportTokens);
        List<CommcareForm> forms = formExportService.fetchForms();

        assertThat(forms.size(), is(0));
        verify(allExportTokens).findByNamespace(nameSpace);
        verifyNoMoreInteractions(allExportTokens);
    }

    private CommCareHttpResponse formResponse(int statusCode, String jsonDump, String newTokenValue) throws IOException {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("X-Abc-Header", "Def"));

        if (newTokenValue != null) {
            headers.add(new BasicHeader("X-CommCareHQ-Export-Token", newTokenValue));
        }

        return new CommCareHttpResponse(statusCode, headers.toArray(new Header[0]), IOUtils.toString(getClass().getResourceAsStream(jsonDump)));
    }

    private void assertForm(CommcareForm actualForm, String[] expectedValuesOfForm, String formName) {
        assertEquals(actualForm.definition().name(), formName);

        HashMap<String, String> mapping = new HashMap<String, String>();
        mapping.put("header.col.1", "FirstValue");
        mapping.put("header.col.2", "SecondValue");
        Map<String,String> data = actualForm.content().getValuesOfFieldsSpecifiedByPath(mapping);

        assertEquals(2, data.size());
        assertEquals(expectedValuesOfForm[0], data.get("FirstValue"));
        assertEquals(expectedValuesOfForm[1], data.get("SecondValue"));
    }
}
