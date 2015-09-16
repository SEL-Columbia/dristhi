package org.ei.drishti.reporting.service;

import java.util.List;

import org.ei.drishti.reporting.controller.LocationController;
import org.ei.drishti.reporting.repository.AllVisitDueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class VisitService {
	AllVisitDueRepository allVisitDueRepository;
	 private static Logger logger = LoggerFactory
				.getLogger(VisitService.class.toString());
	  
	protected VisitService() {
	    }

	    @Autowired
	    public VisitService(AllVisitDueRepository allVisitDueRepository) {
	        this.allVisitDueRepository=allVisitDueRepository;
	    }

    @Transactional("service_provided")
	public List getVisitDue(String entityid) {
    	logger.info("get visit details****"+entityid);
	        return allVisitDueRepository.fetchvisitDueDetails(entityid);
	    }
    
     @Transactional("service_provided")
	public List getVisitconf() {
    	logger.info("get visit details****");
	        return allVisitDueRepository.fetchvisitconf();
	    }
}
