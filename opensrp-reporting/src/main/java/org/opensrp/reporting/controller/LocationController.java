package org.opensrp.reporting.controller;

import org.opensrp.dto.VillagesDTO;
import org.opensrp.reporting.domain.Location;
import org.opensrp.reporting.service.ANMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class LocationController {
    private ANMService anmService;

    @Autowired
    public LocationController(ANMService anmService) {
        this.anmService = anmService;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS}, value = "/villages")
    public ResponseEntity<VillagesDTO> villagesForANM(@RequestParam("anm-id") String anmIdentifier) {
        List villagesForANM = anmService.getVillagesForANM(anmIdentifier);
        VillagesDTO villagesDTO = null;
        if (villagesForANM != null) {
            Location anmLocation = (Location) villagesForANM.get(0);
            List<String> villages = collect(villagesForANM, on(Location.class).village());
            villagesDTO = new VillagesDTO(anmLocation.district().toLowerCase(),
                    anmLocation.phcName(),
                    anmLocation.phc().phcIdentifier(),
                    anmLocation.subCenter(),
                    villages);
        }
        return new ResponseEntity<>(villagesDTO, OK);
    }
}
