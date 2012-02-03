package org.ei.commcare.contract;

import java.util.List;

public class CommcareFormDefinitions {
    private String userName;
    private String password;
    private List<CommcareFormDefinition> forms;

    public List<CommcareFormDefinition> definitions() {
        return forms;
    }

    public String userName() {
        return userName;
    }

    public String password() {
        return password;
    }
}
