package org.opensrp.web.controller;

import ch.lambdaj.function.convert.Converter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.opensrp.common.util.HttpAgent;
import org.opensrp.common.util.HttpResponse;
import org.opensrp.domain.ANMDetail;
import org.opensrp.domain.ANMDetails;
import org.opensrp.service.ANMDetailsService;
import org.opensrp.dto.ANMDTO;
import org.opensrp.dto.register.ANMDetailDTO;
import org.opensrp.dto.register.ANMDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.MessageFormat;
import java.util.List;

import static org.opensrp.web.HttpHeaderFactory.allowOrigin;
import static ch.lambdaj.collection.LambdaCollections.with;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ANMDetailsController {
    private static Logger logger = LoggerFactory.getLogger(ANMDetailsController.class.toString());
    private ANMDetailsService anmDetailsService;
    private final String opensrpSiteUrl;
    private String opensrpANMDetailsUrl;
    private UserController userController;
    private HttpAgent httpAgent;

    @Autowired
    public ANMDetailsController(ANMDetailsService anmDetailsService,
                                @Value("#{opensrp['opensrp.site.url']}") String opensrpSiteUrl,
                                @Value("#{opensrp['opensrp.anm.details.url']}") String opensrpANMDetailsUrl,
                                UserController userController, HttpAgent httpAgent) {
        this.anmDetailsService = anmDetailsService;
        this.opensrpSiteUrl = opensrpSiteUrl;
        this.opensrpANMDetailsUrl = opensrpANMDetailsUrl;
        this.userController = userController;
        this.httpAgent = httpAgent;
    }

    @RequestMapping(method = GET, value = "/anms")
    @ResponseBody
    public ResponseEntity<ANMDetailsDTO> allANMs() {
        HttpResponse response = new HttpResponse(false, null);
        try {
            String anmIdentifier = userController.currentUser().getUsername();
            response = httpAgent.get(opensrpANMDetailsUrl + "?anm-id=" + anmIdentifier);
            List<ANMDTO> anmBasicDetails = new Gson().fromJson(response.body(),
                    new TypeToken<List<ANMDTO>>() {
                    }.getType());
            ANMDetails anmDetails = anmDetailsService.anmDetails(anmBasicDetails);
            logger.info("Fetched ANM details with beneficiary count.");
            return new ResponseEntity<>(mapToDTO(anmDetails), allowOrigin(opensrpSiteUrl), HttpStatus.OK);
        } catch (Exception exception) {
            logger.error(MessageFormat.format("{0} occurred while fetching ANM Details. StackTrace: \n {1}", exception.getMessage(), ExceptionUtils.getFullStackTrace(exception)));
            logger.error(MessageFormat.format("Response with status {0} and body: {1} was obtained from {2}", response.isSuccess(), response.body(), opensrpANMDetailsUrl));
        }
        return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
    }

    private ANMDetailsDTO mapToDTO(ANMDetails anmDetails) {
        List<ANMDetailDTO> anmDetailsDTO =
                with(anmDetails.anmDetails())
                        .convert(new Converter<ANMDetail, ANMDetailDTO>() {
                            @Override
                            public ANMDetailDTO convert(ANMDetail entry) {
                                return new ANMDetailDTO(entry.identifier(),
                                        entry.name(),
                                        entry.location(),
                                        entry.ecCount(),
                                        entry.fpCount(),
                                        entry.ancCount(),
                                        entry.pncCount(),
                                        entry.childCount());
                            }
                        });
        return new ANMDetailsDTO(anmDetailsDTO);
    }
}
