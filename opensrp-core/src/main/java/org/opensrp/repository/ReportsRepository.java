package org.opensrp.repository;

import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.domain.Report;

public interface ReportsRepository extends BaseRepository<Report> {
	
	Report findById(String id);
	
	List<Report> findByBaseEntityId(String baseEntityId);
	
	List<Report> findAllByIdentifier(String identifier);
	
	List<Report> findByBaseEntityAndType(String baseEntityId, String reportType);
	
	List<Report> findByEmptyServerVersion();
	
	List<Report> findByServerVersion(long serverVersion);
	
	List<Report> findByBaseEntityAndFormSubmissionId(String baseEntityId, String formSubmissionId);
	
	List<Report> findReports(String team, String providerId, String locationId, String baseEntityId, Long serverVersion,
	                         String sortBy, String sortOrder, int limit);
	
	List<Report> findReports(String baseEntityId, DateTime from, DateTime to, String reportType, String providerId,
	                         String locationId, DateTime lastEditFrom, DateTime lastEditTo);
	
	List<Report> findReportsByDynamicQuery(String query);
}
