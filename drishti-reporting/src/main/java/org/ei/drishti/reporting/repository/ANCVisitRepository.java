package org.ei.drishti.reporting.repository;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.common.domain.ReportDataDeleteRequest;
import org.ei.drishti.common.domain.ReportDataUpdateRequest;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.common.monitor.Monitor;
import org.ei.drishti.common.monitor.Probe;
import org.ei.drishti.reporting.controller.FormDataController;
import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.domain.ServiceProvidedReport;
import org.ei.drishti.reporting.domain.ServiceProvider;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.ei.drishti.reporting.repository.cache.ReadOnlyCachingRepository;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.ei.drishti.common.monitor.Metric.REPORTING_SERVICE_PROVIDED_CACHE_TIME;
import static org.ei.drishti.common.monitor.Metric.REPORTING_SERVICE_PROVIDED_INSERT_TIME;
import static org.ei.drishti.reporting.domain.ServiceProviderType.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class ANCVisitRepository {
	private static Logger logger = LoggerFactory
			.getLogger(ANCVisitRepository.class.toString());

	private AllServicesProvidedRepository servicesProvidedRepository;
	
	protected ANCVisitRepository(){
		
	}
	@Autowired
	public ANCVisitRepository(AllServicesProvidedRepository servicesProvidedRepository) {
		this.servicesProvidedRepository = servicesProvidedRepository;
	}
	
	@Transactional("service_provided")
	public void insert(String entityId,String phoneNumber,String anmnum,String visittype,Integer visitno,String visitdate,String womenname){
//		String pattern = "yyyy-MM-dd";
//	    SimpleDateFormat format = new SimpleDateFormat(pattern);
//		Date date=new Date();
//		Date visit = new DateTime(date).minusDays(30).toDate();
//		try {
//		//visit = format.parse("12/31/2006");
//		
//		Integer entityid=123456;
//		Integer patientnum=812133;
//		Integer anmnum=124578;
//		String regdate="2015-08-09";
//		Integer visitno=1;
//		String visitdate=format.format(visit);
		logger.info("******ancvisitrepository**");
		servicesProvidedRepository.ancsave( entityId, phoneNumber, anmnum, visittype, visitno, visitdate,womenname);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	@Transactional("service_provided")
	public void ancUpdate(String entityId,String phoneNumber,String anmnum,String visittype,Integer visitnum,String visitdate){

		logger.info("******ancvisitrepository**");
		servicesProvidedRepository.ancvisitupdate(entityId,phoneNumber,anmnum,visittype,visitnum,visitdate);

		}
	public void ecinsert(String entityId,String phoneNumber){

		logger.info("******ancvisitrepository**");
		servicesProvidedRepository.ecsave(entityId,phoneNumber);

		}
	}

