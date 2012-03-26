package org.ei.commcare.api.service;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.ei.commcare.api.domain.CommcareFormInstance;
import org.ei.commcare.api.domain.ExportToken;
import org.ei.commcare.api.repository.AllExportTokens;
import org.ei.commcare.api.util.CommCareHttpClient;
import org.ei.commcare.api.util.CommCareHttpResponse;
import org.ei.commcare.api.util.CommCareImportProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.ei.commcare.api.util.CommCareImportProperties.COMMCARE_IMPORT_DEFINITION_FILE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class CommCareFormImportServiceTest {
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
        properties.setProperty(COMMCARE_IMPORT_DEFINITION_FILE, "/test-data/commcare-export.json");

        when(allExportTokens.findByNameSpace(nameSpace)).thenReturn(new ExportToken(nameSpace, ""));
        when(httpClient.get(urlOfExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse(200, "/test-data/form.1.dump.json", "NEW-TOKEN"));

        CommCareFormImportService formImportService = new CommCareFormImportService(allExportTokens, httpClient, new CommCareImportProperties(properties));
        List<CommcareFormInstance> formInstances = formImportService.fetchForms();

        verify(httpClient).get(urlOfExport, "someUser@gmail.com", "somePassword");
        assertEquals(2, formInstances.size());
        assertForm(formInstances.get(0), new String[]{"form-1-instance-1-value-1", "form-1-instance-1-value-2"}, "Registration");
        assertForm(formInstances.get(1), new String[]{"form-1-instance-2-value-1", "form-1-instance-2-value-2"}, "Registration");
    }

    @Test
    public void shouldFetchMultipleFormsWithMultipleInstancesFromCommCare() throws Exception {
        String nameSpaceOfFirstExport = "http://openrosa.org/formdesigner/UUID-OF-FIRST-FORM";
        String nameSpaceOfSecondExport = "http://openrosa.org/formdesigner/UUID-OF-SECOND-FORM";

        String urlOfFirstExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22" + nameSpaceOfFirstExport + "%22&format=json&previous_export=";
        String urlOfSecondExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22" + nameSpaceOfSecondExport + "%22&format=json&previous_export=";

        Properties properties = new Properties();
        properties.setProperty(COMMCARE_IMPORT_DEFINITION_FILE, "/test-data/commcare-export-with-two-urls.json");
        when(allExportTokens.findByNameSpace(nameSpaceOfFirstExport)).thenReturn(new ExportToken(nameSpaceOfFirstExport, ""));
        when(httpClient.get(urlOfFirstExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse(200, "/test-data/form.1.dump.json", "NEW-TOKEN"));

        when(allExportTokens.findByNameSpace(nameSpaceOfSecondExport)).thenReturn(new ExportToken(nameSpaceOfSecondExport, ""));
        when(httpClient.get(urlOfSecondExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse(200, "/test-data/form.2.dump.json", "NEW-TOKEN"));

        CommCareFormImportService formImportService = new CommCareFormImportService(allExportTokens, httpClient, new CommCareImportProperties(properties));
        List<CommcareFormInstance> formInstances = formImportService.fetchForms();

        verify(httpClient).get(urlOfFirstExport, "someUser@gmail.com", "somePassword");
        verify(httpClient).get(urlOfSecondExport, "someUser@gmail.com", "somePassword");
        verify(allExportTokens).updateToken(nameSpaceOfFirstExport, "NEW-TOKEN");
        verify(allExportTokens).updateToken(nameSpaceOfSecondExport, "NEW-TOKEN");

        assertEquals(4, formInstances.size());
        assertForm(formInstances.get(0), new String[]{"form-1-instance-1-value-1", "form-1-instance-1-value-2"}, "Registration");
        assertForm(formInstances.get(1), new String[]{"form-1-instance-2-value-1", "form-1-instance-2-value-2"}, "Registration");
        assertForm(formInstances.get(2), new String[]{"form-2-instance-1-value-1", "form-2-instance-1-value-2"}, "SomeOtherForm");
        assertForm(formInstances.get(3), new String[]{"form-2-instance-2-value-1", "form-2-instance-2-value-2"}, "SomeOtherForm");
    }

    @Test
    public void shouldUseURLWithoutPreviousTokenWhenThereIsNoToken() throws Exception {
        String nameSpace = "http://openrosa.org/formdesigner/4FBE07FF-2434-40B3-B151-D2EBE2F4FB4F";
        String urlOfExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22" + nameSpace + "%22&format=json&previous_export=";
        Properties properties = new Properties();
        properties.setProperty(COMMCARE_IMPORT_DEFINITION_FILE, "/test-data/commcare-export.json");

        when(allExportTokens.findByNameSpace(nameSpace)).thenReturn(new ExportToken(nameSpace, ""));
        when(httpClient.get(urlOfExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse(200, "/test-data/form.1.dump.json", "NEW-TOKEN"));

        CommCareFormImportService formImportService = new CommCareFormImportService(allExportTokens, httpClient, new CommCareImportProperties(properties));
        formImportService.fetchForms();

        verify(httpClient).get(urlOfExport, "someUser@gmail.com", "somePassword");
    }

    @Test
    public void shouldUseURLWithPreviousTokenWhenThereIsAToken() throws Exception {
        String nameSpace = "http://openrosa.org/formdesigner/4FBE07FF-2434-40B3-B151-D2EBE2F4FB4F";
        String urlOfExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22" + nameSpace + "%22&format=json&previous_export=OLD-TOKEN";
        Properties properties = new Properties();
        properties.setProperty(COMMCARE_IMPORT_DEFINITION_FILE, "/test-data/commcare-export.json");

        when(allExportTokens.findByNameSpace(nameSpace)).thenReturn(new ExportToken(nameSpace, "OLD-TOKEN"));
        when(httpClient.get(urlOfExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse(200, "/test-data/form.1.dump.json", "NEW-TOKEN"));

        CommCareFormImportService formImportService = new CommCareFormImportService(allExportTokens, httpClient, new CommCareImportProperties(properties));
        formImportService.fetchForms();

        verify(httpClient).get(urlOfExport, "someUser@gmail.com", "somePassword");
    }

    @Test
    public void shouldSaveTheExportTokenAfterFetchingData() throws Exception {
        String nameSpace = "http://openrosa.org/formdesigner/4FBE07FF-2434-40B3-B151-D2EBE2F4FB4F";
        String urlOfExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22" + nameSpace + "%22&format=json&previous_export=OLD-TOKEN";
        Properties properties = new Properties();
        properties.setProperty(COMMCARE_IMPORT_DEFINITION_FILE, "/test-data/commcare-export.json");

        when(allExportTokens.findByNameSpace(nameSpace)).thenReturn(new ExportToken(nameSpace, "OLD-TOKEN"));
        when(httpClient.get(urlOfExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse(200, "/test-data/form.1.dump.json", "NEW-TOKEN"));

        CommCareFormImportService formImportService = new CommCareFormImportService(allExportTokens, httpClient, new CommCareImportProperties(properties));
        formImportService.fetchForms();

        verify(allExportTokens).updateToken(nameSpace, "NEW-TOKEN");
    }

    @Test
    public void shouldNotProcessFormOrUpdateTokenWhenResponseSaysThatThereIsNoNewData() throws Exception {
        String nameSpace = "http://openrosa.org/formdesigner/4FBE07FF-2434-40B3-B151-D2EBE2F4FB4F";
        String urlOfExport = "https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22" + nameSpace + "%22&format=json&previous_export=OLD-TOKEN";
        Properties properties = new Properties();
        properties.setProperty(COMMCARE_IMPORT_DEFINITION_FILE, "/test-data/commcare-export.json");

        when(allExportTokens.findByNameSpace(nameSpace)).thenReturn(new ExportToken(nameSpace, "OLD-TOKEN"));
        when(httpClient.get(urlOfExport, "someUser@gmail.com", "somePassword")).thenReturn(formResponse(302, "/test-data/form.with.empty.data.json", null));

        CommCareFormImportService formImportService = new CommCareFormImportService(allExportTokens, httpClient, new CommCareImportProperties(properties));
        List<CommcareFormInstance> formInstances = formImportService.fetchForms();

        assertThat(formInstances.size(), is(0));
        verify(allExportTokens).findByNameSpace(nameSpace);
        verifyNoMoreInteractions(allExportTokens);
    }

    private CommCareHttpResponse formResponse(int statusCode, String jsonDump, String newTokenValue) throws IOException {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("X-Abc-Header", "Def"));

        if (newTokenValue != null) {
            headers.add(new BasicHeader("X-CommCareHQ-Export-Token", newTokenValue));
        }

        return new CommCareHttpResponse(statusCode, headers.toArray(new Header[0]), IOUtils.toByteArray(getClass().getResourceAsStream(jsonDump)));
    }

    private void assertForm(CommcareFormInstance actualFormInstance, String[] expectedValuesOfForm, String formName) {
        assertEquals(actualFormInstance.definition().name(), formName);

        Map<String,String> data = actualFormInstance.content();

        assertEquals(2, data.size());
        assertThat(data.get("FieldInOutput"), is(expectedValuesOfForm[0]));
        assertThat(data.get("AnotherFieldInOutput"), is(expectedValuesOfForm[1]));
    }
}
