package org.opensrp.service;



import httpdowload.JustForFun;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.jackson.JsonProcessingException;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Years;
import org.opensrp.util.FileCreator;
import org.opensrp.util.JsonParser;
import org.opensrp.util.NetClientGet;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

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

	public static void main(String[] args) {
		try {
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "vaccine_stock_position", "115142");
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "child_vaccination_enrollment", "115140");
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "child_vaccination_followup", "115139");
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "offsite_child_vaccination_followup", "115138");
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "woman_tt_enrollement_form", "115137");
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "woman_tt_followup_form", "115136");
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "offsite_woman_followup_form", "115135");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String formatXML(String input)
    {
        try
        {
            final InputSource src = new InputSource(new StringReader(input));
            final Node document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(src).getDocumentElement();

            final DOMImplementationRegistry registry = DOMImplementationRegistry
                    .newInstance();
            final DOMImplementationLS impl = (DOMImplementationLS) registry
                    .getDOMImplementation("LS");
            final LSSerializer writer = impl.createLSSerializer();

            writer.getDomConfig().setParameter("format-pretty-print",
                    Boolean.TRUE);
            writer.getDomConfig().setParameter("xml-declaration", false);

            return writer.writeToString(document);
        } catch (Exception e)
        {
            e.printStackTrace();
            return input;
        }
    }
	
	public boolean downloadFormFiles(String directory,String username ,String formPath, String password,String formId, String formPk) throws IOException{
		
		String xmlData=netClientGet.convertToString("", formPath, formId);
		String modelData=fileCreator.prettyFormat(netClientGet.getModel(xmlData));
		String formData=fileCreator.prettyFormat(netClientGet.getForm(xmlData));
		formData = formData.replaceAll("selected\\(", "contains(");
		formData = formData.replaceAll("<span.*lang=\"openmrs_code\".*</span>", "");
		formData = formData.replaceAll("<option value=\"openmrs_code\">openmrs_code</option>", "");
		
		formJson=netClientGet.downloadJson(username,password,  formPk);
		modelData=formatXML(modelData);
		//formData=fileCreator.prettyFormat(formData);
		System.out.println(getFormDefinition());
		fileCreator.createFile("form_definition.json", fileCreator.osDirectorySet(directory)+formId, getFormDefinition().getBytes());
		return fileCreator.createFormFiles(fileCreator.osDirectorySet(directory)+formId, formId, formData.getBytes(), modelData.getBytes(), formJson);
	}
	
	public String getFormDefinition() throws JsonProcessingException, IOException{
		if(formJson==null){
			return "Data not found on server . Please retry again !";
			
		}
		return jsonParser.getFormDefinition(formJson);
		
	}	
}
