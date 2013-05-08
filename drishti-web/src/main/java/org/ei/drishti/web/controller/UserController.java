package org.ei.drishti.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.HttpStatus.OK;

@Controller
public class UserController {
    @RequestMapping(method = RequestMethod.GET, value = "/authenticate-user")
    public ResponseEntity<HttpStatus> authenticateUser() {
        return new ResponseEntity<>(OK);
    }
}
