package org.opensrp.reporting.repository.cache;

import java.util.List;

public interface CacheableRepository<T> {
    public void save(T objectToBeSaved);
    public T fetch(T objectWhichShouldBeFilledWithMoreInformation);
    public List<T> fetchAll();
    void flush();
}
