package org.opensrp.connector.rapidpro;

import java.util.List;
import java.util.Map;

public interface RapidProService {

	String sendMessage(List<String> urns, List<String> contacts, List<String> groups, String text, String channel);

	String createContact(Map<String,Object> fieldValues);
	
	int deleteContact(String uuid);
	
	String createGroup(String name);
	
	String addField(String label,String valueType);

}