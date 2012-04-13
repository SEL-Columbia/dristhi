package org.ei.drishti.web.controller;

import com.google.gson.Gson;
import org.ei.drishti.repository.AllAlertActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FakeAlertController {
    private AllAlertActions allAlertActions;

    @Autowired
    public FakeAlertController(AllAlertActions allAlertActions) {
        this.allAlertActions = allAlertActions;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/alert/submit")
    @ResponseBody
    public String submitFakeAlert(@RequestParam("formData") String formData) throws Exception {
        try {
            allAlertActions.add(new Gson().fromJson(formData, AlertActionItem.class).toAlertAction());
        } catch (Exception e) {
            return "Failed: " + e.getMessage();
        }

        return "Success!";
    }

}
