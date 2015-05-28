package org.ei.drishti.reporting.service;

import java.util.List;

import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.repository.AllLocationsRepository;
import org.ei.drishti.reporting.repository.AllSP_ANMsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class DocService {
	
	 private AllSP_ANMsRepository allANMsRepository;
	    private AllLocationsRepository allLocationsRepository;

	    protected DocService() {
	    }
	    @Autowired
	    public DocService(AllSP_ANMsRepository allANMsRepository, AllLocationsRepository allLocationsRepository) {
	        this.allANMsRepository = allANMsRepository;
	        this.allLocationsRepository = allLocationsRepository;
	    }
	    
	    @Transactional("service_provided")
	    public Location getLocation(String anmIdentifier) {
	        return allLocationsRepository.fetchByANMIdentifier(anmIdentifier);
	    }
	    
	 
	    @Transactional("service_provided")
	    public List getVillagesForANM(String anmIdentifier) {
	        return allLocationsRepository.fetchVillagesForANM(anmIdentifier);
	    }

}
