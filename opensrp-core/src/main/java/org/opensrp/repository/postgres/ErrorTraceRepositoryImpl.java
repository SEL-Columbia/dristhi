package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.ektorp.DocumentNotFoundException;
import org.joda.time.DateTime;
import org.opensrp.domain.ErrorTrace;
import org.opensrp.domain.postgres.ErrorTraceExample;
import org.opensrp.repository.ErrorTraceRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomErrorTraceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ErrorTraceRepositoryImpl extends BaseRepositoryImpl<ErrorTrace> implements ErrorTraceRepository {
	
	private final static String SOLVED = "solved";
	
	private final static String UNSOLVED = "unsolved";
	
	@Autowired
	private CustomErrorTraceMapper errorTraceMapper;
	
	@Override
	public ErrorTrace get(String id) {
		ErrorTraceExample example = new ErrorTraceExample();
		example.createCriteria().andDocumentIdEqualTo(id);
		List<org.opensrp.domain.postgres.ErrorTrace> errors = errorTraceMapper.selectByExample(example);
		if (!errors.isEmpty())
			return convert(errors.get(0));
		else
			return null;
	}
	
	@Override
	public void add(ErrorTrace entity) {
		if (entity == null || entity.getStatus() == null) {
			return;
		}
		
		if (retrievePrimaryKey(entity) != null) { //ErrorTrace already added
			return;
		}
		
		if (entity.getId() == null)
			entity.setId(UUID.randomUUID().toString());
		
		org.opensrp.domain.postgres.ErrorTrace pgErrorTrace = convert(entity, null);
		if (pgErrorTrace == null) {
			return;
		}
		
		errorTraceMapper.insertSelective(pgErrorTrace);
		
	}
	
	@Override
	public void update(ErrorTrace entity) {
		if (entity == null || entity.getId() == null || entity.getStatus() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		
		if (id == null) { //ErrorTrace doesn't not exist
			return;
		}
		
		org.opensrp.domain.postgres.ErrorTrace pgErrorTrace = convert(entity, id);
		errorTraceMapper.updateByPrimaryKey(pgErrorTrace);
	}
	
	@Override
	public List<ErrorTrace> getAll() {
		return convert(errorTraceMapper.selectMany(new ErrorTraceExample(), 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public void safeRemove(ErrorTrace entity) {
		if (entity == null || entity.getStatus() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}
		
		errorTraceMapper.deleteByPrimaryKey(id);
		
	}
	
	@Override
	public ErrorTrace findById(String _id) throws DocumentNotFoundException {
		return get(_id);
	}
	
	@Override
	public boolean exists(String id) {
		return get(id) != null;
	}
	
	@Override
	public List<ErrorTrace> findAllErrors() throws DocumentNotFoundException {
		return getAll();
	}
	
	@Override
	public List<ErrorTrace> findAllUnSolvedErrors() throws DocumentNotFoundException {
		ErrorTraceExample example = new ErrorTraceExample();
		example.createCriteria().andStatusEqualTo(UNSOLVED);
		return convert(errorTraceMapper.selectMany(new ErrorTraceExample(), 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<ErrorTrace> findAllSolvedErrors() throws DocumentNotFoundException {
		ErrorTraceExample example = new ErrorTraceExample();
		example.createCriteria().andStatusEqualTo(SOLVED);
		return convert(errorTraceMapper.selectMany(new ErrorTraceExample(), 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	protected Long retrievePrimaryKey(ErrorTrace errorTrace) {
		if (errorTrace == null) {
			return null;
		}
		String documentId = errorTrace.getId();
		
		ErrorTraceExample example = new ErrorTraceExample();
		example.createCriteria().andDocumentIdEqualTo(documentId);
		List<org.opensrp.domain.postgres.ErrorTrace> errors = errorTraceMapper.selectByExample(example);
		return errors.isEmpty() ? null : errors.get(0).getId();
	}
	
	@Override
	protected Object getUniqueField(ErrorTrace errorTrace) {
		return errorTrace == null ? errorTrace : errorTrace.getId();
	}
	
	//private Methods
	private ErrorTrace convert(org.opensrp.domain.postgres.ErrorTrace pgEntity) {
		ErrorTrace entity = new ErrorTrace();
		entity.setId(pgEntity.getDocumentId());
		entity.setDateOccurred(new DateTime(pgEntity.getDateOccurred()));
		entity.setErrorType(pgEntity.getErrorType());
		entity.setOccurredAt(pgEntity.getOccurredAt());
		entity.setStackTrace(pgEntity.getStackTrace());
		entity.setStatus(pgEntity.getStatus());
		entity.setDateClosed(pgEntity.getDateClosed());
		entity.setDocumentType(pgEntity.getDocumentType());
		entity.setRetryUrl(pgEntity.getRetryUrl());
		return entity;
	}
	
	private org.opensrp.domain.postgres.ErrorTrace convert(ErrorTrace entity, Long id) {
		org.opensrp.domain.postgres.ErrorTrace pgEntity = new org.opensrp.domain.postgres.ErrorTrace();
		pgEntity.setId(id);
		pgEntity.setDocumentId(entity.getId());
		pgEntity.setDateOccurred(entity.getDateOccurred().toDate());
		pgEntity.setErrorType(entity.getErrorType());
		pgEntity.setOccurredAt(entity.getOccurredAt());
		pgEntity.setStackTrace(entity.getStackTrace());
		pgEntity.setStatus(entity.getStatus());
		pgEntity.setDateClosed(entity.getDateClosed());
		pgEntity.setDocumentType(entity.getDocumentType());
		pgEntity.setRetryUrl(entity.getRetryUrl());
		return pgEntity;
	}
	
	private List<ErrorTrace> convert(List<org.opensrp.domain.postgres.ErrorTrace> pgErrors) {
		if (pgErrors == null || pgErrors.isEmpty()) {
			return new ArrayList<>();
		}
		List<ErrorTrace> errorTraces = new ArrayList<>();
		for (org.opensrp.domain.postgres.ErrorTrace pgError : pgErrors) {
			ErrorTrace error = convert(pgError);
			if (error != null) {
				errorTraces.add(error);
			}
		}
		
		return errorTraces;
	}
}
