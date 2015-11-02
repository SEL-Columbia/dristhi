package org.ei.drishti.reporting.controller;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.ei.drishti.dto.VillagesDTO;
import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.service.ANMService;
import org.ei.drishti.reporting.service.DocService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DocLocationController {
	
	 private static Logger logger = LoggerFactory.getLogger(DocLocationController.class.toString());
	private DocService docService;

    @Autowired
    public DocLocationController(ANMService anmService) {
        this.docService = docService;
    }
	
	 @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS}, value = "/doc-villages")
	    public ResponseEntity<VillagesDTO> villagesForDoc(@RequestParam("doc-id") String anmIdentifier
	    		) {
	        logger.info("Fetched Villages for the ANM");
	        List villagesForANM = docService.getVillagesForANM(anmIdentifier);
	        VillagesDTO villagesDTO = null;
	        if (villagesForANM != null) {
	            Location anmLocation = (Location) villagesForANM.get(0);
	            List<String> villages = collect(villagesForANM, on(Location.class).village());
	            villagesDTO = new VillagesDTO(anmLocation.district().toLowerCase(),
	                    anmLocation.phcName(),
	                    anmLocation.phc().phcIdentifier(),
	                    anmLocation.subCenter(),  
	                    villages);
	        }
	        return new ResponseEntity<>(villagesDTO, OK);
	
	
	
	 }
}
