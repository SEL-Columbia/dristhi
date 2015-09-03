package org.ei.drishti.reporting.controller;


import org.ei.drishti.dto.ANMVillagesDTO;
import org.ei.drishti.dto.VillagesDTO;
import org.ei.drishti.reporting.domain.ANMVillages;
import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.repository.ANCVisitRepository;
import org.ei.drishti.reporting.service.ANMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class LocationController {
    private ANMService anmService;
    
   
    private static Logger logger = LoggerFactory
			.getLogger(LocationController.class.toString());

    @Autowired
    public LocationController(ANMService anmService) {
        this.anmService = anmService;
        
        
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS}, value = "/villages")
    public ResponseEntity<VillagesDTO> villagesForANM(@RequestParam("anm-id") String anmIdentifier
    		) {
    	logger.info("***** trying to fetch village details******");
        List villagesForANM = anmService.getVillagesForANM(anmIdentifier);
       VillagesDTO villagesDTO = null;
        if (villagesForANM != null) {
            Location anmLocation = (Location) villagesForANM.get(0);
            logger.info("**** fetched anmLocation details***"+anmLocation);
            List<String> villages = collect(villagesForANM, on(Location.class).village());
            logger.info("**** fetched villages***"+villages);
            villagesDTO = new VillagesDTO(anmLocation.district().toLowerCase(),
                    anmLocation.phcName(),
                    anmLocation.phc().phcIdentifier(),
                    anmLocation.subCenter(),  
                    villages);
            logger.info("^^^^^^^^^trying to insert data^^^^^^^");
            
           
        }
        return new ResponseEntity<>(villagesDTO, OK);
    }
    
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS}, value = "/anmvillages")
    public ResponseEntity<ANMVillagesDTO> anmVillages(@RequestParam("anm-id") String anmIdentifier ) {
    	
        List villagesForANM = anmService.getANMVillages(anmIdentifier);
        logger.info("*****villages fetched******"+villagesForANM);
        ANMVillagesDTO anmvillagesDTO = null;
        
           if (villagesForANM != null) {
            ANMVillages anmLocation = (ANMVillages) villagesForANM.get(0);
            logger.info("**** fetched anmLocation details***"+anmLocation);
          // villages = collect(villagesForANM, on(ANMVillages.class).villages());
           
            
            anmvillagesDTO = new ANMVillagesDTO(anmLocation.user_id(), anmLocation.userrole(), anmLocation.villages());
            //anmvillagesDTO = new ANMVillagesDTO(anmLocation.user_id(), anmLocation.userrole(), villages);
                    }
           return new ResponseEntity<>(anmvillagesDTO, OK);
    }
   
    }


