package org.ei.drishti.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.FormSubmission;
import org.ei.drishti.domain.Mother;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.UUID.randomUUID;
import static org.ei.drishti.common.AllConstants.Form.*;

@Repository
public class FormDataRepository {
    private static final String ID = "id";
    private static final String DETAILS = "details";
    private static final String DOCUMENT_TYPE = "type";
    private static final String ID_FIELD_ON_ENTITY = "caseId";
    private static final String CASE_ID_VIEW_NAME = "by_caseId";
    private Map<String, Field[]> fieldSetMap;
    private AllFormSubmissions allFormSubmissions;
    private CouchDbConnector db;
    private Map<String, String> designDocMap;

    @Autowired
    public FormDataRepository(AllFormSubmissions allFormSubmissions, @Qualifier("drishtiDatabaseConnector") CouchDbConnector db) {
        this.allFormSubmissions = allFormSubmissions;
        this.db = db;
        initMaps();

    }

    private void initMaps() {
        designDocMap = new HashMap<>();
        fieldSetMap = new HashMap<>();
        designDocMap.put("eligible_couple", "EligibleCouple");
        designDocMap.put("mother", "Mother");
        designDocMap.put("child", "Child");
        fieldSetMap.put("eligible_couple", EligibleCouple.class.getDeclaredFields());
        fieldSetMap.put("mother", Mother.class.getDeclaredFields());
        fieldSetMap.put("child", Child.class.getDeclaredFields());
    }

    public void saveFormSubmission(String paramsJSON, String data) {
        Map<String, String> params = getStringMapFromJSON(paramsJSON);

        allFormSubmissions.add(new FormSubmission(params.get(INSTANCE_ID), params.get(FORM_NAME), params.get(ANM_ID),
                params.get(TIME_STAMP), params.get(ENTITY_ID), data));
    }

    public String saveEntity(String entityType, String fields) {
        Map<String, String> updatedFieldsMap = getStringMapFromJSON(fields);
        String entityId = updatedFieldsMap.get(ID);
        String docEntityType = designDocMap.get(entityType);

        List<ViewResult.Row> viewQueryResult = getDBViewQueryResult(entityId, docEntityType);

        ObjectNode entity;
        ObjectNode details;
        if (viewQueryResult.size() != 0) {
            JsonNode document = viewQueryResult.get(0).getDocAsNode();
            entity = (ObjectNode) document;
            details = (ObjectNode) document.get(DETAILS);
        } else {
            entity = new ObjectNode(JsonNodeFactory.instance);
            details = new ObjectNode(JsonNodeFactory.instance);
            entity.put("_id", randomUUID().toString());
            entity.put(DOCUMENT_TYPE, docEntityType);
        }

        List<String> fieldList = getFieldsList(entityType);
        for (String fieldName : updatedFieldsMap.keySet()) {
            if (fieldList.contains(fieldName)) {
                entity.put(fieldName, updatedFieldsMap.get(fieldName));
            } else if (fieldName.equals(ID)) {
                entity.put(ID_FIELD_ON_ENTITY, updatedFieldsMap.get(fieldName));
            } else {
                details.put(fieldName, updatedFieldsMap.get(fieldName));
            }
        }
        entity.put(DETAILS, details);

        db.update(entity);
        return entityId;
    }

    private List<ViewResult.Row> getDBViewQueryResult(String id, String docEntityType) {
        return db.queryView(new ViewQuery().viewName(CASE_ID_VIEW_NAME).designDocId("_design/" + docEntityType).queryParam(ID_FIELD_ON_ENTITY, id).includeDocs(true)).getRows();
    }

    private List<String> getFieldsList(String entityType) {
        List<String> fieldList = new ArrayList<>();
        Field[] fieldSet = fieldSetMap.get(entityType);
        for (Field field : fieldSet) {
            fieldList.add(field.getName());
        }
        return fieldList;
    }

    private Map<String, String> getStringMapFromJSON(String fields) {
        return new Gson().fromJson(fields, new TypeToken<Map<String, String>>() {
        }.getType());
    }
}
