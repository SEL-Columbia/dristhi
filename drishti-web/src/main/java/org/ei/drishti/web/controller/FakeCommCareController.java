package org.ei.drishti.web.controller;

import org.ei.drishti.scheduler.CommCareScheduler;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class FakeCommCareController {
    private CommCareScheduler commCareScheduler;

    @Autowired
    public FakeCommCareController(CommCareScheduler commCareScheduler) {
        this.commCareScheduler = commCareScheduler;
    }

    @RequestMapping("/form/fetch")
    @ResponseBody
    public String runScheduler() throws Exception {
        commCareScheduler.fetchFromCommCareHQ(null);
        return "Done";
    }
}
