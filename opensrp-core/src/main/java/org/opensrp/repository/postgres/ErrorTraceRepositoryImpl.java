package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.List;

import org.ektorp.DocumentNotFoundException;
import org.joda.time.DateTime;
import org.opensrp.domain.ErrorTrace;
import org.opensrp.domain.postgres.ErrorTraceExample;
import org.opensrp.repository.ErrorTraceRepository;
import org.opensrp.repository.postgres.mapper.ErrorTraceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ErrorTraceRepositoryImpl implements ErrorTraceRepository {
	
	@Autowired
	private ErrorTraceMapper errorTraceMapper;
	
	@Override
	public ErrorTrace get(String id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void add(ErrorTrace entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(ErrorTrace entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<ErrorTrace> getAll() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void safeRemove(ErrorTrace entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public ErrorTrace findById(String _id) throws DocumentNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean exists(String id) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public List<ErrorTrace> findAllErrors() throws DocumentNotFoundException {
		ErrorTraceExample example = new ErrorTraceExample();
		List<ErrorTrace> errors = new ArrayList<ErrorTrace>();
		for (org.opensrp.domain.postgres.ErrorTrace error : errorTraceMapper.selectByExample(example)) {
			ErrorTrace err = new ErrorTrace();
			err.setDate(new DateTime(error.getDateOccurred()));
			err.setErrorType(error.getErrorType());
			err.setStackTrace(error.getStackTrace());
			err.setStatus(error.getStatus());
			errors.add(err);
		}
		return errors;
		
	}
	
	@Override
	public List<ErrorTrace> findAllUnSolvedErrors() throws DocumentNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<ErrorTrace> findAllSolvedErrors() throws DocumentNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
