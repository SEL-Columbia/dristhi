package org.opensrp.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.opensrp.register.domain.PNCVisit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PNCRegisterEntry {

    private String registrationDate;
    private String thayiCardNumber;
    private String wifeName;
    private String husbandName;
    private String wifeDOB;
    private String address;
    private String dateOfDelivery;
    private String placeOfDelivery;
    private String typeOfDelivery;
    private String dischargeDate;
    private String fpMethodName;
    private String fpMethodDate;
    private String deliveryComplications;
    private List<Map<String, String>> childrenDetails;
    private List<PNCVisit> pncVisits;


    public PNCRegisterEntry withRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
        return this;
    }

    public PNCRegisterEntry withThayiCardNumber(String thayiCardNumber) {
        this.thayiCardNumber = thayiCardNumber;
        return this;
    }

    public PNCRegisterEntry withWifeName(String wifeName) {
        this.wifeName = wifeName;
        return this;
    }

    public PNCRegisterEntry withHusbandName(String husbandName) {
        this.husbandName = husbandName;
        return this;
    }

    public PNCRegisterEntry withWifeDOB(String wifeDOB) {
        this.wifeDOB = wifeDOB;
        return this;
    }

    public PNCRegisterEntry withAddress(String address) {
        this.address = address;
        return this;
    }

    public PNCRegisterEntry withDateOfDelivery(String dateOfDelivery) {
        this.dateOfDelivery = dateOfDelivery;
        return this;
    }

    public PNCRegisterEntry withPlaceOfDelivery(String placeOfDelivery) {
        this.placeOfDelivery = placeOfDelivery;
        return this;
    }

    public PNCRegisterEntry withDischargeDate(String dischargeDate) {
        this.dischargeDate = dischargeDate;
        return this;
    }

    public PNCRegisterEntry withFPMethodName(String fpMethodName) {
        this.fpMethodName = fpMethodName;
        return this;
    }

    public PNCRegisterEntry withFPMethodDate(String fpMethodDate) {
        this.fpMethodDate = fpMethodDate;
        return this;
    }

    public PNCRegisterEntry withTypeOfDelivery(String typeOfDelivery) {
        this.typeOfDelivery = typeOfDelivery;
        return this;
    }

    public PNCRegisterEntry withDeliveryComplications(String deliveryComplications) {
        this.deliveryComplications = deliveryComplications;
        return this;
    }


    public PNCRegisterEntry withChildrenDetails(List<Map<String, String>> childrenDetails) {
        this.childrenDetails = childrenDetails;
        return this;
    }

    public PNCRegisterEntry withPNCVisits(List<PNCVisit> pncVisits) {
        this.pncVisits = pncVisits;
        return this;
    }

    public String registrationDate() {
        return registrationDate;
    }

    public String thayiCardNumber() {
        return thayiCardNumber;
    }

    public String wifeName() {
        return wifeName;
    }

    public String husbandName() {
        return husbandName;
    }

    public String wifeDOB() {
        return wifeDOB;
    }

    public String address() {
        return address;
    }

    public String dateOfDelivery() {
        return dateOfDelivery;
    }

    public String placeOfDelivery() {
        return placeOfDelivery;
    }

    public String typeOfDelivery() {
        return typeOfDelivery;
    }

    public String dischargeDate() {
        return dischargeDate;
    }

    public String fpMethodName() {
        return fpMethodName;
    }

    public String fpMethodDate() {
        return fpMethodDate;
    }

    public String deliveryComplications() {
        return deliveryComplications;
    }

    public List<Map<String, String>> childrenDetails() {
        return childrenDetails;
    }

    public List<PNCVisit> pncVisitDetails() {
        if (pncVisits == null) {
            return new ArrayList<>();
        }
        return pncVisits;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}