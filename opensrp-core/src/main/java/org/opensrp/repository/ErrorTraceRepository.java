package org.opensrp.repository;

import java.util.List;

import org.ektorp.DocumentNotFoundException;
import org.opensrp.domain.ErrorTrace;

public interface ErrorTraceRepository extends BaseRepository<ErrorTrace> {
	
	ErrorTrace findById(String _id) throws DocumentNotFoundException;
	
	boolean exists(String id);
	
	List<ErrorTrace> findAllErrors() throws DocumentNotFoundException;
	
	List<ErrorTrace> findAllUnSolvedErrors() throws DocumentNotFoundException;
	
	List<ErrorTrace> findAllSolvedErrors() throws DocumentNotFoundException;
	
}
