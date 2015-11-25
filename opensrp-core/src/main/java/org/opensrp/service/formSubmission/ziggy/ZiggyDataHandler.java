package org.opensrp.service.formSubmission.ziggy;

import static java.util.UUID.randomUUID;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.opensrp.common.AllConstants;
import org.opensrp.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class ZiggyDataHandler {
    private CouchDbConnector db;
    private EntityDataMap edMap;

    @Autowired
    public ZiggyDataHandler(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db, 
    		EntityDataMap edMap) {
        this.db = db;
        this.edMap = edMap;
    }

    public boolean isZiggyCompliant(String entityType) {
		return edMap.hasEntityMap(entityType);
	}
    
    public String saveEntity(String entityType, String fields) {
        Map<String, String> updatedFieldsMap = Utils.getStringMapFromJSON(fields);
        String entityId = updatedFieldsMap.get(edMap.getIdField(entityType));

        List<ViewResult.Row> viewQueryResult = getDBViewQueryResult(entityId, entityType);

        ObjectNode entity;
        ObjectNode details;
        if (viewQueryResult.size() != 0) {
            JsonNode document = viewQueryResult.get(0).getDocAsNode();
            entity = (ObjectNode) document;
            details = (ObjectNode) document.get(EntityDataMap.DETAILS);
        } else {
            String docEntityType = edMap.getDocEntityType(entityType);

            entity = new ObjectNode(JsonNodeFactory.instance);
            details = new ObjectNode(JsonNodeFactory.instance);
            entity.put("_id", randomUUID().toString());
            entity.put(EntityDataMap.DOCUMENT_TYPE, docEntityType);
        }

        List<String> fieldList = getFieldsList(entityType);
        for (String fieldName : updatedFieldsMap.keySet()) {
            if (fieldList.contains(fieldName)) {
                entity.put(fieldName, updatedFieldsMap.get(fieldName));
            } else if (fieldName.equals(EntityDataMap.ID)) {
                entity.put(edMap.getIdField(entityType), updatedFieldsMap.get(fieldName));
            } else {
                details.put(fieldName, updatedFieldsMap.get(fieldName));
            }
        }
        entity.put(EntityDataMap.DETAILS, details);

        db.update(entity);
        return entityId;
    }

    List<ViewResult.Row> getDBViewQueryResult(String id, String entityType) {
        String docEntityType = edMap.getDocEntityType(entityType);
        return db.queryView(new ViewQuery().viewName(edMap.getIdViewName(entityType)).designDocId("_design/" + docEntityType).key(id)
                .queryParam(edMap.getIdField(entityType), id).includeDocs(true)).getRows();
    }

    private List<String> getFieldsList(String entityType) {
       return Collections.unmodifiableList(edMap.getFieldsList(entityType));
    }
}
