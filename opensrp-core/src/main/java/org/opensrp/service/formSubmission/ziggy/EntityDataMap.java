package org.opensrp.service.formSubmission.ziggy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensrp.util.Utils;
import org.springframework.stereotype.Repository;

@Repository
public class EntityDataMap {
	public static final String ID = "id";
	public static final String DETAILS = "details";
	public static final String DOCUMENT_TYPE = "type";
	public static final String[] ID_FIELD_ON_ENTITY = new String[]{"caseId", "baseEntityId", "entityId"};
	private Map<String, Class<?>> classMap;
	
	public EntityDataMap() {
		classMap = new HashMap<>();
	}
	
	public void addEntity(String entityType, Class<?> cls) {
		classMap.put(entityType, cls);
	}
	
	public List<String> getFieldsList(String entityType) {
		return Utils.getFieldsAsList(classMap.get(entityType));
	}
	public String getIdField(String entityType) {
		for (String idf : ID_FIELD_ON_ENTITY) {
			try {
				if(classMap.get(entityType).getDeclaredField(idf) != null){
					return idf;
				}
			} catch (NoSuchFieldException | SecurityException e) {
				// do nothing as method finds field name is null and returns null
			}
		}
		return null;
	}
	
	public String getIdViewName(String entityType) {
		return "by_"+getIdField(entityType);
	}
	
	public String getDocEntityType(String entityType) {
		return classMap.get(entityType).getSimpleName();
	}
	
	public boolean hasEntityMap(String entityMap) {
		return classMap.containsKey(entityMap);
	}
}
