package org.ei.drishti.service.reporting.rules;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.ei.drishti.common.AllConstants;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.reporting.ReferenceData;
import org.ei.drishti.util.SafeMap;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ei.drishti.common.AllConstants.FormEntityTypes.*;

@Repository
public class ReferenceDataRepository implements IReferenceDataRepository {

    private CouchDbConnector db;
    private static final String ID_FIELD_ON_ENTITY = "caseId";
    private static final String CASE_ID_VIEW_NAME = "by_caseId";
    private static final String DETAILS = "details";
    private Map<String, String> designDocMap;

    @Autowired
    public ReferenceDataRepository(@Qualifier(AllConstants.DRISHTI_DATABASE_CONNECTOR) CouchDbConnector db) {
        this.db = db;
        designDocMap = new HashMap<>();
        //replace with constants
        designDocMap.put(ELIGIBLE_COUPLE_TYPE, "EligibleCouple");
        designDocMap.put(MOTHER_TYPE, "Mother");
        designDocMap.put(CHILD_TYPE, "Child");
    }

    public SafeMap getReferenceData(FormSubmission submission, ReferenceData referenceData) {
        String docEntityType = designDocMap.get(referenceData.type());
        SafeMap referenceDataFields = new SafeMap();

        List<ViewResult.Row> viewQueryResult = getDBViewQueryResult(submission.getField(referenceData.idField()), docEntityType);
        ObjectNode entity;
        ObjectNode details;

        if (viewQueryResult.isEmpty()) {
            return referenceDataFields;
        }
        JsonNode document = viewQueryResult.get(0).getDocAsNode();
        entity = (ObjectNode) document;
        details = (ObjectNode) document.get(DETAILS);
        for (String field : referenceData.fields()) {
            if (entity.get(field) != null) {
                referenceDataFields.put(field, entity.get(field).getTextValue());
                continue;
            }
            if (details.get(field) != null) {
                referenceDataFields.put(field, details.get(field).getTextValue());
                continue;
            }
            referenceDataFields.put(field, null);
        }
        return referenceDataFields;
    }

    public List<ViewResult.Row> getDBViewQueryResult(String id, String docEntityType) {
        System.out.println("ID :" + id);
        System.out.println("DocEntityType :" + docEntityType);
        List<ViewResult.Row> rows = db.queryView(new ViewQuery().viewName(CASE_ID_VIEW_NAME).designDocId("_design/" + docEntityType).key(id)
                .queryParam(ID_FIELD_ON_ENTITY, id).includeDocs(true)).getRows();
        System.out.println("Rows :" + rows);
        return rows;
    }
}

