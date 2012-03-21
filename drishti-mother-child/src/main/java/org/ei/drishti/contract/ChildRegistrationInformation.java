package org.ei.drishti.contract;

import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;

public class ChildRegistrationInformation {
    private String caseId;
    private String motherName;
    private String anmPhoneNumber;
    private String bcgHasBeenProvided;
    private String opv0HasBeenProvided;
    private String hepatitisBHasBeenProvided;

    public ChildRegistrationInformation(String motherName, String anmPhoneNumber, String bcgHasBeenProvided, String opv0HasBeenProvided, String hepatitisBHasBeenProvided) {
        this.motherName = motherName;
        this.anmPhoneNumber = anmPhoneNumber;
        this.bcgHasBeenProvided = bcgHasBeenProvided;
        this.opv0HasBeenProvided = opv0HasBeenProvided;
        this.hepatitisBHasBeenProvided = hepatitisBHasBeenProvided;
    }

    public String mother() {
        return motherName;
    }

    public String anmPhoneNumber() {
        return anmPhoneNumber;
    }

    public List<String> missingVaccinations() {
        List<String> missingVaccinations = new ArrayList<String>();

        addIfMissing(missingVaccinations, "BCG", bcgHasBeenProvided, "BCG_yes");
        addIfMissing(missingVaccinations, "OPV 0", opv0HasBeenProvided, "opv0_yes");
        addIfMissing(missingVaccinations, "Hepatitis B", hepatitisBHasBeenProvided, "hepb1_yes");

        return missingVaccinations;
    }

    private void addIfMissing(List<String> missingVaccinations, String vaccinationName, String vaccinationProvidedData, String expectedValueIfProvided) {
        if (!expectedValueIfProvided.equals(vaccinationProvidedData)) {
            missingVaccinations.add(vaccinationName);
        }
    }

    @Override
    public String toString() {
        return format("ChildRegistrationInformation: '{'Mother = ''{0}'', caseId=''{5}'', anmPhoneNumber=''{1}'', " +
                "bcgHasBeenProvided=''{2}'', opv0HasBeenProvided=''{3}'', hepatitisBHasBeenProvided=''{4}'''}'",
                motherName, anmPhoneNumber, bcgHasBeenProvided, opv0HasBeenProvided, hepatitisBHasBeenProvided, caseId);
    }
}
