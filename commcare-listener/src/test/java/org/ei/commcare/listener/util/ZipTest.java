package org.ei.commcare.listener.util;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ZipTest {
    @Test
    public void shouldListFileContentsInZip() throws IOException {
        byte[] bytesInZip = IOUtils.toByteArray(getClass().getResourceAsStream("/ZipOne.zip"));

        List<String> files = new Zip(bytesInZip).getFiles();

        assertThat(files, is(Arrays.asList("ZipFile1-File1-Contents\n", "ZipFile1-File2-Contents\n")));
    }
}
