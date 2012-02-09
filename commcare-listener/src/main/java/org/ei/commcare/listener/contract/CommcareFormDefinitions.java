package org.ei.commcare.listener.contract;

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
