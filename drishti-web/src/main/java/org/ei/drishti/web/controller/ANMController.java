package org.ei.drishti.web.controller;

import ch.lambdaj.function.convert.Converter;
import org.ei.drishti.common.AllConstants;
import org.ei.drishti.domain.DrishtiUser;
import org.ei.drishti.dto.ANMDTO;
import org.ei.drishti.service.ANMService;
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

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS}, value = "/anms")
    public ResponseEntity<List<ANMDTO>> all() {
        List<DrishtiUser> anms = anmService.all();
        List<ANMDTO> anmDTOList = convertToDTO(anms);
        HttpHeaders headers = new HttpHeaders();
        headers.add(AllConstants.HTTP.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, drishtiSiteUrl);
        return new ResponseEntity<>(anmDTOList, headers, OK);
    }

    private List<ANMDTO> convertToDTO(List<DrishtiUser> anms) {
        return with(anms).convert(new Converter<DrishtiUser, ANMDTO>() {
            @Override
            public ANMDTO convert(DrishtiUser drishtiUser) {
                return new ANMDTO(drishtiUser.getUsername());
            }
        });
    }
}
