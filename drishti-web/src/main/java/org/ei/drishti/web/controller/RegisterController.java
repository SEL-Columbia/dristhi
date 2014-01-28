package org.ei.drishti.web.controller;

import org.ei.drishti.domain.register.ANCRegister;
import org.ei.drishti.domain.register.ECRegister;
import org.ei.drishti.dto.register.ANCRegisterDTO;
import org.ei.drishti.dto.register.ECRegisterDTO;
import org.ei.drishti.mapper.ANCRegisterMapper;
import org.ei.drishti.mapper.ECRegisterMapper;
import org.ei.drishti.service.ANCRegisterService;
import org.ei.drishti.service.ECRegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.ei.drishti.web.HttpHeaderFactory.allowOrigin;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class RegisterController {
    private static Logger logger = LoggerFactory.getLogger(RegisterController.class.toString());
    private ANCRegisterService ancRegisterService;
    private ECRegisterService ecRegisterService;
    private ANCRegisterMapper ancRegisterMapper;
    private ECRegisterMapper ecRegisterMapper;
    private final String drishtiSiteUrl;

    @Autowired
    public RegisterController(ANCRegisterService ancRegisterService,
                              ECRegisterService ecRegisterService,
                              ANCRegisterMapper ancRegisterMapper,
                              ECRegisterMapper ecRegisterMapper,
                              @Value("#{drishti['drishti.site.url']}") String drishtiSiteUrl) {
        this.ancRegisterService = ancRegisterService;
        this.ecRegisterService = ecRegisterService;
        this.ancRegisterMapper = ancRegisterMapper;
        this.ecRegisterMapper = ecRegisterMapper;
        this.drishtiSiteUrl = drishtiSiteUrl;
    }

    @RequestMapping(method = GET, value = "/registers/anc")
    @ResponseBody
    public ResponseEntity<ANCRegisterDTO> getANCRegister(@RequestParam("anm-id") String anmIdentifier) {
        ANCRegister ancRegister = ancRegisterService.getRegisterForANM(anmIdentifier);
        return new ResponseEntity<>(ancRegisterMapper.mapToDTO(ancRegister), allowOrigin(drishtiSiteUrl), HttpStatus.OK);
    }

    @RequestMapping(method = GET, value = "/registers/ec")
    @ResponseBody
    public ResponseEntity<ECRegisterDTO> getECRegister(@RequestParam("anm-id") String anmIdentifier) {
        ECRegister ecRegister = ecRegisterService.getRegisterForANM(anmIdentifier);
        return new ResponseEntity<>(ecRegisterMapper.mapToDTO(ecRegister), allowOrigin(drishtiSiteUrl), HttpStatus.OK);
    }
}
