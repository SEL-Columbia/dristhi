package org.ei.drishti.reporting.controller;

import ch.lambdaj.function.convert.Converter;
import org.ei.drishti.common.AllConstants;
import org.ei.drishti.dto.ANMDTO;
import org.ei.drishti.dto.LocationDTO;
import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.domain.SP_ANM;
import org.ei.drishti.reporting.service.ANMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class ANMController {
    private String drishtiSiteUrl;
    private ANMService anmService;

    @Autowired
    public ANMController(@Value("#{drishti['drishti.site.url']}") String drishtiSiteUrl, ANMService anmService) {
        this.drishtiSiteUrl = drishtiSiteUrl;
        this.anmService = anmService;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS}, value = "/anm-details")
    public ResponseEntity<List<ANMDTO>> all() {
        List<SP_ANM> anmList = anmService.all();

        List<ANMDTO> anmDTOList = convertToDTO(anmList);

        HttpHeaders headers = new HttpHeaders();
        headers.add(AllConstants.HTTP.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, drishtiSiteUrl);
        return new ResponseEntity<>(anmDTOList, headers, OK);
    }

    private List<ANMDTO> convertToDTO(List<SP_ANM> anms) {
        return with(anms).convert(new Converter<SP_ANM, ANMDTO>() {
            @Override
            public ANMDTO convert(SP_ANM anm) {
                LocationDTO location = convertToDTO(anmService.getLocation(anm.identifier()));
                return new ANMDTO(anm.identifier(), anm.name(), location);
            }

            private LocationDTO convertToDTO(Location location) {
                return new LocationDTO(location.subCenter(), location.phcName(), location.taluka(), location.district(), location.state());
            }
        });
    }
}
