package org.opensrp.connector;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.opensrp.form.domain.FormSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Component;

@Component
public class FormAttributeMapper {
	
	private String jsonFilePath;
	private String xmlFilePath;
	@Autowired
	public FormAttributeMapper(@Value("#{opensrp['form.def.json.file.path']}") String jsonFilePath,
			@Value("#{opensrp['model.xml.file.path']}") String xmlFilePath)
	{
		this.jsonFilePath = jsonFilePath;
		this.xmlFilePath = xmlFilePath;
	}
	public Map<String, String> getAttributesForField (String fieldName,FormSubmission formSubmission){
	
		String formBindForField = "";
		Node tagAndAttributes = null;
		// From form_definition.json
		formBindForField = getPropertyBindFromFormDefinition(fieldName, formSubmission); 
		// xpath in model.xml
		if(formBindForField!=null && !formBindForField.equals("null") && formBindForField.length()>0)
		{
			tagAndAttributes = getFormPropertyNameForAttribute(formBindForField,formSubmission);
		}

	    //Map <attribute,attributeValue> map from attributes in tagAndAttributes;
	    Map<String,String> attributeMap = new HashMap<>();
	    Node attributeNode = null;
	    
	    if(tagAndAttributes!=null)
	    {
	    	NamedNodeMap attributes = tagAndAttributes.getAttributes();
	    	for(int i=0;i<attributes.getLength();i++)
	    	{
	    		attributeNode = attributes.item(i);
	    		attributeMap.put(attributeNode.getNodeName(), attributeNode.getNodeValue());
	    	}
	    }
	    
	    return attributeMap; 
	}
	/*
	 * read form_definition.json for given formSubmission from disk 
	 * bindAttribute = get bind attribute for provided fieldName
	 */
	String getPropertyBindFromFormDefinition(String fieldName,FormSubmission formSubmission)
	{		
		String bindAttribute = "";
		String formName = formSubmission.formName();
		JsonParser parser = new JsonParser();
		String filePath = this.jsonFilePath+"/"+formName+"/form_definition.json";
		try
		{
			Object obj = parser.parse(new FileReader(filePath));
			JsonObject jsonObject = (JsonObject)obj;
			JsonElement formElement = jsonObject.get("form");
			JsonElement subformElement = null;
			JsonObject fields = null;
			JsonObject individualField = null;
			JsonElement individualBindObj = null;
			JsonElement individualNameObj = null;
			JsonArray fieldArray = null;
			JsonArray subFormArray = null;
			if(formElement!=null)
			{
				fields = formElement.getAsJsonObject();
			
				for(Entry<String, JsonElement> element:fields.entrySet())
				{
					if(element.getKey().equalsIgnoreCase("fields"))
					{
						fieldArray = element.getValue().getAsJsonArray();
					}
					if(element.getKey().equalsIgnoreCase("sub_forms"))
					{						
						subFormArray = element.getValue().getAsJsonArray();
					}
				}
				for(JsonElement fieldElement:fieldArray)
				{
					individualField = fieldElement.getAsJsonObject();
					individualBindObj = individualField.get("bind");
					individualNameObj = individualField.get("name");
					if(individualBindObj!=null)
					{
						if(individualNameObj.getAsString().equalsIgnoreCase(fieldName))
						{
							bindAttribute = individualBindObj.getAsString();
							return bindAttribute;
						}						
					}					
				}
				for(JsonElement fieldElement:subFormArray)
				{
					individualField = fieldElement.getAsJsonObject();
					subformElement = individualField.get("fields");
				}
				for(JsonElement fieldElement:subformElement.getAsJsonArray())
				{
					individualField = fieldElement.getAsJsonObject();
					individualNameObj = individualField.get("name");
					individualBindObj = individualField.get("bind");
					if(individualBindObj!=null)
					{
						if(individualNameObj.getAsString().equalsIgnoreCase(fieldName))
						{
							bindAttribute = individualBindObj.getAsString();
							return bindAttribute;
						}						
					}					
				}
			}
			
		}
		catch(Exception e)
		{
			e.getMessage();
		}
    	
    	return null;
	}
	/*
	 * read model.xml
	 * use xpath for finding the path directly in the model.xml
	 * or split path by / and decend by each parent node unless last child specified is reached    	
	 */
	Node getFormPropertyNameForAttribute(String formBindForField,FormSubmission formSubmission)
	{
		
    	Node lastNode = null;
    	String formName = formSubmission.formName();
    	String filePath = this.xmlFilePath+"/"+formName+"/model.xml";
    	File file = new File(filePath);
    	try {
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xPath = xPathFactory.newXPath();
			//put the xpath to get
			XPathExpression expr = xPath.compile(formBindForField);
			
			Object numberOfDownloads = expr.evaluate(document, XPathConstants.NODE);
			lastNode = (Node) expr.evaluate(document, XPathConstants.NODE);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block	
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    			
		
		return lastNode;
	}
}
