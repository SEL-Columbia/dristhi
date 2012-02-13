package org.ei.drishti.web.controller;

import org.ei.commcare.api.contract.CommCareFormDefinitions;
import org.ei.commcare.api.util.CommCareListenerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class TestController {
    private final CommCareListenerProperties properties;

    @Autowired
    public TestController(CommCareListenerProperties properties) {
        this.properties = properties;
    }

    @RequestMapping("/test")
    @ResponseBody
    public String test() throws IOException {
        CommCareFormDefinitions formDefinitions = properties.definitions();
        return "User: " + formDefinitions.userName() + ", Password: " + formDefinitions.password();
    }
}
