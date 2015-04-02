package org.opensrp.web.controller;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;

public class FormDownloadTest 
{
	@Mock
	MockHttpServletResponse resp = new MockHttpServletResponse();
	
	@Test
	public void shouldReturnAvailableVersion() throws URISyntaxException, IOException
	{
        String filename = "form/";

		FormDownLoadController formDownLoadController = new FormDownLoadController(filename, "model.xml,form.xml,form_definition.json");
		
		String versions = formDownLoadController.getAllAvailableVersion();
		System.out.println("available version:::"+versions);
		readZip(new ByteArrayInputStream(formDownLoadController.getForm("delivery_outcome", resp)));
	}	
	
	public void readZip(InputStream fip) throws IOException {
        // create a buffer to improve copy performance later.
        byte[] buffer = new byte[2048];

        // open the zip file stream
        ZipInputStream stream = new ZipInputStream(fip);
        String outdir = "g:\\";

        try
        {
            // now iterate through each item in the stream. The get next
            // entry call will return a ZipEntry for each file in the stream
            ZipEntry entry;
            while((entry = stream.getNextEntry())!=null)
            {
                String s = String.format("Entry: %s len %d added %TD", entry.getName(), entry.getSize(), new Date(entry.getTime()));
                System.out.println(s);

                // Once we get the entry from the stream, the stream is
                // positioned read to read the raw data, and we keep
                // reading until read returns 0 or less.
                String outpath = outdir + "/" + entry.getName();
                FileOutputStream output = null;
                try
                {
                    output = new FileOutputStream(outpath);
                    int len = 0;
                    while ((len = stream.read(buffer)) > 0)
                    {
                        output.write(buffer, 0, len);
                    }
                }
                finally
                {
                    // we must always close the output file
                    if(output!=null) output.close();
                }
            }
        }
        finally
        {
            // we must always close the zip file.
            stream.close();
        }
	  }
}
