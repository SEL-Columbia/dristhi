package org.opensrp.register.action;

import static org.opensrp.register.RegisterConstants.Form.ANC_CLOSE;
import static org.opensrp.register.RegisterConstants.Form.ANC_INVESTIGATIONS;
import static org.opensrp.register.RegisterConstants.Form.ANC_REGISTRATION;
import static org.opensrp.register.RegisterConstants.Form.ANC_REGISTRATION_OA;
import static org.opensrp.register.RegisterConstants.Form.ANC_VISIT;
import static org.opensrp.register.RegisterConstants.Form.CHILD_CLOSE;
import static org.opensrp.register.RegisterConstants.Form.CHILD_IMMUNIZATIONS;
import static org.opensrp.register.RegisterConstants.Form.CHILD_REGISTRATION_OA;
import static org.opensrp.register.RegisterConstants.Form.DELIVERY_OUTCOME;
import static org.opensrp.register.RegisterConstants.Form.DELIVERY_PLAN;
import static org.opensrp.register.RegisterConstants.Form.HB_TEST;
import static org.opensrp.register.RegisterConstants.Form.IFA;
import static org.opensrp.register.RegisterConstants.Form.TT;
import static org.opensrp.register.RegisterConstants.Form.TT_1;
import static org.opensrp.register.RegisterConstants.Form.TT_2;
import static org.opensrp.register.RegisterConstants.Form.TT_BOOSTER;
import static org.opensrp.register.RegisterConstants.Form.VITAMIN_A;

import org.opensrp.register.RegisterConstants;
import org.opensrp.register.service.handler.ANCCloseHandler;
import org.opensrp.register.service.handler.ANCInvestigationsHandler;
import org.opensrp.register.service.handler.ANCRegistrationHandler;
import org.opensrp.register.service.handler.ANCRegistrationOAHandler;
import org.opensrp.register.service.handler.ANCVisitHandler;
import org.opensrp.register.service.handler.ChildCloseHandler;
import org.opensrp.register.service.handler.ChildImmunizationsHandler;
import org.opensrp.register.service.handler.ChildRegistrationOAHandler;
import org.opensrp.register.service.handler.DeliveryOutcomeHandler;
import org.opensrp.register.service.handler.DeliveryPlanHandler;
import org.opensrp.register.service.handler.HbTestHandler;
import org.opensrp.register.service.handler.IFAHandler;
import org.opensrp.register.service.handler.TTHandler;
import org.opensrp.register.service.handler.VitaminAHandler;
import org.opensrp.register.ziggy.domain.Child;
import org.opensrp.register.ziggy.domain.Mother;
import org.opensrp.service.formSubmission.handler.HandlerMapper;
import org.opensrp.service.formSubmission.ziggy.EntityDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FormZiggyMapper {

    @Autowired
    public FormZiggyMapper(EntityDataMap edMap, HandlerMapper handlerMapper,
            ANCRegistrationHandler ancRegistrationHandler,
            ANCRegistrationOAHandler ancRegistrationOAHandler,
            ANCVisitHandler ancVisitHandler,
            ANCCloseHandler ancCloseHandler,
            TTHandler ttHandler,
            IFAHandler ifaHandler,
            HbTestHandler hbTestHandler,
            DeliveryOutcomeHandler deliveryOutcomeHandler,
            ChildRegistrationOAHandler childRegistrationOAHandler,
            VitaminAHandler vitaminAHandler,
            ChildImmunizationsHandler childImmunizationsHandler,
            ChildCloseHandler childCloseHandler,
            DeliveryPlanHandler deliveryPlanHandler,
            ANCInvestigationsHandler ancInvestigationsHandler) {
    	
    	System.out.println("Adding Custom entities to EntityDataMap (ZiggyService)");
        edMap.addEntity(RegisterConstants.FormEntityTypes.MOTHER_TYPE, Mother.class);
        edMap.addEntity(RegisterConstants.FormEntityTypes.CHILD_TYPE, Child.class);

    	System.out.println("Registering Form Custom Handlers for Form Submission Router for complex logic");

        handlerMapper
        .addHandler(ANC_REGISTRATION, ancRegistrationHandler)
        .addHandler(ANC_REGISTRATION_OA, ancRegistrationOAHandler)
        .addHandler(ANC_VISIT, ancVisitHandler)
        .addHandler(ANC_CLOSE, ancCloseHandler)
        .addHandler(TT, ttHandler)
        .addHandler(TT_BOOSTER, ttHandler)
        .addHandler(TT_1, ttHandler)
        .addHandler(TT_2, ttHandler)
        .addHandler(IFA, ifaHandler)
        .addHandler(HB_TEST, hbTestHandler)
        .addHandler(DELIVERY_OUTCOME, deliveryOutcomeHandler)
        .addHandler(DELIVERY_PLAN, deliveryPlanHandler)
        .addHandler(CHILD_REGISTRATION_OA, childRegistrationOAHandler)
        .addHandler(CHILD_IMMUNIZATIONS, childImmunizationsHandler)
        .addHandler(CHILD_CLOSE, childCloseHandler)
        .addHandler(VITAMIN_A, vitaminAHandler)
        .addHandler(ANC_INVESTIGATIONS, ancInvestigationsHandler);
    }
}
