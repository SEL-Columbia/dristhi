package org.ei.commcare;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.ei.commcare.domain.CommcareForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class CommCareListenerTest {
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
        String urlOfExport = "https://www.commcarehq.org/a/abhilasha/reports/export/&export_tag=%22http://openrosa.org/formdesigner/4FBE07FF-2434-40B3-B151-D2EBE2F4FB4F%22&format=raw";

        Properties properties = new Properties();
        properties.setProperty(CommCareListener.COMMCARE_EXPORT_DEFINITION_FILE, "/commcare-export.json");
        when(httpClient.get(urlOfExport, "someUser@gmail.com", "somePassword")).thenReturn(getFileAsBytes("/zipOne.zip"));

        CommCareListener careListener = new CommCareListener(httpClient, properties);
        List<CommcareForm> actualContent = careListener.fetchForms();

        verify(httpClient).get(urlOfExport, "someUser@gmail.com", "somePassword");
        assertEquals(1, actualContent.size());

        CommcareForm actualForm = actualContent.get(0);
        assertEquals(Arrays.asList("ZipFile1-File1-Contents\n", "ZipFile1-File2-Contents\n"), actualForm.instances());
        assertEquals(urlOfExport, actualForm.definition().url());
    }

    @Test
    public void shouldFetchMultipleXMLsFromCommCare() throws Exception {
        String urlOfFirstExport = "https://www.commcarehq.org/a/abhilasha/reports/export/&export_tag=%22http://openrosa.org/formdesigner/UUID-OF-FIRST-FORM%22&format=raw";
        String urlOfSecondExport = "https://www.commcarehq.org/a/abhilasha/reports/export/&export_tag=%22http://openrosa.org/formdesigner/UUID-OF-SECOND-FORM%22&format=raw";

        Properties properties = new Properties();
        properties.setProperty(CommCareListener.COMMCARE_EXPORT_DEFINITION_FILE, "/commcare-export-with-two-urls.json");
        when(httpClient.get(urlOfFirstExport, "someUser@gmail.com", "somePassword")).thenReturn(getFileAsBytes("/zipOne.zip"));
        when(httpClient.get(urlOfSecondExport, "someUser@gmail.com", "somePassword")).thenReturn(getFileAsBytes("/zipTwo.zip"));

        CommCareListener careListener = new CommCareListener(httpClient, properties);
        List<CommcareForm> actualContent = careListener.fetchForms();

        verify(httpClient).get(urlOfFirstExport, "someUser@gmail.com", "somePassword");
        verify(httpClient).get(urlOfSecondExport, "someUser@gmail.com", "somePassword");
        assertEquals(2, actualContent.size());

        assertEquals(Arrays.asList("ZipFile1-File1-Contents\n", "ZipFile1-File2-Contents\n"), actualContent.get(0).instances());
        assertEquals(urlOfFirstExport, actualContent.get(0).definition().url());

        assertEquals(Arrays.asList("ZipFile2-File1-Contents\n", "ZipFile2-File2-Contents\n"), actualContent.get(1).instances());
        assertEquals(urlOfFirstExport, actualContent.get(0).definition().url());
    }

    private byte[] getFileAsBytes(String fileNameOnClassPath) throws Exception {
        return IOUtils.toByteArray(getClass().getResourceAsStream(fileNameOnClassPath));
    }
}
