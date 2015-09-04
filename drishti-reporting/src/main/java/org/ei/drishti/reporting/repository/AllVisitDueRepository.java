package org.ei.drishti.reporting.repository;

import java.util.List;

import org.ei.drishti.reporting.domain.ANCVisitDue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllVisitDueRepository {

	 private DataAccessTemplate dataAccessTemplate;

	    protected AllVisitDueRepository() {
	    }

	    @Autowired
	    public AllVisitDueRepository(@Qualifier("serviceProvidedDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
	        this.dataAccessTemplate = dataAccessTemplate;
	    }

//	    public List fetchvisitDueDetails(String entityid) {	
//	    	
//	        return dataAccessTemplate.findByNamedQueryAndNamedParam(ANCVisitDue.FIND_BY_ENTITY_ID,
//	                new String[]{"entityid"}, new Object[]{entityid});
//	    }
	
}
