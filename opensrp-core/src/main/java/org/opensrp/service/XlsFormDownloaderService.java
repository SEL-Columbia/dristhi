package org.opensrp.service;

import java.io.IOException;

import org.codehaus.jackson.JsonProcessingException;
import org.opensrp.util.FileCreator;
import org.opensrp.util.JsonParser;
import org.opensrp.util.NetClientGet;
import org.springframework.stereotype.Service;

/**
 * @author muhammad.ahmed@ihsinformatics.com
 *  Created on 17-September, 2015
 */
@Service
public class XlsFormDownloaderService {
	private NetClientGet netClientGet;
	private FileCreator fileCreator;
	private JsonParser jsonParser;
	
	private byte[] formJson=null; 
	public XlsFormDownloaderService() {
	netClientGet=new NetClientGet();
	fileCreator=new FileCreator();
	
	jsonParser=new JsonParser();
	}
	
	public boolean downloadFormFiles(String directory,String username ,String formId, String formName ) throws IOException{
		
		String xmlData=netClientGet.convertToString("", username, formId);
	//	System.out.println("xml data"+xmlData);
		String modelData=fileCreator.prettyFormat(netClientGet.getModel(xmlData),2);
		String formData=fileCreator.prettyFormat(netClientGet.getForm(xmlData));
		formJson=netClientGet.downloadFile(username, formId);
	//	modelData=fileCreator.prettyFormat(modelData);
		//formData=fileCreator.prettyFormat(formData);
		
		return fileCreator.createFormFiles(fileCreator.osDirectorySet(directory)+formName, formId, formData.getBytes(), modelData.getBytes(), formJson);
		
	
	}
	
	public String getFormDefinition() throws JsonProcessingException, IOException{
		if(formJson==null){
			return "Data not found on server . Please retry again !";
			
		}
		return jsonParser.getFormDefinition(formJson);
		
	}
	
	
	
}
