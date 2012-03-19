package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareCloseInformation;
import org.ei.drishti.contract.AnteNatalCareEnrollmentInformation;
import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.scheduler.service.ANCSchedulesService;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.joda.time.LocalTime.now;

@Service
public class ANCService {
    private static Logger logger = LoggerFactory.getLogger(ANCService.class.toString());

    private final AllMothers allMothers;
    private ANCSchedulesService ancSchedulesService;

    @Autowired
    public ANCService(AllMothers allMothers, ANCSchedulesService ancSchedulesService) {
        this.allMothers = allMothers;
        this.ancSchedulesService = ancSchedulesService;
    }

    public void registerANCCase(AnteNatalCareEnrollmentInformation info) {
        Mother mother = new Mother(info.caseId(), info.thaayiCardNumber(), info.name()).withAnmPhoneNumber(info.anmPhoneNumber()).withLMP(info.lmpDate());
        allMothers.register(mother);

//        Time preferredAlertTime = new Time(LocalTime.now().plusMinutes(2).withSecondOfMinute(0));
        Time preferredAlertTime = new Time(new LocalTime(14, 0));
        LocalDate referenceDate = info.lmpDate() != null ? info.lmpDate() : DateUtil.today();

        ancSchedulesService.enrollMother(info.caseId(), referenceDate, new Time(now()), preferredAlertTime);
    }

    public void ancCareHasBeenProvided(AnteNatalCareInformation ancInformation) {
        if (!allMothers.motherExists(ancInformation.caseId())) {
            logger.warn("Found care provided without registered mother for case ID: " + ancInformation.caseId());
            return;
        }

        for (Map.Entry<Integer, LocalDate> entry : ancInformation.ancVisits().entrySet()) {
            ancSchedulesService.ancVisitHasHappened(ancInformation.caseId(), entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Integer, LocalDate> entry : ancInformation.ttVisits().entrySet()) {
            ancSchedulesService.ttVisitHasHappened(ancInformation.caseId(), entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Integer, LocalDate> entry : ancInformation.ifaVisits().entrySet()) {
            ancSchedulesService.ifaVisitHasHappened(ancInformation.caseId(), entry.getKey(), entry.getValue());
        }
    }

    public void updateANCOutcome(AnteNatalCareOutcomeInformation outcomeInformation) {
    }

    public void closeANCCase(AnteNatalCareCloseInformation closeInformation) {
        if (!allMothers.motherExists(closeInformation.caseId())) {
            logger.warn("Tried to close case without registered mother for case ID: " + closeInformation.caseId());
            return;
        }

        ancSchedulesService.closeCase(closeInformation.caseId());
    }
}
