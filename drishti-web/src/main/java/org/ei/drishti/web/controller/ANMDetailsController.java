package org.ei.drishti.web.controller;

import ch.lambdaj.function.convert.Converter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.domain.ANMDetail;
import org.ei.drishti.domain.ANMDetails;
import org.ei.drishti.dto.ANMDTO;
import org.ei.drishti.dto.register.ANMDetailDTO;
import org.ei.drishti.dto.register.ANMDetailsDTO;
import org.ei.drishti.service.ANMDetailsService;
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

import static ch.lambdaj.collection.LambdaCollections.with;
import static org.ei.drishti.web.HttpHeaderFactory.allowOrigin;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ANMDetailsController {
    private static Logger logger = LoggerFactory.getLogger(ANMDetailsController.class.toString());
    private ANMDetailsService anmDetailsService;
    private final String drishtiSiteUrl;
    private String drishtiANMDetailsUrl;
    private UserController userController;
    private HttpAgent httpAgent;

    @Autowired
    public ANMDetailsController(ANMDetailsService anmDetailsService,
                                @Value("#{drishti['drishti.site.url']}") String drishtiSiteUrl,
                                @Value("#{drishti['drishti.anm.details.url']}") String drishtiANMDetailsUrl,
                                UserController userController, HttpAgent httpAgent) {
        this.anmDetailsService = anmDetailsService;
        this.drishtiSiteUrl = drishtiSiteUrl;
        this.drishtiANMDetailsUrl = drishtiANMDetailsUrl;
        this.userController = userController;
        this.httpAgent = httpAgent;
    }

    @RequestMapping(method = GET, value = "/anms")
    @ResponseBody
    public ResponseEntity<ANMDetailsDTO> allANMs() {
        HttpResponse response = new HttpResponse(false, null);
        try {
            String anmIdentifier = userController.currentUser().getUsername();
            response = httpAgent.get(drishtiANMDetailsUrl + "?anm-id=" + anmIdentifier);
            List<ANMDTO> anmBasicDetails = new Gson().fromJson(response.body(),
                    new TypeToken<List<ANMDTO>>() {
                    }.getType());
            ANMDetails anmDetails = anmDetailsService.anmDetails(anmBasicDetails);
            logger.info("Fetched ANM details with beneficiary count.");
            return new ResponseEntity<>(mapToDTO(anmDetails), allowOrigin(drishtiSiteUrl), HttpStatus.OK);
        } catch (Exception exception) {
            logger.error(MessageFormat.format("{0} occurred while fetching ANM Details. StackTrace: \n {1}", exception.getMessage(), ExceptionUtils.getFullStackTrace(exception)));
            logger.error(MessageFormat.format("Response with status {0} and body: {1} was obtained from {2}", response.isSuccess(), response.body(), drishtiANMDetailsUrl));
        }
        return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
    }
    //new change

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
