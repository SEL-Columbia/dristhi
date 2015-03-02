package org.opensrp.reporting.controller;

import ch.lambdaj.function.convert.Converter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.opensrp.common.domain.UserDetail;
import org.opensrp.common.util.HttpAgent;
import org.opensrp.common.util.HttpResponse;
import org.opensrp.dto.ANMDTO;
import org.opensrp.dto.LocationDTO;
import org.opensrp.reporting.domain.Location;
import org.opensrp.reporting.domain.SP_ANM;
import org.opensrp.reporting.factory.DetailsFetcherFactory;
import org.opensrp.reporting.service.ANMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.opensrp.reporting.HttpHeaderFactory.allowOrigin;
import static ch.lambdaj.collection.LambdaCollections.with;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class ANMController {
    private String opensrpSiteUrl;
    private String opensrpUserDetailsUrl;
    private ANMService anmService;
    private HttpAgent httpAgent;
    private DetailsFetcherFactory detailsFetcherFactory;

    @Autowired
    public ANMController(@Value("#{opensrp['opensrp.site.url']}") String opensrpSiteUrl,
                         @Value("#{opensrp['opensrp.user.details.url']}") String opensrpUserDetailsUrl,
                         ANMService anmService, HttpAgent httpAgent, DetailsFetcherFactory detailsFetcherFactory) {
        this.opensrpSiteUrl = opensrpSiteUrl;
        this.opensrpUserDetailsUrl = opensrpUserDetailsUrl;
        this.anmService = anmService;
        this.httpAgent = httpAgent;
        this.detailsFetcherFactory = detailsFetcherFactory;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS}, value = "/anm-details")
    @ResponseBody
    public ResponseEntity<List<ANMDTO>> all(@RequestParam("anm-id") String anmIdentifier) {
        HttpResponse response;
        try {
            response = httpAgent.get(opensrpUserDetailsUrl + "?anm-id=" + anmIdentifier);
            UserDetail userDetail = new Gson().fromJson(response.body(),
                    new TypeToken<UserDetail>() {
                    }.getType()
            );
            ANMDetailsFetcher anmDetailsFetcher = detailsFetcherFactory.detailsFetcher(userDetail.roles());

            List<ANMDTO> anmDTOList = convertToDTO(anmDetailsFetcher.fetchDetails(anmIdentifier));
            return new ResponseEntity<>(anmDTOList, allowOrigin(opensrpSiteUrl), OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, allowOrigin(opensrpSiteUrl), OK);
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
