package org.opensrp.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import static org.opensrp.web.HttpHeaderFactory.allowOrigin;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.opensrp.domain.ErrorTrace;
import org.opensrp.domain.ErrorTraceForm;
import org.opensrp.service.ErrorTraceService;

@Controller
@RequestMapping("/errorhandler")
public class ErrorTraceController {
	
	private String opensrpSiteUrl;
	private ErrorTraceService errorTraceService;
	
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
	
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
		List<ErrorTrace> list=errorTraceService.getAllUnsolvedErrors();
		model.put("errors",list );
		
		return new ModelAndView("unsolvederrors", model);
		
		
	}
	
	@RequestMapping(method=GET,value="/solvederrors")
	public ModelAndView  showSolved(){
	//	ModelAndView  map=new ModelAndView ();
		Map<String, Object> model = new HashMap<String, Object>();
		List<ErrorTrace> list=errorTraceService.getAllSolvedErrors();
		model.put("errors",list );
		
		return new ModelAndView("home_error", model);
		
		
	}
	
	
	/*@RequestMapping(method=GET,value="/viewerror")
	public ModelAndView  showError(@RequestParam("id") String id){
	
		ErrorTrace error=errorTraceService.getError(id);
	//	ModelAndView  map=new ModelAndView ();
		
	//	erro.ge
		ErrorTraceForm errorTraceForm=new ErrorTraceForm();
		errorTraceForm.setErrorTrace(error);
		System.out.println("error ID :" + errorTraceForm.getErrorTrace());
		//Map<String, Object> model = new HashMap<String, Object>();
		//List<ErrorTrace> list=errorTraceService.getAllErrors();
		//model.put("errorTraceForm",errorTraceForm );
		
		return new ModelAndView("view_error", "errorTraceForm",errorTraceForm);
		
		
	}*/
	
	@RequestMapping(method=GET,value="/viewerror")
	public String  showError(Model model,@RequestParam("id") String id){
	
		ErrorTrace error=errorTraceService.getError(id);
	//	ModelAndView  map=new ModelAndView ();
		
//		error.
	//	erro.ge
		ErrorTraceForm errorTraceForm=new ErrorTraceForm();
		errorTraceForm.setErrorTrace(error);
		System.out.println("error ID :" + errorTraceForm.getErrorTrace());
		//Map<String, Object> model = new HashMap<String, Object>();
		//List<ErrorTrace> list=errorTraceService.getAllErrors();
		model.addAttribute("errorTraceForm",errorTraceForm );
		
		return "view_error";//new ModelAndView("view_error", "errorTraceForm",errorTraceForm);
		
		
	}
	
	@RequestMapping(value="/update_errortrace" ,method=POST)
	public String updateErrorTrace(HttpServletRequest request,ErrorTraceForm errorTraceForm, BindingResult errors) {
		if (errors.hasErrors()) {
			
		}
		
		System.out.println(errorTraceForm.getErrorTrace().getId());
		ErrorTrace errorTrace=errorTraceService.getError(errorTraceForm.getErrorTrace().getId());
		errorTrace.setStatus(errorTraceForm.getErrorTrace().getStatus());
		errorTraceService.updateError(errorTrace);
		//System.out.println("page context :: "+request.getContextPath());
		return "redirect:/errorhandler/viewerror?id="+errorTrace.getId();
	}
	
	@RequestMapping(method=GET,value="/allerrors")
	@ResponseBody
	public <T> ResponseEntity<T> getAllErrors(){
		
		List<ErrorTrace> list=errorTraceService.getAllErrors();
		if(list==null){
			return (ResponseEntity<T>) new  ResponseEntity<> ("No Record(s) Found .",allowOrigin(opensrpSiteUrl), OK);
			
		}
		return (ResponseEntity<T>) new  ResponseEntity<> (list,allowOrigin(opensrpSiteUrl), OK);
		
	}
	
	
	
	
}
