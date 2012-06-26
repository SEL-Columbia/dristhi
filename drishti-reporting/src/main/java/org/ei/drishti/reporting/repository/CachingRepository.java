package org.ei.drishti.reporting.repository;

import java.util.HashMap;
import java.util.Map;

public class CachingRepository<T> {
    private CacheableRepository<T> cacheableRepository;
    private Map<T, T> cache;

    public CachingRepository(CacheableRepository<T> cacheableRepository) {
        this.cacheableRepository = cacheableRepository;
        cache = new HashMap<>();
    }

    public T fetch(T object) {
        if (!cache.containsKey(object)) {
            cacheableRepository.save(object);
            cache.put(object, cacheableRepository.fetch(object));
        }
        return cache.get(object);
    }
}
