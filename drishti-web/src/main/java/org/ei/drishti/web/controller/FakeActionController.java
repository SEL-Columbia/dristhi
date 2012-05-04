package org.ei.drishti.web.controller;

import com.google.gson.Gson;
import org.ei.drishti.repository.AllActions;
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

    @RequestMapping(method = RequestMethod.POST, value = "/alert/submit")
    @ResponseBody
    public String submitFakeAction(@RequestParam("formData") String formData) throws Exception {
        try {
            allActions.add(new Gson().fromJson(formData, ActionItem.class).toAction());
        } catch (Exception e) {
            return "Failed: " + e.getMessage();
        }

        return "Success!";
    }

}
