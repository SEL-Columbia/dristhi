package org.ei.drishti.web.controller;

import org.ei.drishti.dto.ANMDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class ANMController {
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS}, value = "/anms")
    public ResponseEntity<List<ANMDTO>> all() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "http://localhost:9000");
        return new ResponseEntity<>(
                asList(
                        new ANMDTO("c", "bherya - a"),
                        new ANMDTO("demo1", "bherya - b"),
                        new ANMDTO("demo2", "keelanapura")),
                headers,
                OK);
    }
}
