package org.ei.drishti.reporting.repository;

public interface CacheableRepository<T> {
    public void save(T objectToBeSaved);
    public T fetch(T objectWhichShouldBeFilledWithMoreInformation);
}
