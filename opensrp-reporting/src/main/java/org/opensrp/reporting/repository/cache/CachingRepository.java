package org.opensrp.reporting.repository.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class CachingRepository<T> {
    private CacheableRepository<T> cacheableRepository;
    private Map<T, T> cache;
    private static ReentrantLock lock = new ReentrantLock();

    public CachingRepository(CacheableRepository<T> cacheableRepository) {
        this.cacheableRepository = cacheableRepository;
        cache = new HashMap<>();
    }

    public T fetch(T object) {
        if (cache.containsKey(object)) {
            return cache.get(object);
        }

        saveToCache(object);
        return cache.get(object);
    }

    public void clear(T object) {
        if (cache.containsKey(object)) {
            cache.remove(object);
        }
    }

    private void saveToCache(T object) {
        lock.lock();
        try {
            if (cache.containsKey(object)) {
                return;
            }

            T objectInDB = cacheableRepository.fetch(object);
            if (objectInDB == null) {
                cacheableRepository.save(object);
                cacheableRepository.flush();
                objectInDB = cacheableRepository.fetch(object);
            }
            cache.put(object, objectInDB);
        } finally {
            lock.unlock();
        }
    }

    public List<T> fetchAll() {
        return cacheableRepository.fetchAll();
    }
}
