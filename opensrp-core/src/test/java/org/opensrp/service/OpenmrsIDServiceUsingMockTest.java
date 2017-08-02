package org.opensrp.service;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EntityUtils.class)
@PowerMockIgnore({"org.apache.http.ssl.*", "javax.net.ssl.*","org.apache.http.conn.ssl.*"})
public class OpenmrsIDServiceUsingMockTest {


    OpenmrsIDService openmrsIDService;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(EntityUtils.class);

    }

    @Ignore@Test
    public void testDownloadsOpenmrsIds() throws IOException {
        openmrsIDService = OpenmrsIDService.createInstanceWithOpenMrsUrl("http://localhost/");
        String jsonResponse = "{\"identifiers\":[\"1\",\"2\"]}";
        BDDMockito.when(EntityUtils.toString(any(HttpEntity.class))).thenReturn(jsonResponse);
        List<String> ids = openmrsIDService.downloadOpenmrsIds(2);
        assertEquals(2, ids.size());
        assertTrue(ids.contains("1"));
        assertTrue(ids.contains("2"));
        assertFalse(ids.contains("3"));
    }

    @Ignore@Test
    public void testDownloadOpenmrsIdsWithInvalidUrl() {
        openmrsIDService = OpenmrsIDService.createInstanceWithOpenMrsUrl("");
         List<String> ids = openmrsIDService.downloadOpenmrsIds(2);
        assertNull(ids);
    }
}
