package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.domain.Report;
import org.opensrp.domain.postgres.ReportMetadata;
import org.opensrp.domain.postgres.ReportMetadataExample;
import org.opensrp.domain.postgres.ReportMetadataExample.Criteria;
import org.opensrp.repository.ReportsRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomReportMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomReportMetadataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("reportsRepositoryPostgres")
public class ReportsRepositoryImpl extends BaseRepositoryImpl<Report> implements ReportsRepository {
	
	@Autowired
	private CustomReportMapper reportMapper;
	
	@Autowired
	private CustomReportMetadataMapper reportMetadataMapper;
	
	@Override
	public Report get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		org.opensrp.domain.postgres.Report pgReport = reportMetadataMapper.selectByDocumentId(id);
		
		return convert(pgReport);
	}
	
	@Override
	public void add(Report entity) {
		if (entity == null) {
			return;
		}
		
		if (retrievePrimaryKey(entity) != null) { //Report already added
			return;
		}
		
		if (entity.getId() == null)
			entity.setId(UUID.randomUUID().toString());
		
		org.opensrp.domain.postgres.Report pgReport = convert(entity, null);
		if (pgReport == null) {
			return;
		}
		
		int rowsAffected = reportMapper.insertSelectiveAndSetId(pgReport);
		logger.info("rowsAffected, pgReport.getId():" + rowsAffected + "," + pgReport.getId());
		if (rowsAffected < 1 || pgReport.getId() == null) {
			return;
		}
		
		ReportMetadata reportMetadata = createMetadata(entity, pgReport.getId());
		if (reportMetadata != null) {
			reportMetadataMapper.insertSelective(reportMetadata);
		}
		
	}
	
	@Override
	public void update(Report entity) {
		if (entity == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) { // Report not added
			return;
		}
		
		org.opensrp.domain.postgres.Report pgReport = convert(entity, id);
		if (pgReport == null) {
			return;
		}
		
		ReportMetadata reportMetadata = createMetadata(entity, id);
		if (reportMetadata == null) {
			return;
		}
		
		int rowsAffected = reportMapper.updateByPrimaryKey(pgReport);
		if (rowsAffected < 1) {
			return;
		}
		
		ReportMetadataExample reportMetadataExample = new ReportMetadataExample();
		reportMetadataExample.createCriteria().andReportIdEqualTo(id);
		reportMetadata.setId(reportMetadataMapper.selectByExample(reportMetadataExample).get(0).getId());
		reportMetadataMapper.updateByPrimaryKey(reportMetadata);
		
	}
	
	@Override
	public List<Report> getAll() {
		List<org.opensrp.domain.postgres.Report> reports = reportMetadataMapper.selectMany(new ReportMetadataExample(), 0,
		    DEFAULT_FETCH_SIZE);
		return convert(reports);
	}
	
	@Override
	public void safeRemove(Report entity) {
		if (entity == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}
		
		ReportMetadataExample reportMetadataExample = new ReportMetadataExample();
		reportMetadataExample.createCriteria().andReportIdEqualTo(id);
		int rowsAffected = reportMetadataMapper.deleteByExample(reportMetadataExample);
		if (rowsAffected < 1) {
			return;
		}
		
		reportMapper.deleteByPrimaryKey(id);
		
	}
	
	@Override
	public Report findById(String id) {
		return get(id);
	}
	
	@Override
	public List<Report> findByBaseEntityId(String baseEntityId) {
		ReportMetadataExample reportMetadataExample = new ReportMetadataExample();
		reportMetadataExample.createCriteria().andBaseEntityIdEqualTo(baseEntityId);
		return convert(reportMetadataMapper.selectMany(reportMetadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Report> findAllByIdentifier(String identifier) {
		return convert(reportMapper.selectByIdentifier(identifier, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Report> findByBaseEntityAndType(String baseEntityId, String reportType) {
		ReportMetadataExample reportMetadataExample = new ReportMetadataExample();
		reportMetadataExample.createCriteria().andBaseEntityIdEqualTo(baseEntityId).andReportTypeEqualTo(reportType);
		return convert(reportMetadataMapper.selectMany(reportMetadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Report> findByEmptyServerVersion() {
		ReportMetadataExample reportMetadataExample = new ReportMetadataExample();
		reportMetadataExample.createCriteria().andServerVersionIsNull();
		reportMetadataExample.or(reportMetadataExample.createCriteria().andServerVersionEqualTo(0l));
		return convert(reportMetadataMapper.selectMany(reportMetadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Report> findByServerVersion(long serverVersion) {
		ReportMetadataExample reportMetadataExample = new ReportMetadataExample();
		reportMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion + 1);
		return convert(reportMetadataMapper.selectMany(reportMetadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Report> findByBaseEntityAndFormSubmissionId(String baseEntityId, String formSubmissionId) {
		ReportMetadataExample reportMetadataExample = new ReportMetadataExample();
		reportMetadataExample.createCriteria().andBaseEntityIdEqualTo(baseEntityId)
		        .andFormSubmissionIdEqualTo(formSubmissionId);
		return convert(reportMetadataMapper.selectMany(reportMetadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Report> findReports(String team, String providerId, String locationId, String baseEntityId,
	                                Long serverVersion, String sortBy, String sortOrder, int limit) {
		ReportMetadataExample reportMetadataExample = new ReportMetadataExample();
		Criteria criteria = reportMetadataExample.createCriteria();
		if (team != null && !team.isEmpty()) {
			String[] idsArray = org.apache.commons.lang.StringUtils.split(team, ",");
			List<String> ids = new ArrayList<String>(Arrays.asList(idsArray));
			if (providerId != null && !ids.contains(providerId)) {
				ids.add(providerId);
			}
			criteria.andProviderIdIn(ids);
		} else if ((providerId != null && StringUtils.isNotEmpty(providerId))) {
			criteria.andProviderIdEqualTo(providerId);
		}
		if (StringUtils.isNotBlank(locationId))
			criteria.andLocationIdEqualTo(locationId);
		if (StringUtils.isNotBlank(baseEntityId))
			criteria.andBaseEntityIdEqualTo(baseEntityId);
		if (serverVersion != null)
			criteria.andServerVersionGreaterThanOrEqualTo(serverVersion);
		reportMetadataExample.setOrderByClause(getOrderByClause(sortBy, sortOrder));
		if (!criteria.isValid()) {
			throw new IllegalArgumentException("Atleast one search filter must be specified");
		} else
			return convert(reportMetadataMapper.selectMany(reportMetadataExample, 0, limit));
	}
	
	@Override
	public List<Report> findReports(String baseEntityId, DateTime from, DateTime to, String reportType, String providerId,
	                                String locationId, DateTime lastEditFrom, DateTime lastEditTo) {
		ReportMetadataExample reportMetadataExample = new ReportMetadataExample();
		Criteria criteria = reportMetadataExample.createCriteria();
		if (StringUtils.isNotBlank(baseEntityId))
			criteria.andBaseEntityIdEqualTo(baseEntityId);
		if (StringUtils.isNotBlank(locationId))
			criteria.andReportTypeEqualTo(reportType);
		if (StringUtils.isNotBlank(providerId))
			criteria.andProviderIdEqualTo(providerId);
		if (StringUtils.isNotBlank(locationId))
			criteria.andLocationIdEqualTo(locationId);
		if (from != null || to != null)
			criteria.andReportDateBetween(from.toDate(), to.toDate());
		if (lastEditFrom != null || lastEditTo != null)
			criteria.andDateEditedBetween(lastEditFrom.toDate(), lastEditTo.toDate());
		if (!criteria.isValid())
			throw new IllegalArgumentException("Atleast one search filter must be specified");
		else
			return convert(reportMetadataMapper.selectMany(reportMetadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Report> findReportsByDynamicQuery(String query) {
		throw new IllegalArgumentException("Method not supported");
	}
	
	@Override
	protected Long retrievePrimaryKey(Report entity) {
		if (entity == null || entity.getId() == null) {
			return null;
		}
		String documentId = entity.getId();
		
		ReportMetadataExample reportMetadataExample = new ReportMetadataExample();
		reportMetadataExample.createCriteria().andDocumentIdEqualTo(documentId);
		
		org.opensrp.domain.postgres.Report pgReport = reportMetadataMapper.selectByDocumentId(documentId);
		if (pgReport == null) {
			return null;
		}
		return pgReport.getId();
	}
	
	@Override
	protected Object getUniqueField(Report report) {
		return report == null ? null : report.getId();
	}
	
	//private Methods
	private Report convert(org.opensrp.domain.postgres.Report pgReport) {
		if (pgReport == null || pgReport.getJson() == null || !(pgReport.getJson() instanceof Report)) {
			return null;
		}
		return (Report) pgReport.getJson();
	}
	
	private org.opensrp.domain.postgres.Report convert(Report entity, Long primaryKey) {
		if (entity == null) {
			return null;
		}
		
		org.opensrp.domain.postgres.Report pgReport = new org.opensrp.domain.postgres.Report();
		pgReport.setId(primaryKey);
		pgReport.setJson(entity);
		
		return pgReport;
	}
	
	private List<Report> convert(List<org.opensrp.domain.postgres.Report> reports) {
		if (reports == null || reports.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<Report> convertedReports = new ArrayList<>();
		for (org.opensrp.domain.postgres.Report event : reports) {
			Report convertedReport = convert(event);
			if (convertedReport != null) {
				convertedReports.add(convertedReport);
			}
		}
		return convertedReports;
	}
	
	private ReportMetadata createMetadata(Report entity, Long id) {
		ReportMetadata reportMetadata = new ReportMetadata();
		reportMetadata.setReportId(id);
		reportMetadata.setDocumentId(entity.getId());
		reportMetadata.setBaseEntityId(entity.getBaseEntityId());
		reportMetadata.setFormSubmissionId(entity.getFormSubmissionId());
		reportMetadata.setReportType(entity.getReportType());
		if (entity.getReportDate() != null) {
			reportMetadata.setReportDate(entity.getReportDate().toDate());
		}
		reportMetadata.setServerVersion(entity.getServerVersion());
		reportMetadata.setProviderId(entity.getProviderId());
		reportMetadata.setLocationId(entity.getLocationId());
		if (entity.getDateEdited() != null) {
			reportMetadata.setDateEdited(entity.getDateEdited().toDate());
		}
		return reportMetadata;
	}
	
}
