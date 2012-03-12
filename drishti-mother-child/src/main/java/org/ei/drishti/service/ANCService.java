package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareCloseInformation;
import org.ei.drishti.contract.AnteNatalCareEnrollmentInformation;
import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class ANCService {
    private static Logger logger = Logger.getLogger(ANCService.class.toString());

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

        Time preferredAlertTime = new Time(LocalTime.now().plusMinutes(2).withSecondOfMinute(0));
        LocalDate referenceDate = info.lmpDate() != null ? info.lmpDate() : DateUtil.today();

        ancSchedulesService.enrollMother(info.caseId(), referenceDate, preferredAlertTime);
    }

    public void ancCareHasBeenProvided(AnteNatalCareInformation ancInformation) {
        if (!allMothers.motherExists(ancInformation.caseId())) {
            logger.warning("Found care provided without registered mother for case ID: " + ancInformation.caseId());
            return;
        }

        if (ancInformation.anc1Date() != null) {
            ancSchedulesService.ancVisitHasHappened(ancInformation.caseId(), 1, ancInformation.anc1Date());
        }
        if (ancInformation.anc2Date() != null) {
            ancSchedulesService.ancVisitHasHappened(ancInformation.caseId(), 2, ancInformation.anc2Date());
        }
        if (ancInformation.anc3Date() != null) {
            ancSchedulesService.ancVisitHasHappened(ancInformation.caseId(), 3, ancInformation.anc3Date());
        }
        if (ancInformation.anc4Date() != null) {
            ancSchedulesService.ancVisitHasHappened(ancInformation.caseId(), 4, ancInformation.anc4Date());
        }

        if (ancInformation.tt1Date() != null) {
            ancSchedulesService.ttVisitHasHappened(ancInformation.caseId(), 1, ancInformation.tt1Date());
        }

        if (ancInformation.tt2Date() != null) {
            ancSchedulesService.ttVisitHasHappened(ancInformation.caseId(), 2, ancInformation.tt2Date());
        }

        if (ancInformation.ifa1Date() != null) {
            ancSchedulesService.ifaVisitHasHappened(ancInformation.caseId(), 1, ancInformation.ifa1Date());
        }

        if (ancInformation.ifa2Date() != null) {
            ancSchedulesService.ifaVisitHasHappened(ancInformation.caseId(), 2, ancInformation.ifa2Date());
        }
    }

    public void updateANCOutcome(AnteNatalCareOutcomeInformation outcomeInformation) {
    }

    public void closeANCCase(AnteNatalCareCloseInformation closeInformation) {
        if (!allMothers.motherExists(closeInformation.caseId())) {
            logger.warning("Tried to close case without registered mother for case ID: " + closeInformation.caseId());
            return;
        }

        ancSchedulesService.closeCase(closeInformation.caseId());
    }
}
