package org.ei.drishti.reporting.repository.cache;

public interface CacheableRepository<T> {
    public void save(T objectToBeSaved);
    public T fetch(T objectWhichShouldBeFilledWithMoreInformation);
}
