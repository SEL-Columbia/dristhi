package org.ei.drishti.web.controller;

import ch.lambdaj.function.convert.Converter;

import org.ei.drishti.dto.Action;

import org.ei.drishti.service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;

@Controller
public class ActionController {
    private ActionService actionService;
    private static Logger logger = LoggerFactory
    		.getLogger(ActionController.class.toString());
    @Autowired
    public ActionController(ActionService actionService) {
        this.actionService = actionService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/actions")
    @ResponseBody
    public List<Action> getNewActionForANM(@RequestParam("anmIdentifier") String anmIdentifier, @RequestParam("timeStamp") Long timeStamp){
    	logger.info("action controller");
        List<org.ei.drishti.domain.Action> actions = actionService.getNewAlertsForANM(anmIdentifier, timeStamp);
        logger.info("response return");
        return with(actions).convert(new Converter<org.ei.drishti.domain.Action, Action>() {
            @Override
            public Action convert(org.ei.drishti.domain.Action action) {
                return ActionConvertor.from(action);
            }
        });
    }
}
