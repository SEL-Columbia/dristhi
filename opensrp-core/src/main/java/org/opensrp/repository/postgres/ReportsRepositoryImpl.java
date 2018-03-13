package org.opensrp.repository.postgres;

import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.domain.Report;
import org.opensrp.repository.ReportsRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ReportsRepositoryImpl implements ReportsRepository {
	
	@Override
	public Report get(String id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void add(Report entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(Report entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<Report> getAll() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void safeRemove(Report entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Report findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Report> findByBaseEntityId(String baseEntityId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Report> findAllByIdentifier(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Report> findByBaseEntityAndType(String baseEntityId, String reportType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Report> findByEmptyServerVersion() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Report> findByServerVersion(long serverVersion) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Report> findByBaseEntityAndFormSubmissionId(String baseEntityId, String formSubmissionId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Report> findReports(String team, String providerId, String locationId, String baseEntityId,
	                                Long serverVersion, String sortBy, String sortOrder, int limit) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Report> findReports(String baseEntityId, DateTime from, DateTime to, String reportType, String providerId,
	                                String locationId, DateTime lastEditFrom, DateTime lastEditTo) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Report> findReportsByDynamicQuery(String query) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
