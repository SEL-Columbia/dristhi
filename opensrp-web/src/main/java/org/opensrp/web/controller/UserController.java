package org.opensrp.web.controller;

import org.opensrp.common.domain.UserDetail;
import org.opensrp.domain.DrishtiUser;
import org.opensrp.web.security.DrishtiAuthenticationProvider;
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

import static org.opensrp.web.HttpHeaderFactory.allowOrigin;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class UserController {
    private String drishtiSiteUrl;
    private DrishtiAuthenticationProvider drishtiAuthenticationProvider;

    @Autowired
    public UserController(@Value("#{drishti['drishti.site.url']}") String drishtiSiteUrl,
                          DrishtiAuthenticationProvider drishtiAuthenticationProvider) {
        this.drishtiSiteUrl = drishtiSiteUrl;
        this.drishtiAuthenticationProvider = drishtiAuthenticationProvider;
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
    public ResponseEntity<UserDetail> userDetail(@RequestParam("anm-id") String anmIdentifier) {
        DrishtiUser user = drishtiAuthenticationProvider.getDrishtiUser(anmIdentifier);
        return new ResponseEntity<>(new UserDetail(user.getUsername(), user.getRoles()), allowOrigin(drishtiSiteUrl), OK);
    }

}
