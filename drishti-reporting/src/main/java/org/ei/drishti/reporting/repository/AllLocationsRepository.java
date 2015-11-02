package org.ei.drishti.reporting.repository;

import java.util.List;
import org.ei.drishti.reporting.domain.ANCVisitDue;
import org.ei.drishti.reporting.domain.ANMVillages;
import org.ei.drishti.reporting.domain.EcRegDetails;
import org.ei.drishti.reporting.domain.HealthCenter;
import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.domain.PHC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class AllLocationsRepository {
    private DataAccessTemplate dataAccessTemplate;
    
    private static Logger logger = LoggerFactory
			.getLogger(AllLocationsRepository.class.toString());

    protected AllLocationsRepository() {
    }

    @Autowired
    public AllLocationsRepository(@Qualifier("serviceProvidedDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    	public Location fetchBy(String village, String subCenter, String phcIdentifier) {
        return (Location) dataAccessTemplate.getUniqueResult(Location.FIND_BY_VILLAGE_SUBCENTER_AND_PHC_IDENTIFIER,
                new String[]{"village", "subCenter", "phcIdentifier"}, new Object[]{village,
                subCenter, phcIdentifier});

    }
   
    
    

    public Location fetchByANMIdentifier(String anmIdentifier) {
        return (Location) dataAccessTemplate.findByNamedQueryAndNamedParam(Location.FIND_BY_ANM_IDENTIFIER,
                new String[]{"anmIdentifier"}, new Object[]{anmIdentifier}).get(0);
    }

    public List fetchVillagesForANM(String anmIdentifier) {
        Location location = fetchByANMIdentifier(anmIdentifier);
        if (location == null)
            return null;
        return dataAccessTemplate.findByNamedQueryAndNamedParam(Location.FIND_VILLAGES_BY_PHC_AND_SUBCENTER,
                new String[]{"phcIdentifier", "subCenter"}, new Object[]{location.phc().phcIdentifier(), location.subCenter()});
    }
    
    public List fetchANMVillages(String user_id) {

        return dataAccessTemplate.findByNamedQueryAndNamedParam(ANMVillages.FIND_BY_USER_ID,
                new String[]{"user_id"}, new Object[]{user_id});
    }
    public List fetchANMphonenumber(String user_id) {
     return dataAccessTemplate.findByNamedQueryAndNamedParam(ANMVillages.FIND_PHONENUMBER_BY_USER_ID,
              new String[]{"user_id"}, new Object[]{user_id});
  }
    public List fetchphonenumber(String entityid) {
      return dataAccessTemplate.findByNamedQueryAndNamedParam(EcRegDetails.FIND_BY_ENTITYID,
              new String[]{"entityid"}, new Object[]{entityid});
  } 
    
    public List fetchphc(Integer id) {
        logger.info("try to fetch phc details");
      return dataAccessTemplate.findByNamedQueryAndNamedParam(HealthCenter.FIND_BY_ID,
              new String[]{"id"}, new Object[]{id});
  }
}
