package org.opensrp.web.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("form/")
public class FormDownLoadController {

	private String FORMS_DIR;
	private String FORMS_DEFINITION_FILE_NAME = "form_definition.json";
	private static final String DS = "/"; 
	private String FILES_TO_DOWNLOAD;
	
	@Autowired
	public FormDownLoadController(@Value("#{opensrp['form.directory.name']}") String FORMS_DIR, 
			@Value("#{opensrp['form.download.files']}") String FILES_TO_DOWNLOAD) throws IOException 
	{
		ResourceLoader loader=new DefaultResourceLoader();
		this.FORMS_DIR = loader.getResource(FORMS_DIR).getURI().getPath();
		this.FILES_TO_DOWNLOAD = FILES_TO_DOWNLOAD.replace(" ", "");
		System.out.println(FORMS_DIR);
	}

	@RequestMapping("latest-form-versions")
	public @ResponseBody String getAllAvailableVersion() {
		return readFormDefinitionFromDirectories();
	}

	/*
	 * Service method transfer file as a byte stream
	 * */
	@RequestMapping("form-files")
	public @ResponseBody byte[] getForm(@RequestParam(value="formDirName") String formDirName, HttpServletResponse resp) {
		resp.setContentType("application/zip");
		resp.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"",formDirName+".zip"));
	    String filePath =  ((FORMS_DIR.endsWith("/")||FORMS_DIR.endsWith("\\"))?FORMS_DIR:(FORMS_DIR+"/")) + formDirName;   
	    try {
	        File file = new File(filePath);
	        return zipFiles(file);
	    } catch (IOException ex) {
	        System.err.println(ex);
	        throw new WebServiceException(ex);
	    }      
	}
    /**
     * Compress the given directory with all its files.
     */
    private byte[] zipFiles(File directory) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        byte bytes[] = new byte[2048];
 
        String[] fl = directory.list();
        for (String fileName : fl) {
        	if(FILES_TO_DOWNLOAD.matches("(.+,)?"+fileName+"(,.+)?$")){
	            FileInputStream fis = new FileInputStream(directory.getPath() + DS + fileName);
	            BufferedInputStream bis = new BufferedInputStream(fis);
	 
	            zos.putNextEntry(new ZipEntry(fileName));
	 
	            int bytesRead;
	            while ((bytesRead = bis.read(bytes)) != -1) {
	                zos.write(bytes, 0, bytesRead);
	            }
	            zos.closeEntry();
	            bis.close();
	            fis.close();
        	}
        }
        zos.flush();
        baos.flush();
        zos.close();
        baos.close();
 
        return baos.toByteArray();
    }	
    
	/*
	 * Utility method read Forms directories 
	 * recursively find name of available forms 
	 * and their respective versions and then 
	 * return as a json string
	 * */
	private String  readFormDefinitionFromDirectories(){
	    String formName_Version_DefinitionJson = "{\"formVersions\" : [";
	    String formVersionDefinition = "";
	    File file = new File(FORMS_DIR);
	    File[] files = file.listFiles();
	    for (int i = 0; i < files.length; i++) {
	    	if(new File(files[i].getPath()).isDirectory()){
		        formVersionDefinition = getFormName_Definition(files[i].getPath()+"/"+FORMS_DEFINITION_FILE_NAME, files[i].getName());        
		        if (files.length > 0 && i< files.length-1){
		            formName_Version_DefinitionJson +=  formVersionDefinition + ",";
		        }else{
		            formName_Version_DefinitionJson +=  formVersionDefinition;
		        }
	    	}
	    }
	    formName_Version_DefinitionJson += "]}";
	    return formName_Version_DefinitionJson;
	}
	/*
	 * Utility method read form definition json
	 * find its name and version 
	 * and return it as a json string
	 * */
	private String getFormName_Definition(String filePath, String formDirName) {
	    String formName = "";
	    String versionNo = "-1";
	    String fieldname = "";
	    JsonFactory jfactory = new JsonFactory();       
	    JsonParser jParser;
	    try {
	        jParser = jfactory.createJsonParser(new File(filePath));
	        while (jParser.nextToken() != JsonToken.END_OBJECT) {
	
	            fieldname = jParser.getCurrentName();
	            if ("form_data_definition_version".equals(fieldname)) {
	                jParser.nextToken();
	                versionNo = jParser.getText();
	            }
	            if ("form".equals(fieldname)) {
	                while (jParser.nextToken() != JsonToken.FIELD_NAME) {
	                    jParser.nextToken();
	                    fieldname = jParser.getCurrentName();
	                    if ("default_bind_path".equals(fieldname)) {
	                        jParser.nextToken();
	                        formName = jParser.getText().substring(16,
	                                jParser.getText().length() - 1);
	                    }
	                }
	
	            }
	        }
	        jParser.close();
	    } catch (JsonParseException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return "{\"formName\": \"" + formName + "\", \"formDirName\": \"" + formDirName + "\", \"formDataDefinitionVersion\": \""
	            + versionNo + "\"}";
	}
}
