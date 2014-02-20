package org.ei.drishti.web.controller;

import org.ei.drishti.web.HttpHeaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.ei.drishti.web.HttpHeaderFactory.allowOrigin;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class UserController {
    private String drishtiSiteUrl;

    @Autowired
    public UserController(@Value("#{drishti['drishti.site.url']}") String drishtiSiteUrl) {
        this.drishtiSiteUrl = drishtiSiteUrl;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/authenticate-user")
    public ResponseEntity<HttpStatus> authenticateUser() {
        return new ResponseEntity<>(null, allowOrigin(drishtiSiteUrl), OK);
    }
}
