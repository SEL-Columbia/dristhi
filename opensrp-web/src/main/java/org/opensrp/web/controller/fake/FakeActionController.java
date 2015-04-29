package org.opensrp.web.controller.fake;

import com.google.gson.Gson;

import org.opensrp.scheduler.repository.AllActions;
import org.opensrp.dto.Action;
import org.opensrp.web.controller.ActionConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FakeActionController {
    private AllActions allActions;

    @Autowired
    public FakeActionController(AllActions allActions) {
        this.allActions = allActions;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/action/submit")
    @ResponseBody
    public String submitFakeAction(@RequestParam("formData") String formData, @RequestParam("anmIdentifier") String anmIdentifier) throws Exception {
        allActions.add(ActionConvertor.toAction(new Gson().fromJson(formData, Action.class), anmIdentifier));
        return "Success!";
    }

}
