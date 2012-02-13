package org.ei.commcare.api.contract;

import java.util.List;

public class CommCareFormDefinitions {
    private String userName;
    private String password;
    private List<CommCareFormDefinition> forms;

    public List<CommCareFormDefinition> definitions() {
        return forms;
    }

    public String userName() {
        return userName;
    }

    public String password() {
        return password;
    }
}
