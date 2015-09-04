package org.ei.drishti.reporting.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.ei.drishti.dto.VillagesDTO;
import org.ei.drishti.reporting.repository.ANCVisitRepository;
import org.ei.drishti.reporting.service.ANMService;
import org.ei.drishti.reporting.service.VisitService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FromDataController {
	
private ANCVisitRepository ancVisitRepository;
private VisitService visitService;
private ANMService anmService;

private static Logger logger = LoggerFactory
.getLogger(FromDataController.class.toString());

	@Autowired
	public FromDataController(ANCVisitRepository ancVisitRepository,VisitService visitService, ANMService anmService){
	this.ancVisitRepository=ancVisitRepository;
	this.visitService=visitService;
	this.anmService=anmService;
	}
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS}, value = "/formdata")
    public void formData(@RequestParam("entityId") String entityId,
    		@RequestParam("edd") String edd,
    		@RequestParam("phoneNumber") String phoneNumber,
    		@RequestParam("visitnumber") Integer visitnumber
    		) {
		logger.info("^^ form data into reporting controller****");
		
		String visitdate=edd;
		String visittype="ANC";
		List anmphonenumber=anmService.getanmPhoneNumber("anm123");
		logger.info("anmPhone number************::::"+anmphonenumber);
		String anmnum="124578";
		
		
		
		//List visitduedetails=visitService.getVisitDue(entityId);
		
			logger.info("date conversion completed######"+visitdate);
			logger.info("^^ transfer data from conteroller to repository****");
			
			
			ancVisitRepository.insert(entityId,phoneNumber,anmnum,visittype,visitnumber,visitdate);
		
			//visit = format.parse("12/31/2006");
	
	}
	}

