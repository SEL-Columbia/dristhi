package org.ei.drishti.web.controller;

import org.motechproject.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class TimeController {
    @RequestMapping("/time/set")
    @ResponseBody
    public String setTime(@RequestParam("offset") Integer offsetInSeconds) throws IOException {
        System.setProperty("faketime.offset.seconds", offsetInSeconds.toString());
        return String.valueOf(DateUtil.now().getMillis());
    }
}
