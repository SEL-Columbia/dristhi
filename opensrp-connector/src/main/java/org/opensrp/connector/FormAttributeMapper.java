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
import com.mysql.jdbc.StringUtils;

/**
 * The class is the bridge that allows parsing and mapping of formSubmission fields with 
 * those defined in xls form for OpenMRS entity mappings.
 */

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
	
	/**
	 * The method returns the field name in form submission mapped with openmrs attributes given as attributeMap.
	 * Ex: What is the field name in given form submission that is mapped with openmrs_entity=person 
	 * and openmrs_entity_id=first_name
	 * @param attributeMap
	 * @param formSubmission
	 * @return
	 */
	public String getFieldName(Map<String, String> attributeMap,FormSubmission formSubmission)
	{
		String fieldName = "";
		Node fieldTag = getFieldTagFromModel(attributeMap,formSubmission);
		String bind =getXPath(fieldTag);
		fieldName = getFieldNameFromFormDefinition(bind,formSubmission);
		return fieldName;
	}
	
	/**
	 * The method returns the field name in form submission in given subform(repeat group) mapped with openmrs attributes given as attributeMap.
	 * Ex: What is the field name in given form submission in subform=child_born that is 
	 * mapped with openmrs_entity=person and openmrs_entity_id=first_name
	 * @param attributeMap
	 * @param subform
	 * @param formSubmission
	 * @return
	 */
	public String getFieldName(Map<String, String> attributeMap, String subform, FormSubmission formSubmission)
	{
		String fieldName = "";
		Node fieldTag = getFieldTagFromModel(attributeMap, subform, formSubmission);
		String bind =getXPath(fieldTag);
		fieldName = getFieldNameFromFormDefinition(bind, subform, formSubmission);
		return fieldName;
	}
	
	/**
	 * Returns the field name in form submission with given bind path
	 * @param bind
	 * @param formSubmission
	 * @return
	 */
	String getFieldNameFromFormDefinition(String bind,FormSubmission formSubmission)
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
	
	/**
	 * Returns the field name in given subform of given form submission with specified bind path
	 * @param bind
	 * @param subform
	 * @param formSubmission
	 * @return
	 */
	String getFieldNameFromFormDefinition(String bind, String subform, FormSubmission formSubmission)
	{
		String formName = formSubmission.formName();
		JsonParser parser = new JsonParser();
		String filePath = this.jsonFilePath+"/"+formName+"/form_definition.json";
		try
		{
			JsonObject jsonObject = (JsonObject) parser.parse(new FileReader(filePath));
			JsonArray subforms = jsonObject.get("form").getAsJsonObject().get("sub_forms").getAsJsonArray();
			for (JsonElement jsonElement : subforms) {
				if(jsonElement.getAsJsonObject().get("name").getAsString().equalsIgnoreCase(subform)){
					JsonArray flarr = jsonElement.getAsJsonObject().get("fields").getAsJsonArray();
					for (JsonElement fl : flarr) {
						if(fl.getAsJsonObject().has
								("bind") && fl.getAsJsonObject().get("bind").getAsString().equalsIgnoreCase(bind)){
							return fl.getAsJsonObject().get("name").getAsString();
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
	
	/**
	 * Returns the Node in model.xml of given form submission that maps to given custom attributes
	 * @param attributeMapForm
	 * @param formSubmission
	 * @return
	 */
	Node getFieldTagFromModel(Map<String,String> attributeMapForm,FormSubmission formSubmission)
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
	
	/**
 	 * Returns the Node in model.xml of given subform of given form submission that maps to given custom attributes
	 * @param attributeMapForm
	 * @param subform
	 * @param formSubmission
	 * @return
	 */
	public Node getFieldTagFromModel(Map<String,String> attributeMapForm, String subform, FormSubmission formSubmission)
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
			String expression = getDefaultBindPathFromSubformDefinition(subform, formSubmission)+"/node()[";
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
	
	/**
	 * Get attributes and their values for given list of mappings. This should only be used for mapping those are 
	 * unique in xls forms. Otherwise may lead to inconsistent and incomplete data
	 * @param attributeName
	 * @param formSubmission
	 * @return
	 */
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
	
	/**
	 * Returns the list of custom attributes or mappings associated with given field in given form submission
	 * @param fieldName
	 * @param formSubmission
	 * @return
	 */
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

		return convertToMap(tagAndAttributes);
	    
	}
	
	private Map<String, String> convertToMap(Node tagAndAttributes){
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
	
	/**
	 * Returns the custom attributes associated with the given subform in given form submission
	 * @param subformName
	 * @param formSubmission
	 * @return
	 */
	public Map<String, String> getAttributesForSubform (String subformName, FormSubmission formSubmission){
		String formBindForField = "";
		Node tagAndAttributes = null;
		// From form_definition.json
		formBindForField = getDefaultBindPathFromSubformDefinition(subformName, formSubmission); 
		// xpath in model.xml
		if(formBindForField!=null && !formBindForField.equals("null") && formBindForField.length()>0)
		{
			tagAndAttributes = getFormPropertyNameForAttribute(formBindForField,formSubmission);
		}

	    return convertToMap(tagAndAttributes); 
	}
	
	/**
	 * Returns the custom attributes associated with specified field in given subform in given form submission
	 * @param subformName
	 * @param field
	 * @param formSubmission
	 * @return
	 */
	public Map<String, String> getAttributesForSubform (String subformName, String field, FormSubmission formSubmission){
		String formBindForField = "";
		Node tagAndAttributes = null;
		// From form_definition.json
		formBindForField = getPathFromSubformDefinition(subformName, field, formSubmission); 
		// xpath in model.xml
		if(formBindForField!=null && !formBindForField.equals("null") && formBindForField.length()>0)
		{
			tagAndAttributes = getFormPropertyNameForAttribute(formBindForField,formSubmission);
		}

	    return convertToMap(tagAndAttributes); 
	}
	
	/**
	 * Returns the value of openmrs_code associated with specified field and selected value for given subform and form submission.
	 * The function is used to get openmrs_code value of selected option in form submission from choices sheet in xls form.
	 * @param fieldName
	 * @param fieldVal
	 * @param subform
	 * @param formSubmission
	 * @return
	 */
	public String getInstanceAttributesForFormFieldAndValue(String fieldName, String fieldVal, String subform, FormSubmission formSubmission)
	{		
		String formName = formSubmission.formName();
		JsonParser parser = new JsonParser();
		String filePath = this.jsonFilePath+"/"+formName+"/form.json";
		try
		{
			String bindPath = null;
			if(StringUtils.isEmptyOrWhitespaceOnly(subform)){
				bindPath = getPropertyBindFromFormDefinition(fieldName, formSubmission);
			}
			else {
				bindPath = getPathFromSubformDefinition(subform, fieldName, formSubmission);
			}
			
			String[] sps = bindPath.split("/");
			int level = sps.length-4;
			String nodeNameToFind = sps[sps.length-1];
			Object obj = parser.parse(new FileReader(filePath));
			JsonObject jsonObject = (JsonObject)obj;
			JsonObject node = getChildrenOfLevel(level, jsonObject, nodeNameToFind);
			
			if(node != null && node.getAsJsonObject().has("children")){
				JsonArray nodeChAr = node.getAsJsonObject().get("children").getAsJsonArray();
				for (int j = 0; j < nodeChAr.size(); j++) {
					JsonObject option = nodeChAr.get(j).getAsJsonObject();
					if(option.get("name").getAsString().equalsIgnoreCase(fieldVal)){
						return option.get("instance").getAsJsonObject().get("openmrs_code").getAsString();
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    	
    	return null;
	}
	
	private JsonObject getChildrenOfLevel(int level, JsonObject node, String nodeName){
		for (JsonElement ch : getChildren(node)) {
			if(ch.getAsJsonObject().has("type")){
				for (int i = 1; i <= level; i++) {//3
					String ccurrchnmae = ch.getAsJsonObject().get("name").toString();
					if(i==level){
						if(ch.getAsJsonObject().get("name").getAsString().equalsIgnoreCase(nodeName)){
							return ch.getAsJsonObject();
						}
						continue;
					}
					
					if(!ch.getAsJsonObject().has("children")){
						break;
					}
					JsonObject obj = getChildrenOfLevel(level-i, ch.getAsJsonObject(), nodeName);
					if(obj != null){
						return obj;
					}
				}
			}
		}
		return null;
	}
	
	private JsonArray getChildren(JsonObject node){
		if(!node.has("children")){
			return new JsonArray();
		}
		
		return node.getAsJsonArray("children");
		
	}
	
	private JsonObject searchChildNode(String nodeName, JsonObject node){
		if(node.has("children")) 
		for (JsonElement ch : node.get("children").getAsJsonArray()) {
			if(ch.getAsJsonObject().get("name").getAsString().equalsIgnoreCase(nodeName)){
				return ch.getAsJsonObject();
			}
		}
		return null;
		
	}
	
	/**
	 * read default bind path from form_definition.json for given subform in given formSubmission. This is used to get the xpath in model.xml for subform.
	 */
	String getDefaultBindPathFromSubformDefinition(String subformName, FormSubmission formSubmission)
	{		
		JsonParser parser = new JsonParser();
		String filePath = this.jsonFilePath+"/"+formSubmission.formName()+"/form_definition.json";
		try
		{
			JsonObject jsonObject = (JsonObject) parser.parse(new FileReader(filePath));
			JsonArray subforms = jsonObject.get("form").getAsJsonObject().get("sub_forms").getAsJsonArray();
			for (JsonElement jsonElement : subforms) {
				if(jsonElement.getAsJsonObject().get("name").getAsString().equalsIgnoreCase(subformName)){
					return jsonElement.getAsJsonObject().get("default_bind_path").getAsString();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    	
    	return null;
	}
	
	/**
	 * Gets the bind path of specified field for given subform in given form submission
	 * @param subformName
	 * @param field
	 * @param formSubmission
	 * @return
	 */
	String getPathFromSubformDefinition(String subformName, String field, FormSubmission formSubmission)
	{		
		JsonParser parser = new JsonParser();
		String filePath = this.jsonFilePath+"/"+formSubmission.formName()+"/form_definition.json";
		try
		{
			JsonObject jsonObject = (JsonObject) parser.parse(new FileReader(filePath));
			JsonArray subforms = jsonObject.get("form").getAsJsonObject().get("sub_forms").getAsJsonArray();
			for (JsonElement jsonElement : subforms) {
				if(jsonElement.getAsJsonObject().get("name").getAsString().equalsIgnoreCase(subformName)){
					JsonArray flarr = jsonElement.getAsJsonObject().get("fields").getAsJsonArray();
					for (JsonElement fl : flarr) {
						if(fl.getAsJsonObject().get("name").getAsString().equalsIgnoreCase(field)&&fl.getAsJsonObject().has("bind")){
							return fl.getAsJsonObject().get("bind").getAsString();
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    	
    	return null;
	}
	
	/**
	 * Gets the bind path from form_definition.json in given formSubmission for specified field 
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
	
	/**
	 * Gets the Node from model.xml that maps to specified bind path in given form submission   	
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
}
