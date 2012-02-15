package org.ei.commcare.listener.event;

public class FakeDrishtiController {
    public void registerMother(FakeMotherRegistrationRequest request) {
    }

    public void ancVisit(FakeANCVisitRequestWithoutADefaultConstructor request) {
    }

    public void methodWithoutArguments() {
    }

    public void methodWithTwoArguments(int a, int b) {
    }

    public void methodWhichThrowsAnException(FakeMotherRegistrationRequest request) {
        throw new RuntimeException("Boo");
    }

    public void methodWithArgumentHavingADate(FakeRequestWithDate fakeRequestWithDate) {
    }
}
