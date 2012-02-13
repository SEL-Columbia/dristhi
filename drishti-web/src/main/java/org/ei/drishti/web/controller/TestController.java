package org.ei.drishti.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class TestController {
    @RequestMapping("/test")
    @ResponseBody
    public String test() throws IOException {
        return "Hello World!";
    }
}
