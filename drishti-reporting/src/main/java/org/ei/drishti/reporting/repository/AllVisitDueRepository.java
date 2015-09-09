package org.ei.drishti.reporting.repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ei.drishti.reporting.controller.LocationController;
import org.ei.drishti.reporting.domain.ANCVisitDue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllVisitDueRepository {

	 private DataAccessTemplate dataAccessTemplate;
	 private static Logger logger = LoggerFactory
				.getLogger(AllVisitDueRepository.class.toString());

	    protected AllVisitDueRepository() {
	    }

	    @Autowired
	    public AllVisitDueRepository(@Qualifier("serviceProvidedDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
	        this.dataAccessTemplate = dataAccessTemplate;
	    }

	    public List fetchvisitDueDetails(String entityid) {	
	    	logger.info("Fetch visit due details***"+entityid);
	        return dataAccessTemplate.findByNamedQueryAndNamedParam(ANCVisitDue.FIND_BY_ENTITY_ID,
	                new String[]{"entityid"}, new Object[]{entityid});
	    }
	
}
