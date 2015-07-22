package org.opensrp.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import static org.opensrp.web.HttpHeaderFactory.allowOrigin;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.opensrp.domain.ErrorTrace;
import org.opensrp.service.ErrorTraceService;

@Controller
@RequestMapping("/errorhandler")
public class ErrorTraceController {
	
	private String opensrpSiteUrl;
	private ErrorTraceService errorTraceService;
	
	@Autowired
	public ErrorTraceController(ErrorTraceService errorTraceService) {
		
		this.errorTraceService=errorTraceService;
	}
	
	@RequestMapping(method=GET,value="/errortrace")
	public ModelAndView  showPage(){
	//	ModelAndView  map=new ModelAndView ();
		Map<String, Object> model = new HashMap<String, Object>();
		List<ErrorTrace> list=errorTraceService.getAllErrors();
		model.put("errors",list );
		
		return new ModelAndView("home_error", model);
		
		
	}
	
	@RequestMapping(method=GET,value="/unsolvederrors")
	public ModelAndView  showUnsolved(){
	//	ModelAndView  map=new ModelAndView ();
		Map<String, Object> model = new HashMap<String, Object>();
		List<ErrorTrace> list=errorTraceService.getAllErrors();
		model.put("errors",list );
		
		return new ModelAndView("unsolvederrors", model);
		
		
	}
	
	@RequestMapping(method=GET,value="/solvederrors")
	public ModelAndView  showSolved(){
	//	ModelAndView  map=new ModelAndView ();
		Map<String, Object> model = new HashMap<String, Object>();
		List<ErrorTrace> list=errorTraceService.getAllErrors();
		model.put("errors",list );
		
		return new ModelAndView("home_error", model);
		
		
	}
	
	
	@RequestMapping(method=GET,value="/viewerror")
	public ModelAndView  showError(@RequestParam("id") String id){
	
		ErrorTrace error=errorTraceService.getError(id);
	//	ModelAndView  map=new ModelAndView ();
		Map<String, Object> model = new HashMap<String, Object>();
		//List<ErrorTrace> list=errorTraceService.getAllErrors();
		model.put("error",error );
		
		return new ModelAndView("view_error", model);
		
		
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
