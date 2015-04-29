package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.FormEntityTypes.CHILD_TYPE;
import static org.opensrp.common.AllConstants.FormEntityTypes.ELIGIBLE_COUPLE_TYPE;
import static org.opensrp.common.AllConstants.FormEntityTypes.MCTS_REPORT_TYPE;
import static org.opensrp.common.AllConstants.FormEntityTypes.MOTHER_TYPE;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.opensrp.common.AllConstants;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.service.reporting.ReferenceData;
import org.opensrp.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class ReferenceDataRepository implements IReferenceDataRepository {

    private static Logger logger = LoggerFactory.getLogger(ReferenceDataRepository.class);
    private CouchDbConnector db;
    private static final String ID_FIELD_ON_ENTITY = "caseId";
    private static final String CASE_ID_VIEW_NAME = "by_caseId";
    private static final String DETAILS = "details";
    private Map<String, String> designDocMap;

    @Autowired
    public ReferenceDataRepository(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db) {
        this.db = db;
        designDocMap = new HashMap<>();
        //replace with constants
        designDocMap.put(ELIGIBLE_COUPLE_TYPE, "EligibleCouple");
        designDocMap.put(MOTHER_TYPE, "Mother");
        designDocMap.put(CHILD_TYPE, "Child");
        designDocMap.put(MCTS_REPORT_TYPE, "MCTSReport");
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
        logger.info(MessageFormat.format("Trying to load entityType: {0}, with id: {1}", docEntityType, id));
        List<ViewResult.Row> rows = db.queryView(new ViewQuery().viewName(CASE_ID_VIEW_NAME).designDocId("_design/" + docEntityType).key(id)
                .queryParam(ID_FIELD_ON_ENTITY, id).includeDocs(true)).getRows();
        logger.debug(MessageFormat.format("Found these rows for entityType: {0}, with id: {1}, rows: {2}", docEntityType, id, rows));
        return rows;
    }
}

