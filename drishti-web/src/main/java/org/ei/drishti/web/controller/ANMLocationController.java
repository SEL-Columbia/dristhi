package org.ei.drishti.web.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.dto.VillagesDTO;
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

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ANMLocationController {
    private static Logger logger = LoggerFactory.getLogger(ANMLocationController.class.toString());
    private final String drishtiANMVillagesURL;
    private UserController userController;
    private HttpAgent httpAgent;

    @Autowired
    public ANMLocationController(@Value("#{drishti['drishti.anm.villages.url']}") String drishtiANMVillagesURL,
                                 UserController userController,
                                 HttpAgent httpAgent) {
        this.drishtiANMVillagesURL = drishtiANMVillagesURL;
        this.userController = userController;
        this.httpAgent = httpAgent;
    }

    @RequestMapping(method = GET, value = "/anm-villages")
    @ResponseBody
    public ResponseEntity<VillagesDTO> villagesForANM() {
        HttpResponse response = new HttpResponse(false, null);
        try {
            String anmIdentifier = userController.currentUser().getUsername();
            response = httpAgent.get(drishtiANMVillagesURL + "?anm-id=" + anmIdentifier);
            VillagesDTO villagesDTOs = new Gson().fromJson(response.body(),
                    new TypeToken<VillagesDTO>() {
                    }.getType());
            logger.info("Fetched Villages for the ANM");
            return new ResponseEntity<>(villagesDTOs, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error(MessageFormat.format("{0} occurred while fetching Village Details for anm. StackTrace: \n {1}", exception.getMessage(), ExceptionUtils.getFullStackTrace(exception)));
            logger.error(MessageFormat.format("Response with status {0} and body: {1} was obtained from {2}", response.isSuccess(), response.body(), drishtiANMVillagesURL));
        }
        return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
    }
}
