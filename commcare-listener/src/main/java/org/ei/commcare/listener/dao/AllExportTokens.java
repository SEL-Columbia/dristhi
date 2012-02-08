package org.ei.commcare.listener.dao;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class AllExportTokens {
    private HashMap<String, String> map;

    public AllExportTokens() {
        this.map = new HashMap<String, String>();
    }

    public String findByNamespace(String nameSpace) {
        String token = map.get(nameSpace);
        return token == null ? "" : token;
    }

    public void updateToken(String nameSpace, String tokenData) {
        map.put(nameSpace, tokenData);
    }
}
