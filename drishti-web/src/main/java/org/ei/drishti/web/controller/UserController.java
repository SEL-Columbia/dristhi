package org.ei.drishti.web.controller;


import org.ei.drishti.common.domain.PhcDetail;
import org.ei.drishti.common.domain.UserDetail;
import org.ei.drishti.domain.DrishtiUser;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.web.security.DrishtiAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static org.ei.drishti.web.HttpHeaderFactory.allowOrigin;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class UserController {
	 private static Logger logger = LoggerFactory.getLogger(UserController.class.toString());
    private String drishtiSiteUrl;
    private DrishtiAuthenticationProvider drishtiAuthenticationProvider;
   private AllEligibleCouples allEligibleCouple;
    @Autowired
    public UserController(@Value("#{drishti['drishti.site.url']}") String drishtiSiteUrl,
                          DrishtiAuthenticationProvider drishtiAuthenticationProvider) {
        this.drishtiSiteUrl = drishtiSiteUrl;
        this.drishtiAuthenticationProvider = drishtiAuthenticationProvider;
        this.allEligibleCouple = allEligibleCouple;
        
    }

    @RequestMapping(method = RequestMethod.GET, value = "/authenticate-user")
    public ResponseEntity<HttpStatus> authenticateUser() {
        return new ResponseEntity<>(null, allowOrigin(drishtiSiteUrl), OK);
    }

    public DrishtiUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return drishtiAuthenticationProvider.getDrishtiUser(authentication);
    }
 

    @RequestMapping(method = RequestMethod.GET, value = "/user-details")
    public ResponseEntity<UserDetail> userDetail(@RequestParam("anm-id") String anmIdentifier)
    
    {
    
    	DrishtiUser user = drishtiAuthenticationProvider.getDrishtiUser(anmIdentifier);
        logger.info("fetched details for user" +user);
    	
    	return new ResponseEntity<>(new UserDetail(user.getUsername(), user.getRoles()), allowOrigin(drishtiSiteUrl), OK);
    }
    
    
   //new method 
    @RequestMapping(method = RequestMethod.GET, value = "/phc-details")
    public ResponseEntity<PhcDetail> phcDetail(@RequestParam("phc") String phcidentifier)
    
    {
    
    	EligibleCouple phc = allEligibleCouple.findByPhc(phcidentifier);
        logger.info("fetched details for phc" + phc);
         logger.warn(" unable to print " +phc);
    	
    	return new ResponseEntity<>(new PhcDetail(phc.id(), phc.name()), allowOrigin(drishtiSiteUrl), OK);
    
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /*
    @RequestMapping(method=RequestMethod.GET, value="/user-details/doctor")
    public ResponseEntity<DoctorDetail>doctordetail(@RequestParam("doctor-id") String Doctoridentifier){
    	DrishtiUser user = drishtiAuthenticationProvider.getDrishtiUser(Doctoridentifier);
    	return new ResponseEntity<>(new DoctorDetail(user.getUsername(), user.getRoles()),allowOrigin(drishtiSiteUrl), OK);
    	*/
   // }

}
