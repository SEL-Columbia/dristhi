package org.opensrp.reporting.repository.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class ReadOnlyCachingRepository<T> {
    private ReadOnlyCacheableRepository<T> cacheableRepository;
    private Map<T, T> cache;
    private static ReentrantLock lock = new ReentrantLock();

    public ReadOnlyCachingRepository(ReadOnlyCacheableRepository<T> cacheableRepository) {
        this.cacheableRepository = cacheableRepository;
        cache = new HashMap<>();
    }

    public T fetch(T object) {
        if (cache.containsKey(object)) {
            return cache.get(object);
        }

        return writeToCache(object);
    }

    public void clear(T object){
        if (cache.containsKey(object)) {
            cache.remove(object);
        }
    }

    private T writeToCache(T object) {
        lock.lock();
        try {
            if (cache.containsKey(object)) {
                return cache.get(object);
            }

            T objectInDB = cacheableRepository.fetch(object);
            if (objectInDB == null) {
                return null;
            }
            cache.put(object, objectInDB);
        } finally {
            lock.unlock();
        }
        return cache.get(object);
    }

    public List<T> fetchAll() {
        return cacheableRepository.fetchAll();
    }
}
