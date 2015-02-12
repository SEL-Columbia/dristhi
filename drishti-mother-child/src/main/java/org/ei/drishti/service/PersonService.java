package org.ei.drishti.service;

import static org.ei.drishti.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.CURRENT_FP_METHOD_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.DMPA_INJECTION_DATE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.FP_METHOD_CHANGE_DATE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.NUMBER_OF_CONDOMS_SUPPLIED_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.NUMBER_OF_OCP_STRIPS_SUPPLIED_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.OCP_REFILL_DATE_FIELD_NAME;

import java.util.List;

import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.domain.Person;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllPersons;
import org.ei.drishti.service.formSubmission.handler.ReportFieldsDefinition;
import org.ei.drishti.service.scheduling.PersonSchedulesService;
import org.ei.drishti.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
	private static Logger logger = LoggerFactory.getLogger(ActionService.class.toString());
	 private AllPersons allPersons;
	 private ReportFieldsDefinition reportFieldsDefinition;
	 private PersonSchedulesService schedulingService;

	 public void registerPerson(FormSubmission submission) {
	     /*   Person person = allPersons.findByCaseId(submission.entityId());
	        

	        allPersons.update(person.withANMIdentifier(submission.anmId()));

	        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
	        

	        FPProductInformation fpProductInformation = new FPProductInformation(
	                submission.entityId(), submission.anmId(),
	                submission.getField(CURRENT_FP_METHOD_FIELD_NAME),
	                null, submission.getField(DMPA_INJECTION_DATE_FIELD_NAME),
	                submission.getField(NUMBER_OF_OCP_STRIPS_SUPPLIED_FIELD_NAME),
	                submission.getField(OCP_REFILL_DATE_FIELD_NAME),
	                submission.getField(NUMBER_OF_CONDOMS_SUPPLIED_FIELD_NAME),
	                submission.getField(SUBMISSION_DATE_FIELD_NAME),
	                submission.getField(FP_METHOD_CHANGE_DATE_FIELD_NAME), null, null, null);*/

	       // schedulingService.registerEC(fpProductInformation);
	    }
}
