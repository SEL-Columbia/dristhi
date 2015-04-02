package org.opensrp.connector;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class FormAttributeMapper {
	
	private String jsonFilePath;
	private String xmlFilePath;
	@Autowired
	public FormAttributeMapper(@Value("#{opensrp['form.directory.name']}") String formDirPath) throws IOException
	{
		ResourceLoader loader=new DefaultResourceLoader();
		formDirPath = loader.getResource(formDirPath).getURI().getPath();
		this.jsonFilePath = formDirPath;
		this.xmlFilePath = formDirPath;
	}
	
	public String getFieldName(Map<String, String> attributeMap,FormSubmission formSubmission)
	{
		String fieldName = "";
		Node fieldTag = getFieldTagFromModel(attributeMap,formSubmission);
		String bind =getXPath(fieldTag);
		fieldName = getFieldNameFromFormDefinition(bind,formSubmission);
		return fieldName;
	}
	public String getFieldNameFromFormDefinition(String bind,FormSubmission formSubmission)
	{
		String fieldAttribute = "";
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
						if(individualBindObj.getAsString().equalsIgnoreCase(bind))
						{
							fieldAttribute = individualNameObj.getAsString();
							return fieldAttribute;
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
						if(individualNameObj.getAsString().equalsIgnoreCase(bind))
						{
							fieldAttribute = individualNameObj.getAsString();
							return fieldAttribute;
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
	public Node getFieldTagFromModel(Map<String,String> attributeMapForm,FormSubmission formSubmission)
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
			XPath xpath = xPathFactory.newXPath();
			String expression = "//*[";
			String expressionQuery = "";
			NodeList nodeList;
			for(String key:attributeMapForm.keySet())
			{				
				if(expressionQuery.length()>0)
				{
					expressionQuery += " and ";
				}
				expressionQuery += "@"+key+"='"+attributeMapForm.get(key)+"'";
			}
			
			expression += expressionQuery;
			expression += "]";
			nodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
			lastNode = nodeList.item(0);			
    	}
    	catch (ParserConfigurationException e) {
 			e.printStackTrace();
 		} catch (SAXException e) {
 			e.printStackTrace();
 		} catch (IOException e) {
 			e.printStackTrace();
 		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return lastNode;
	}
	
	public Map<String, String> getUniqueAttributeValue(List<String> attributeName, FormSubmission formSubmission)
	{
		Map<String, String> map = new HashMap<>();

		Node lastNode = null;
		String formName = formSubmission.formName();
		String filePath = this.xmlFilePath+"/"+formName+"/model.xml";
    	File file = new File(filePath);
    	try {
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			String expression = "//*[";
			String expressionQuery = "";
			NodeList nodeList;
			for(String att:attributeName)
			{				
				if(expressionQuery.length()>0)
				{
					expressionQuery += " and ";
				}
				expressionQuery += "@"+att;
			}
			
			expression += expressionQuery;
			expression += "]";
			nodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
			lastNode = nodeList.item(0);
			
			NamedNodeMap attributes = lastNode.getAttributes();
	    	for(int i=0;i<attributes.getLength();i++)
	    	{
	    		Node attributeNode = attributes.item(i);
	    		map.put(attributeNode.getNodeName(), attributeNode.getNodeValue());
	    	}
    	}
    	catch (ParserConfigurationException e) {
 			e.printStackTrace();
 		} catch (SAXException e) {
 			e.printStackTrace();
 		} catch (IOException e) {
 			e.printStackTrace();
 		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	private static String getXPath(Node node) {
	    if(node == null || node.getNodeType() != Node.ELEMENT_NODE) {
	        return "";
	    }

	    return getXPath(node.getParentNode()) + "/" + node.getNodeName();
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
	
	public String getInstanceAttributesForFormFieldAndValue(String fieldName, String fieldVal, FormSubmission formSubmission)
	{		
		String formName = formSubmission.formName();
		JsonParser parser = new JsonParser();
		String filePath = this.jsonFilePath+"/"+formName+"/form.json";
		try
		{
			Object obj = parser.parse(new FileReader(filePath));
			JsonObject jsonObject = (JsonObject)obj;
			JsonArray formElement = jsonObject.getAsJsonArray("children");
			if(formElement!=null)
			{
				for (int i = 0; i < formElement.size(); i++) {
					JsonObject el = formElement.get(i).getAsJsonObject();
					if(el.get("name").getAsString().equalsIgnoreCase(fieldName)){
						JsonArray ar = el.get("children").getAsJsonArray();
						for (int j = 0; j < ar.size(); j++) {
							JsonObject option = ar.get(j).getAsJsonObject();
							if(option.get("name").getAsString().equalsIgnoreCase(fieldVal)){
								return option.get("instance").getAsJsonObject().get("openmrs_code").getAsString();
							}
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
