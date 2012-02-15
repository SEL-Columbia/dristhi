package org.ei.commcare.listener.event;

import java.util.Date;

public class FakeRequestWithDate {
    private final Date date;

    public FakeRequestWithDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FakeRequestWithDate that = (FakeRequestWithDate) o;

        if (date != null ? !date.equals(that.date) : that.date != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return date != null ? date.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "FakeRequestWithDate{date=" + date + '}';
    }
}
