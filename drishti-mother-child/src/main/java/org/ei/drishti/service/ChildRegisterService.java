package org.ei.drishti.service;

import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.register.ChildRegister;
import org.ei.drishti.domain.register.ChildRegisterEntry;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

@Service
public class ChildRegisterService {
    private final AllChildren allChildren;
    private AllMothers allMothers;
    private final AllEligibleCouples allEligibleCouples;

    @Autowired
    public ChildRegisterService(AllChildren allChildren,
                                AllMothers allMothers, AllEligibleCouples allEligibleCouples) {
        this.allChildren = allChildren;
        this.allMothers = allMothers;
        this.allEligibleCouples = allEligibleCouples;
    }

    public ChildRegister getRegisterForANM(String anmIdentifier) {
        ArrayList<ChildRegisterEntry> childRegisterEntries = new ArrayList<>();
        List<Child> children = allChildren.findAllOpenChildrenForANM(anmIdentifier);
        List<String> motherIds = new ArrayList<>(
                new HashSet<>(collect(children, on(Child.class).motherCaseId())));
        List<Mother> mothers = allMothers.findAll(motherIds);
        List<String> ecIDs = new ArrayList<>(new HashSet<>(
                collect(mothers, on(Mother.class).ecCaseId())));
        List<EligibleCouple> ecs = allEligibleCouples.findAll(ecIDs);
        for (Child child : children) {
            Mother mother = selectUnique(mothers,
                    having(on(Mother.class).caseId(), equalTo(child.motherCaseId())));
            EligibleCouple ec = selectUnique(ecs,
                    having(on(EligibleCouple.class).caseId(), equalTo(mother.ecCaseId())));

            ChildRegisterEntry entry = new ChildRegisterEntry()
                    .withThayiCardNumber(mother.thayiCardNumber())
                    .withWifeName(ec.wifeName())
                    .withHusbandName(ec.husbandName())
                    .withWifeDOB(ec.wifeDOB())
                    .withVillage(ec.location().village())
                    .withSubCenter(ec.location().subCenter())
                    .withDob(LocalDate.parse(child.dateOfBirth()))
                    .withImmunizations(child.immunizations());
            childRegisterEntries.add(entry);
        }
        return new ChildRegister(childRegisterEntries);
    }
}
