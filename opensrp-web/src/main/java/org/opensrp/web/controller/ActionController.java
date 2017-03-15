package org.opensrp.web.controller;

import static ch.lambdaj.collection.LambdaCollections.with;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.opensrp.domain.Client;
import org.opensrp.dto.Action;
import org.opensrp.repository.AllClients;
import org.opensrp.scheduler.Alert;
import org.opensrp.scheduler.repository.AllAlerts;
import org.opensrp.scheduler.service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.lambdaj.function.convert.Converter;

@Controller
public class ActionController {
    private ActionService actionService;
    private AllClients allClients;
    private AllAlerts allAlerts;

    @Autowired
    public ActionController(ActionService actionService, AllClients c, AllAlerts allAlerts) {
        this.actionService = actionService;
        this.allClients = c;
        this.allAlerts = allAlerts;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/actions")
    @ResponseBody
    public List<Action> getNewActionForANM(@RequestParam("anmIdentifier") String anmIdentifier, @RequestParam("timeStamp") Long timeStamp){
        List<org.opensrp.scheduler.Action> actions = actionService.getNewAlertsForANM(anmIdentifier, timeStamp);
        return with(actions).convert(new Converter<org.opensrp.scheduler.Action, Action>() {
            @Override
            public Action convert(org.opensrp.scheduler.Action action) {
                return ActionConvertor.from(action);
            }
        });
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/useractions")
    @ResponseBody
    public List<Action> getNewActionForClient(@RequestParam("baseEntityId") String baseEntityId, @RequestParam("timeStamp") Long timeStamp){
        List<org.opensrp.scheduler.Action> actions = actionService.findByCaseIdAndTimeStamp(baseEntityId, timeStamp);
        return with(actions).convert(new Converter<org.opensrp.scheduler.Action, Action>() {
            @Override
            public Action convert(org.opensrp.scheduler.Action action) {
                return ActionConvertor.from(action);
            }
        });
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/alert_delete")
    @ResponseBody
    public void deleteDuplicateAlerts(@RequestParam("key") String key){
    	if(!key.equalsIgnoreCase("20160727KiSafaiMuhim")){
    		throw new RuntimeException("Invalid Key");
    	}
        for (Client c : allClients.findAllClients()) {
			List<Alert> al = allAlerts.findActiveAlertByEntityId(c.getBaseEntityId());
			Logger.getLogger(getClass()).warn(al.size()+" Alerts for "+c.getBaseEntityId());
			Map<String, Alert> am = new HashMap<>();
			for (Alert a : al) {
				if(am.containsKey(a.triggerName())){
					Logger.getLogger(getClass()).warn("Removing trigger "+a.triggerName());
					allAlerts.safeRemove(a);
				}
				else {
					am.put(a.triggerName(), a);					
				}
			}
		}
    }
    
}

