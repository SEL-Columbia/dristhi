package org.opensrp.web.controller;

import static ch.lambdaj.collection.LambdaCollections.with;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
    
    public static void main(String[] args) throws JSONException, IOException, SQLException {
    	// create a mysql database connection
        //String myDriver = "org.gjt.mm.mysql.Driver";
        String myUrl = "jdbc:mysql://localhost:3306/couchcleanup";
        Connection conn = DriverManager.getConnection(myUrl, "root", "VA1913wm");
        
    	FileOutputStream fdup = new FileOutputStream("d:\\filterduplicate.txt");
    	FileOutputStream fpers = new FileOutputStream("d:\\filterpersist.txt");
		Scanner s = new Scanner(new File("d:\\all_alerts_with_inactive.txt"));
		Map<String, JSONObject> m = new HashMap<>();
		int i = 0;
		while (s.hasNextLine()) {
			String l = s.nextLine();
			if(StringUtils.isNotBlank(l) && l.startsWith("{\"id\":")){
				JSONObject jo = new JSONObject(l.replace("},", "}"));
				System.out.println((++i)+":"+jo);
				if(m.containsKey(makeKey(jo.getJSONArray("key")))){
					fdup.write((jo.getString("id")+";,"+jo.getString("key")+"\n\r").getBytes());
					Statement st = conn.createStatement();

			        // note that i'm leaving "date_created" out of this insert statement
			        st.executeUpdate("INSERT INTO duplicate (`id`,`entityId`,`vaccine`,`key`,`value`)"
			        		+ " VALUES ('"+jo.getString("id")+"', "
	        				+ "'"+jo.getJSONArray("key").getString(0)+"', "
	        				+ "'"+jo.getJSONArray("key").getString(1)+"', "
	        				+ "'"+jo.getString("key")+"', "
	        				+ "'"+jo.getString("value")+"'); ");
				}
				else {
					m.put(makeKey(jo.getJSONArray("key")), jo);
					fpers.write((jo.getString("id")+";,"+jo.getString("key")+"\n\r").getBytes());
					Statement st = conn.createStatement();

			        // note that i'm leaving "date_created" out of this insert statement
			        st.executeUpdate("INSERT INTO persist (`id`,`entityId`,`vaccine`,`key`,`value`)"
			        		+ " VALUES ('"+jo.getString("id")+"', "
	        				+ "'"+jo.getJSONArray("key").getString(0)+"', "
	        				+ "'"+jo.getJSONArray("key").getString(1)+"', "
	        				+ "'"+jo.getString("key")+"', "
	        				+ "'"+jo.getString("value")+"'); ");
				}
			}
		}
        conn.close();
        fdup.close();
        fpers.close();
        s.close();
    }
    
    private static String makeKey(JSONArray keyArr) throws JSONException {
		return keyArr.getString(0)+":"+keyArr.getString(1);
	}
    
}

