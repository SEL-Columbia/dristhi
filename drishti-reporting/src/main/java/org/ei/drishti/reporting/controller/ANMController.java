package org.ei.drishti.reporting.controller;

import ch.lambdaj.function.convert.Converter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ei.drishti.common.domain.UserDetail;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.dto.ANMDTO;
import org.ei.drishti.dto.LocationDTO;
import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.domain.SP_ANM;
import org.ei.drishti.reporting.factory.DetailsFetcherFactory;
import org.ei.drishti.reporting.service.ANMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;
import static org.ei.drishti.reporting.HttpHeaderFactory.allowOrigin;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class ANMController {
    private String drishtiSiteUrl;
    private String dristhiUserDetailsUrl;
    private ANMService anmService;
    private HttpAgent httpAgent;
    private DetailsFetcherFactory detailsFetcherFactory;

    @Autowired
    public ANMController(@Value("#{drishti['drishti.site.url']}") String drishtiSiteUrl,
                         @Value("#{drishti['drishti.user.details.url']}") String dristhiUserDetailsUrl,
                         ANMService anmService, HttpAgent httpAgent, DetailsFetcherFactory detailsFetcherFactory) {
        this.drishtiSiteUrl = drishtiSiteUrl;
        this.dristhiUserDetailsUrl = dristhiUserDetailsUrl;
        this.anmService = anmService;
        this.httpAgent = httpAgent;
        this.detailsFetcherFactory = detailsFetcherFactory;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS}, value = "/anm-details")
    @ResponseBody
    public ResponseEntity<List<ANMDTO>> all(@RequestParam("anm-id") String anmIdentifier) {
        HttpResponse response;
        try {
            response = httpAgent.get(dristhiUserDetailsUrl + "?anm-id=" + anmIdentifier);
            UserDetail userDetail = new Gson().fromJson(response.body(),
                    new TypeToken<UserDetail>() {
                    }.getType()
            );
            ANMDetailsFetcher anmDetailsFetcher = detailsFetcherFactory.detailsFetcher(userDetail.roles());

            List<ANMDTO> anmDTOList = convertToDTO(anmDetailsFetcher.fetchDetails(anmIdentifier));
            return new ResponseEntity<>(anmDTOList, allowOrigin(drishtiSiteUrl), OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, allowOrigin(drishtiSiteUrl), OK);
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