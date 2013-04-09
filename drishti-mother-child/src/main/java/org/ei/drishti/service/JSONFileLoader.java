package org.ei.drishti.service;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class JSONFileLoader {
    private String formFilePath;

    @Autowired
    public JSONFileLoader(@Value("#{drishti['form.files.path']}") String formFilePath) {
        this.formFilePath = formFilePath;
    }

    public String loadFormData(String fileName) throws IOException {
        File file = new File(formFilePath + fileName);
        return load(file);
    }

    public String load(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        return IOUtils.toString(inputStream, "UTF-8");
    }
}
