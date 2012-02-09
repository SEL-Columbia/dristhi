package org.ei.commcare.listener.event;

public class FakeANCVisitRequestWithoutADefaultConstructor {
    private final int something;

    public FakeANCVisitRequestWithoutADefaultConstructor(int something) {
        this.something = something;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FakeANCVisitRequestWithoutADefaultConstructor that = (FakeANCVisitRequestWithoutADefaultConstructor) o;

        if (something != that.something) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return something;
    }
}
