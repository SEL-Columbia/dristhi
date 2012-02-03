package org.ei.commcare.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Zip
{
    public List<String> getFiles(byte[] zipFileContent) throws IOException {
        File temporaryFile = File.createTempFile("commcare-data", "zip");
        ZipFile zipFile = null;
        try {
            FileUtils.writeByteArrayToFile(temporaryFile, zipFileContent);
            zipFile = new ZipFile(temporaryFile);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            List<String> files = new ArrayList<String>();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                if (!zipEntry.isDirectory()) {
                    InputStream stream = zipFile.getInputStream(zipEntry);
                    files.add(IOUtils.toString(stream));
                    stream.close();
                }
            }

            return files;
        } finally {
            if (zipFile != null) {
                zipFile.close();
            }
            FileUtils.deleteQuietly(temporaryFile);
        }
    }
}
