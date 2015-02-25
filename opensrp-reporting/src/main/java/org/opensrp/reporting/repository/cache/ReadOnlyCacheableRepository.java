package org.opensrp.reporting.repository.cache;

import java.util.List;

public interface ReadOnlyCacheableRepository<T> {
    T fetch(T object);

    List<T> fetchAll();
}
