package org.opensrp.connector.rapidpro;

import java.util.List;
import java.util.Map;

public interface RapidProService {
	public static final String RAPIDPRO_UUID_IDENTIFIER_TYPE = "RAPIDPRO_UUID";
	public static final String RAPIDPRO_GROUPS = "RAPIDPRO_GROUPS";

	String sendMessage(List<String> urns, List<String> contacts, List<String> groups, String text, String channel);

	String createContact(Map<String,Object> fieldValues);
	
	int deleteContact(String uuid);
	
	String createGroup(String name);
	
	String addField(String label,String valueType);

}