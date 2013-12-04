package org.ei.drishti.service;

import org.ei.drishti.domain.DrishtiUser;
import org.ei.drishti.repository.AllDrishtiUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ANMService {
    private AllDrishtiUsers allDrishtiUsers;

    @Autowired
    public ANMService(AllDrishtiUsers allDrishtiUsers) {
        this.allDrishtiUsers = allDrishtiUsers;
    }

    public List<DrishtiUser> all() {
        return allDrishtiUsers.allANMs();
    }
}
