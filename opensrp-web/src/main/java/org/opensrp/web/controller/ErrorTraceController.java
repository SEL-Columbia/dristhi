package org.opensrp.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.opensrp.web.HttpHeaderFactory.allowOrigin;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.opensrp.domain.ErrorTrace;
import org.opensrp.service.ErrorTraceService;

@Controller
@RequestMapping("/errortrace")
public class ErrorTraceController {
	
	private String opensrpSiteUrl;
	private ErrorTraceService errorTraceService;
	
	@Autowired
	public ErrorTraceController(ErrorTraceService errorTraceService) {
		
		this.errorTraceService=errorTraceService;
	}
	
	@RequestMapping(method=GET,value="/allerrors")
	@ResponseBody
	public <T> ResponseEntity<T> getAllErrors(){
		
		List<ErrorTrace> list=errorTraceService.getAllErrors();
		if(list==null){
			return (ResponseEntity<T>) new  ResponseEntity<> ("No Record(s) Found .`",allowOrigin(opensrpSiteUrl), OK);
			
		}
		return (ResponseEntity<T>) new  ResponseEntity<> (list,allowOrigin(opensrpSiteUrl), OK);
		
	}
	
	
	
	
}
