package org.ei.drishti.reporting.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ei.drishti.dto.VillagesDTO;
import org.ei.drishti.reporting.repository.ANCVisitRepository;
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

private static Logger logger = LoggerFactory
.getLogger(FromDataController.class.toString());

	@Autowired
	public FromDataController(ANCVisitRepository ancVisitRepository){
	this.ancVisitRepository=ancVisitRepository;
	}
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS}, value = "/formdata")
    public void formData(@RequestParam("entityId") String entityId,
    		@RequestParam("edd") String edd,
    		@RequestParam("phoneNumber") String phoneNumber
    		) throws ParseException{
		logger.info("^^ form data into reporting controller****");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String visitdate=null;
		Date date = null;
		String anmnum="124578";
		Date visit=null;
		
		Integer visitno=3;
			
		date=formatter.parse(edd);		    
		logger.info("value od date******"+date);
		visit = new DateTime(date).minusDays(250).toDate();
					
		logger.info("visit******"+visit);
			visitdate=formatter.format(visit);
			logger.info("date conversion completed######"+visitdate);
			logger.info("^^ transfer data from conteroller to repository****");
			
			
			ancVisitRepository.insert(entityId,phoneNumber,anmnum,edd,visitno,visitdate);
		
			//visit = format.parse("12/31/2006");
						
			
			
			
			
		
	}
	}

