
package org.opensrp.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ektorp.DocumentNotFoundException;
import org.opensrp.domain.ErrorTrace;
import org.opensrp.repository.AllErrorTrace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author muhammad.ahmed@ihsinformatics.com
 *  Created on May 25, 2015
 */
@Service
public class ErrorTraceService {
	
	private final AllErrorTrace allErrorTrace;
	
	
	@Autowired
	public ErrorTraceService(AllErrorTrace allErrorTrace) {
		this.allErrorTrace=allErrorTrace;
	}
	
	public void addError(ErrorTrace entity){
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		
		entity.setDateOccurred(new Date (sdf.format(entity.getDateOccurred())));
		allErrorTrace.add(entity);
	}
	
	public void updateError(ErrorTrace entity){
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		entity.setDateClosed(new Date (sdf.format(entity.getDateClosed())));
		allErrorTrace.update(entity);
	}
	
	public List<ErrorTrace> getAllErrors(){
		ArrayList<ErrorTrace> allErrorList= (ArrayList<ErrorTrace>) allErrorTrace.findAllErrors();
		if(allErrorList.isEmpty()){
			return null;
			
		}
		ArrayList<ErrorTrace> errors=new ArrayList<ErrorTrace>();
		
		for(ErrorTrace e: allErrorList){
			//ErrorTrace object=new ErrorTrace(e.getDate(),e.getName(), e.getOccurredAt(), e.getStackTrace(),e.getStatus());
			errors.add(e);
		}
		
		return errors;
		
	}
	
	public List<ErrorTrace> getAllSolvedErrors(){
		ArrayList<ErrorTrace> allErrorList= (ArrayList<ErrorTrace>) allErrorTrace.findAllSolvedErrors();
		ArrayList<ErrorTrace> errors=new ArrayList<ErrorTrace>();
		
		for(ErrorTrace e: allErrorList){
			//rrorTrace object=new ErrorTrace(e.getDate(),e.getName(), e.getOccurredAt(), e.getStackTrace(),e.getStatus());
			errors.add(e);
		}
		
		return errors;
		
	}
 
	public List<ErrorTrace> getAllUnsolvedErrors(){
		
		ArrayList<ErrorTrace> allErrorList= (ArrayList<ErrorTrace>) allErrorTrace.findAllUnSolvedErrors();
		ArrayList<ErrorTrace> errors=new ArrayList<ErrorTrace>();
		
		for(ErrorTrace e: allErrorList){
			//ErrorTrace object=new ErrorTrace(e.getDate(),e.getName(), e.getOccurredAt(), e.getStackTrace(),e.getStatus());
			errors.add(e);
		}
		
		return errors;
	}
	
	public ErrorTrace getError(String id){
		try{
		return allErrorTrace.findById(id);
		}catch(DocumentNotFoundException e){
			e.printStackTrace();
			
		}
		return null;
	}

	
	
	
}
