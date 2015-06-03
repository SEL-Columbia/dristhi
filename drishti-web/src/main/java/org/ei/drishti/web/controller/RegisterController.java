package org.ei.drishti.web.controller;

import org.ei.drishti.domain.register.*;
import org.ei.drishti.dto.register.*;
import org.ei.drishti.mapper.*;
import org.ei.drishti.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class RegisterController {
	 private static Logger logger = LoggerFactory.getLogger(RegisterController.class.toString());
    private ANCRegisterService ancRegisterService;
    private PNCRegisterService pncRegisterService;
    private ECRegisterService ecRegisterService;
    private ChildRegisterService childRegisterService;
    private FPRegisterService fpRegisterService;
    private ANCRegisterMapper ancRegisterMapper;
    private ECRegisterMapper ecRegisterMapper;
    private ChildRegisterMapper childRegisterMapper;
    private FPRegisterMapper fpRegisterMapper;
    private PNCRegisterMapper pncRegisterMapper;

    @Autowired
    public RegisterController(ANCRegisterService ancRegisterService,
                              PNCRegisterService pncRegisterService,
                              ECRegisterService ecRegisterService,
                              ChildRegisterService childRegisterService,
                              FPRegisterService fpRegisterService,
                              ANCRegisterMapper ancRegisterMapper,
                              ECRegisterMapper ecRegisterMapper,
                              ChildRegisterMapper childRegisterMapper,
                              FPRegisterMapper fpRegisterMapper,
                              PNCRegisterMapper pncRegisterMapper) {
        this.ancRegisterService = ancRegisterService;
        this.ecRegisterService = ecRegisterService;
        this.pncRegisterService = pncRegisterService;
        this.childRegisterService = childRegisterService;
        this.fpRegisterService = fpRegisterService;
        this.ancRegisterMapper = ancRegisterMapper;
        this.ecRegisterMapper = ecRegisterMapper;
        this.childRegisterMapper = childRegisterMapper;
        this.fpRegisterMapper = fpRegisterMapper;
        this.pncRegisterMapper = pncRegisterMapper;
    }

    @RequestMapping(method = GET, value = "/registers/ec")
    @ResponseBody
    public ResponseEntity<ECRegisterDTO> ecRegister(@RequestParam("anm-id") String anmIdentifier) {
        ECRegister ecRegister = ecRegisterService.getRegisterForANM(anmIdentifier);
        
        logger.info("fetched ecregisters for " + anmIdentifier);
        return new ResponseEntity<>(ecRegisterMapper.mapToDTO(ecRegister), HttpStatus.OK);
    }

    @RequestMapping(method = GET, value = "/registers/anc")
    @ResponseBody
    public ResponseEntity<ANCRegisterDTO> ancRegister(@RequestParam("anm-id") String anmIdentifier) {
        ANCRegister ancRegister = ancRegisterService.getRegisterForANM(anmIdentifier);
        return new ResponseEntity<>(ancRegisterMapper.mapToDTO(ancRegister), HttpStatus.OK);
    }

    @RequestMapping(method = GET, value = "/registers/child")
    @ResponseBody
    public ResponseEntity<ChildRegisterDTO> childRegister(@RequestParam("anm-id") String anmIdentifier) {
        ChildRegister childRegister = childRegisterService.getRegisterForANM(anmIdentifier);
        return new ResponseEntity<>(childRegisterMapper.mapToDTO(childRegister), HttpStatus.OK);
    }

    @RequestMapping(method = GET, value = "/registers/fp")
    @ResponseBody
    public ResponseEntity<FPRegisterDTO> fpRegister(@RequestParam("anm-id") String anmIdentifier) {
        FPRegister fpRegister = fpRegisterService.getRegisterForANM(anmIdentifier);
        return new ResponseEntity<>(fpRegisterMapper.mapToDTO(fpRegister), HttpStatus.OK);

    }

    @RequestMapping(method = GET, value = "/registers/pnc")
    @ResponseBody
    public ResponseEntity<PNCRegisterDTO> pncRegister(@RequestParam("anm-id") String anmIdentifier) {
        PNCRegister pncRegister = pncRegisterService.getRegisterForANM(anmIdentifier);
        return new ResponseEntity<>(pncRegisterMapper.mapToDTO(pncRegister), HttpStatus.OK);
    }
}
