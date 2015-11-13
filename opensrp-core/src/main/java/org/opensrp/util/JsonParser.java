package org.opensrp.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;





import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.opensrp.domain.Form;
import org.opensrp.domain.FormDefinition;
import org.opensrp.domain.FormField;
import org.opensrp.domain.SubFormDefinition;
public class JsonParser {

	public String getFormDefinition(byte[] jsonData) throws JsonProcessingException, IOException {
	try{
		
		
		//read json file data to String
		//byte[] jsonData = Files.readAllBytes(Paths.get("tt.json"));
		JsonParser jsonParser=new  JsonParser();
		//create ObjectMapper instance
		ObjectMapper objectMapper = new ObjectMapper();
	//	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	//	objectMapper.readValue(jsonData, FormSubmission.class)
		//read JSON like DOM Parser FormSubmission
		JsonNode rootNode = objectMapper.readTree(jsonData);
		FormDefinition formD=jsonParser.getForm(rootNode );
		//System.out.println(objectMapper.writeValueAsString(formD));;
	//	objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		//objectMapper.writeValue(new File("form_definition.json"), formD);
		String s=objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(formD);
		//String s=objectMapper.writeValueAsString(formD);
		//objectMapper.
		//System.out.println(s);
	
	return s;
	}catch(Exception e){
		System.err.println(e.getMessage());
		e.printStackTrace();
		
	}
	return null;
	}
	
	private List<String> getFields(JsonNode jsonNode) {
		//StringBuilder fields=new StringBuilder();
		List<String> fields=new ArrayList<String>();
		
		Iterator<JsonNode> elements = jsonNode.getElements();
		while(elements.hasNext()){
			Iterator<JsonNode>  phones = elements.next().getElements();
			while (phones.hasNext()) {
				JsonNode jsonNode1 = (JsonNode) phones.next();
				Iterator<JsonNode>  ps	=jsonNode1.iterator();
				while (ps.hasNext()) {
					JsonNode jsonNode2 = (JsonNode) ps.next();
					if(jsonNode2.get("name")!=null){
					//	System.out.println(jsonNode2.get("name"));
						fields.add(jsonNode2.get("name").asText());
					}
				}
		//		System.out.println("Phone No = "+Text());	
			}
		    
		}
		return fields;
	}
	
	private List<FormField> getFields(JsonNode node,String source){
		List<FormField> list =new ArrayList<FormField>();
		//String source="/model/instance/"+source+"/";
		
		Iterator<JsonNode> elements = node.getElements();
		
			
	//	System.err.println(node);
		//System.out.println(phoneNosNode);
		while(elements.hasNext()){
			JsonNode jjNode=elements.next();
			Iterator<JsonNode>  phones = node.getElements();

			if(!jjNode.get("type").asText().equalsIgnoreCase("group") && !jjNode.get("type").asText().equalsIgnoreCase("repeat"))
			{
				
			//	list.add(new FormField(  Node.get("name").asText(), source+Node.get("name").asText()  ));
			
				list.add(new FormField(jjNode.get("name").asText(), source+jjNode.get("name").asText()));
			}else if(jjNode.get("type").asText().equalsIgnoreCase("group")){
				//System.err.println( );
				list.addAll(getGroupFields(jjNode,source));
			}
			
			//System.out.println(Node.get("name"));
		}
		
		return list;
	}
	
	private List<FormField> getGroupFields(JsonNode node,String formName){
		
		List<FormField> list =new ArrayList<FormField>();
		String source=formName+node.get("name").asText()+"/";
			try{//jjNode.path("children")
		Iterator<JsonNode> elements = node.path("children").getElements();
		
		//System.out.println(phoneNosNode);
		while(elements.hasNext()){
			//System.err.println("sda");
			JsonNode jjNode=elements.next();
			//System.err.println(jjNode);
			//Iterator<JsonNode>  phones = node.getElements();
			if(jjNode.get("name")!=null ){
			//System.err.println(jjNode.get("name").asText());
				
			list.add(new FormField(jjNode.get("name").asText(), source+jjNode.get("name").asText()));
		
		//	}//list.add(node.get("name")+"/"+(Node.get("name").asText()));
			//System.out.println(Node.get("name"));
		}else {
			
			//
			System.err.println(jjNode.get("name").asText());
		}
			}}
			catch(Exception e){
				e.printStackTrace();
			}
		
		return list;
		
	}
	
	private FormDefinition getForm(JsonNode rootNode ){
		//StringBuilder sBuilder=new StringBuilder("{ ");
		FormDefinition formDefinition= new FormDefinition();
		//FormInstance formInstance=new FormInstance();
		//formInstance. 
		
		
	//]
		
	
		String formName=rootNode.get("name").asText();
	String source="/model/instance/"+formName+"/";
	
		//System.out.println(rootNode.get("name"));
		JsonNode phoneNosNode = rootNode.path("children");
		//getFields();
		List<FormField> fields=getFields(phoneNosNode, source);
		//System.out.println(fields);
		FormField field=new FormField("id",null);
		field.setShouldLoadValue(true);
		//fields.add(field);
	//	fields.set(0, field);
		fields.add(0, field);
		Form formData =new Form("", source, fields, null);
		List<SubFormDefinition> sub_forms=getSubForms(phoneNosNode, source);
		formData.setSub_forms(sub_forms);
		formDefinition.setForm(formData);
		return formDefinition;
	}
	
	
	private SubFormDefinition getSubForm(JsonNode rootNode, String source){
		String formName=rootNode.get("name").asText();
		source+=formName+"/";
		System.out.println(rootNode.path("children"));
		List<FormField> fields=getFields(rootNode.path("children"), source);
		FormField field=new FormField("id",null);
		field.setShouldLoadValue(true);
		//fields.add(field);
		fields.add(0, field);
		SubFormDefinition subForm=new SubFormDefinition(formName, fields);
		subForm.setDefault_bind_path(source);
		subForm.setBind_type("");
		//SubFormDefinition
		return subForm ;
	}
	

	
	private List<SubFormDefinition> getSubForms(JsonNode node,String source){
		List<SubFormDefinition> list =new ArrayList<SubFormDefinition>();
		//String source="/model/instance/"+source+"/";
		Iterator<JsonNode> elements = node.getElements();
		//System.out.println(phoneNosNode);
		while(elements.hasNext()){
			JsonNode jjNode=elements.next();
			Iterator<JsonNode>  phones = node.getElements();

			 if(jjNode.get("type").asText().equalsIgnoreCase("repeat")){
				
				list.add(getSubForm(jjNode, source));
			}
			
			//System.out.println(Node.get("name"));
		}
		
		return list;
	}
}
