package org.ei.drishti.reporting.repository;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;

import org.ei.drishti.reporting.controller.LocationController;
import org.ei.drishti.reporting.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Transaction;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class  AllServicesProvidedRepository {
    private DataAccessTemplate dataAccessTemplate;
    private ANCVisitDue ancvisitDue;
    private static Logger logger = LoggerFactory
			.getLogger(AllServicesProvidedRepository.class.toString());

    protected AllServicesProvidedRepository() {
    }

    @Autowired
    public AllServicesProvidedRepository(@Qualifier("serviceProvidedDataAccessTemplate") DataAccessTemplate 
    		dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
        
    }

    public void save(ServiceProvider serviceProvider, String externalId, Indicator indicator, Date date, Location location, String dristhiEntityId) {
        dataAccessTemplate.save(new ServiceProvided(serviceProvider, externalId, indicator, date, location, dristhiEntityId));
    }

    public void delete(String indicator, String startDate, String endDate) {
        List result = dataAccessTemplate.findByNamedQuery(ServiceProvided.FIND_BY_ANM_IDENTIFIER_WITH_INDICATOR_FOR_MONTH,
                indicator, LocalDate.parse(startDate).toDate(), LocalDate.parse(endDate).toDate());
        dataAccessTemplate.deleteAll(result);
    }

    public List<ServiceProvidedReport> getNewReports(Integer token) {
        return dataAccessTemplate.findByNamedQuery(ServiceProvidedReport.FIND_NEW_SERVICE_PROVIDED, token);
    }

    public List<ServiceProvided> getAllReportsForDristhiEntityID(String dristhiEntityID) {
        return dataAccessTemplate.findByNamedQuery(ServiceProvided.FIND_SERVICE_PROVIDED_FOR_DRISTHI_ENTITY_ID, dristhiEntityID);
    }

    public List<ServiceProvidedReport> getNewReports(Integer token, int numberOfRowsToFetch) {
        return dataAccessTemplate.getSessionFactory()
                .getCurrentSession()
                .getNamedQuery(ServiceProvidedReport.FIND_NEW_SERVICE_PROVIDED)
                .setParameter(0, token)
                .setMaxResults(numberOfRowsToFetch)
                .list();

    }

    public void deleteReportsFor(String dristhiEntityId) {
        dataAccessTemplate.deleteAll(getAllReportsForDristhiEntityID(dristhiEntityId));
    }
    public void ancsave(String entityid,String patientnum,String anmnum,String visittype,Integer visitno,String visitdate,String womenname) {
    	logger.info("####### ancsave method invoked$$$$$");
        dataAccessTemplate.save(new ANCVisitDue(entityid,patientnum,anmnum,visittype,visitno,visitdate,womenname));
    	
    }
    
    public void ancvisitupdate(String entityid,String patientnum,String anmnum,String visittype,Integer visitno,String visitdate) {
    	logger.info(" ancsave method invoked$$$$$");
//    	logger.info(" entity id$$$$$"+entityid);
//        //dataAccessTemplate.save(new ANCVisitDue(entityid,patientnum,anmnum,visittype,visitno,visitdate));
    	List<ANCVisitDue> ancdetails=dataAccessTemplate.findByNamedQuery(ANCVisitDue.FIND_BY_ENTITY_ID,entityid);
    	String date = collect(ancdetails, on(ANCVisitDue.class).visitdate()).toString();
    	
    	logger.info("anc details: "+ancdetails+"date"+date);
    	ANCVisitDue ancupdate=ancdetails.get(0);
    	logger.info("anc update: "+ancupdate);
    	ancupdate.setvisitdate(visitdate);
    	 logger.info("VISIT DATE"+visitdate);
    	dataAccessTemplate.update(ancupdate);
    	logger.info("visit date"+visitdate);
    	//dataAccessTemplate.merge(entity);
    	//dataAccessTemplate.save(new ANCVisitDue(entityid,patientnum,anmnum,visittype,visitno,visitdate));
    }
    
    public void ecsave(String entityid,String phonenumber) {
    	logger.info("####### ancsave method invoked$$$$$");
        dataAccessTemplate.save(new EcRegDetails(entityid,phonenumber));
    	
    }
    
}
