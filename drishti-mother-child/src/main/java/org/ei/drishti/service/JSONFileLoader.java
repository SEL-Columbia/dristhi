package org.ei.drishti.service;

import org.apache.commons.io.IOUtils;

import java.io.*;

public class JSONFileLoader {
    public String loadFormData(String fileName) throws IOException {
        File file = new File("drishti-mother-child/form/" + fileName);
        return load(file);
    }

    public String load(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        return IOUtils.toString(inputStream, "UTF-8");
    }
}
