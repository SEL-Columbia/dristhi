package org.ei.drishti.reporting.service;

import java.util.List;

import org.ei.drishti.reporting.repository.AllVisitDueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VisitService {
	AllVisitDueRepository allVisitDueRepository;
	  
	protected VisitService() {
	    }

	    @Autowired
	    public VisitService(AllVisitDueRepository allVisitDueRepository) {
	        this.allVisitDueRepository=allVisitDueRepository;
	    }
//
//	    @Transactional("service_provided")
//	    public List getVisitDue(String entityid) {
//	        return allVisitDueRepository.fetchvisitDueDetails(entityid);
//	    }
}
