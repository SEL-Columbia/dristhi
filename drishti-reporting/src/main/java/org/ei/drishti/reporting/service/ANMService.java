package org.ei.drishti.reporting.service;

import org.ei.drishti.reporting.domain.SP_ANM;
import org.ei.drishti.reporting.repository.AllSP_ANMsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ANMService {
    private final AllSP_ANMsRepository allANMsRepository;

    @Autowired
    public ANMService(AllSP_ANMsRepository allANMsRepository) {
        this.allANMsRepository = allANMsRepository;
    }

    public List<SP_ANM> all() {
        return allANMsRepository.fetchAll();
    }
}
