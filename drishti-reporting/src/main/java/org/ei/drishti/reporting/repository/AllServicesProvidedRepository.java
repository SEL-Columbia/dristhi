package org.ei.drishti.reporting.repository;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.ei.drishti.reporting.controller.LocationController;
import org.ei.drishti.reporting.domain.*;
import org.ei.drishti.reporting.service.VisitService;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;



@Repository
public class  AllServicesProvidedRepository {
    private DataAccessTemplate dataAccessTemplate;
    private ANCVisitDue ancVisitDue;
    private VisitService visit;
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
    public void ancsave(String entityid,String patientnum,String anmnum,String visittype,Integer visitno,String lmpdate,String womenname,String visitdate, String anmid) {
    	logger.info("####### ancsave method invoked$$$$$");
        dataAccessTemplate.save(new ANCVisitDue(entityid,patientnum,anmnum,visittype,visitno,lmpdate,womenname,visitdate,anmid));
    	
    }
    
   // public void ancvisitupdate(String entityid,String patientnum,String anmnum,String visittype,Integer visitno,String visitdate) {
    public void ancvisitupdate(Integer id,String newdate,Integer visitno) {	
   	
   	ANCVisitDue objectToUpdate = (ANCVisitDue) dataAccessTemplate.get(ANCVisitDue.class, id);
   	objectToUpdate.setvisitdate(newdate);
   	objectToUpdate.setvisitno(visitno);
   	dataAccessTemplate.saveOrUpdate(objectToUpdate);
   	logger.info(" visit date: "+objectToUpdate);
  	logger.info(" visit date2: ");
    }
    
    public void ancupdate(Integer id,String phonenumber) {	
       	
       	ANCVisitDue objectToUpdate = (ANCVisitDue) dataAccessTemplate.get(ANCVisitDue.class, id);
       	objectToUpdate.setpatientnum(phonenumber);
       	dataAccessTemplate.saveOrUpdate(objectToUpdate);
       	logger.info(" visit date: "+objectToUpdate);
      	logger.info(" visit date2: ");
        }
    public void ecupdate(Integer id,String phoneNumber) {	
       	
       	EcRegDetails objectToUpdate = (EcRegDetails) dataAccessTemplate.get(EcRegDetails.class, id);
       	objectToUpdate.setphonenumber(phoneNumber);
       	dataAccessTemplate.saveOrUpdate(objectToUpdate);
       	logger.info(" visit date: "+objectToUpdate);
      	logger.info(" visit date2: ");
        }
    
    public void ecsave(String entityid,String phonenumber) {
    	logger.info("####### ancsave method invoked$$$$$");
        dataAccessTemplate.save(new EcRegDetails(entityid,phonenumber));
    	
    }
    public void pocsave(String visittype, String visitentityid,String entityidec,String anmid, String phc, String timestamp) {
    	logger.info("####### pocsave method invoked$$$$$"+phc);
        
           java.util.Date date = new java.util.Date();
			Timestamp timestamp1 = new Timestamp(date.getTime());  
                        
        dataAccessTemplate.save(new POC_Table(visitentityid,entityidec,anmid,"1"," "," ", visittype, phc," ", " ", timestamp1));
       
    	
    }
    
    public List<ANCVisitDue> getANCDristhiEntityID(String entityid) {
    	logger.info("delete process started step 2");
        return dataAccessTemplate.findByNamedQuery(ANCVisitDue.FIND_BY_ENTITY_ID, entityid);
    }
    
    public void deleteANCFor(String entityid) {
    	logger.info("delete process started");
        dataAccessTemplate.deleteAll(getANCDristhiEntityID(entityid));
        logger.info("delete done*****");
    }
    
}
