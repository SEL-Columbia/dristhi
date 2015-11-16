package org.opensrp.form.service;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.opensrp.form.domain.FormSubmission;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;


public class TestResourceLoader {
	protected String formDirPath;


	public TestResourceLoader() throws IOException {
		formDirPath = "/form";
	}
	
	protected FormSubmission getFormSubmissionFor(String formName, Integer number) throws JsonIOException, IOException{
		ResourceLoader loader=new DefaultResourceLoader();
		String path = loader.getResource(formDirPath).getURI().getPath();
		File fsfile = new File(path+"/"+formName+"/form_submission"+(number==null?"":number)+".json");
		return new Gson().fromJson(new FileReader(fsfile), FormSubmission.class);		
	}
	
	protected FormSubmission getFormSubmissionFor(String formName) throws JsonIOException, IOException{
		return getFormSubmissionFor(formName, null);		
	}
}
