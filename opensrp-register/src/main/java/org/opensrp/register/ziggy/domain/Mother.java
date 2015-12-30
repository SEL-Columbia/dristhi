package org.opensrp.register.ziggy.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;
import org.opensrp.common.util.IntegerUtil;
import org.opensrp.register.RegisterConstants;

@TypeDiscriminator("doc.type === 'Mother'")
public class Mother extends MotechBaseDataObject {
    @JsonProperty
    private String caseId;
    @JsonProperty
    private String ecCaseId;
    @JsonProperty
    private String thayiCardNumber;
    @JsonProperty
    private String anmIdentifier;
    @JsonProperty
    private String locationId;
    @JsonProperty
    private String referenceDate;
    @JsonProperty
    private String isClosed;
    @JsonProperty
    private Map<String, String> details;
    @JsonProperty
    private List<Map<String, String>> ancVisits;
    @JsonProperty
    private List<Map<String, String>> ifaTablets;
    @JsonProperty
    private List<Map<String, String>> ttDoses;
    @JsonProperty
    private List<Map<String, String>> hbTests;
    @JsonProperty
    private List<Map<String, String>> ancInvestigations;
    @JsonProperty
    private List<Map<String, String>> childrenDetails;

    private Mother() {
    }

    public Mother(String caseId, String ecCaseId, String thayiCardNumber) {
        this.caseId = caseId;
        this.ecCaseId = ecCaseId;
        this.thayiCardNumber = thayiCardNumber;
        this.details = new HashMap<>();
        this.setIsClosed(false);
        this.childrenDetails = new ArrayList<>();
    }

    public Mother withAnm(String identifier) {
        anmIdentifier = identifier;
        return this;
    }
    
    public Mother withLocationId(String locationId) {
        this.locationId = locationId;
        return this;
    }

    public Mother withLMP(LocalDate lmp) {
        this.referenceDate = lmp.toString();
        return this;
    }

    public Mother withDeliveryOutCome(String dateOfDelivery) {
        this.referenceDate = dateOfDelivery;
        return this;
    }

    public Mother withDetails(Map<String, String> details) {
        this.details = details;
        return this;
    }

    public String caseId() {
        return caseId;
    }

    public String ecCaseId() {
        return ecCaseId;
    }

    public String thayiCardNumber() {
        return thayiCardNumber;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }
    
    public String locationId() {
        return locationId;
    }

    public Map<String, String> details() {
        return details;
    }

    public String getDetail(String name) {
        return details.get(name);
    }

    public LocalDate dateOfDelivery() {
        return LocalDate.parse(referenceDate);
    }

    public LocalDate lmp() {
        return LocalDate.parse(referenceDate);
    }

    public List<Map<String, String>> ancVisits() {
        return ancVisits;
    }

    public List<Map<String, String>> ifaTablets() {
        return ifaTablets;
    }

    public List<Map<String, String>> ttDoses() {
        return ttDoses;
    }

    public List<Map<String, String>> hbTests() {
        return hbTests;
    }

    public List<Map<String, String>> ancInvestigations() {
        return ancInvestigations;
    }

    public List<Map<String, String>> childrenDetails() {
        if (childrenDetails == null) {
            childrenDetails = new ArrayList<>();
        }
        return childrenDetails;
    }

    public Mother setIsClosed(boolean isClosed) {
        this.isClosed = Boolean.toString(isClosed);
        return this;
    }

    private String getCaseId() {
        return caseId;
    }
    private String getEcCaseId() {
        return ecCaseId;
    }

    public Mother withANCVisits(List<Map<String, String>> ancVisits) {
        this.ancVisits = ancVisits;
        return this;
    }

    public void updateANCVisitInformation(Map<String, String> ancVisits) {
        if (this.ancVisits == null) {
            this.ancVisits = new ArrayList<>();
        }
        this.ancVisits.add(ancVisits);
    }

    public Mother withIFATablets(List<Map<String, String>> ifaTablets) {
        this.ifaTablets = ifaTablets;
        return this;
    }

    public void updateIFATabletsInformation(Map<String, String> ifaTablets) {
        if (this.ifaTablets == null) {
            this.ifaTablets = new ArrayList<>();
        }
        this.ifaTablets.add(ifaTablets);
    }

    public Mother withTTDoses(List<Map<String, String>> ttDoses) {
        this.ttDoses = ttDoses;
        return this;
    }

    public void updateTTDoseInformation(Map<String, String> ttDose) {
        if (this.ttDoses == null) {
            this.ttDoses = new ArrayList<>();
        }
        this.ttDoses.add(ttDose);
    }

    public Mother withHBTests(List<Map<String, String>> hbTests) {
        this.hbTests = hbTests;
        return this;
    }

    public Mother withANCInvestigations(List<Map<String, String>> ancInvestigations) {
        this.ancInvestigations = ancInvestigations;
        return this;
    }

    public void updateHBTestInformation(Map<String, String> hbTest) {
        if (this.hbTests == null) {
            this.hbTests = new ArrayList<>();
        }
        this.hbTests.add(hbTest);
    }

    public void updateTotalNumberOfIFATabletsGiven(int numberOfIFATabletsGivenThisTime) {
        int totalNumberOfIFATabletsGiven = IntegerUtil.tryParse(getDetail(RegisterConstants.IFAFields.TOTAL_NUMBER_OF_IFA_TABLETS_GIVEN), 0);
        details().put(RegisterConstants.IFAFields.TOTAL_NUMBER_OF_IFA_TABLETS_GIVEN, String.valueOf(totalNumberOfIFATabletsGiven + numberOfIFATabletsGivenThisTime));
    }
    
    public void updateANCInvestigationsInformation(Map<String, String> ancInvestigations) {
        if (this.ancInvestigations == null) {
            this.ancInvestigations = new ArrayList<>();
        }
        this.ancInvestigations.add(ancInvestigations);
    }

    public Mother withChildrenDetails(List<Map<String, String>> childrenDetails) {
        this.childrenDetails = childrenDetails;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "id", "revision");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "id", "revision");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
