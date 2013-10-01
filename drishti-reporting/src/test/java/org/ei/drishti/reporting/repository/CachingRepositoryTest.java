package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.repository.cache.CacheableRepository;
import org.ei.drishti.reporting.repository.cache.CachingRepository;
import org.ei.drishti.reporting.repository.cache.DatesCacheableRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.util.Date;

import static org.joda.time.LocalDate.now;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CachingRepositoryTest {
    @Mock
    private DatesCacheableRepository datesRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveNewDatesAndFetchOldOnesFromCache() throws Exception {
        assertCacheable(datesRepository, new Factory<Dates>() {
            @Override
            public Dates makeObjectWithIDOfZero() {
                return new Dates(now().toDate());
            }

            @Override
            public Dates makeObjectWithIDOfFour() {
                return new Dates(4, now().toDate());
            }
        });
    }

    @Test
    public void shouldNotTryAndSaveAnAlreadySavedItemWhenThereAreCallsFromMultipleThreadsUsingTheSameCachingRepository() throws Exception {
        AllDatesRepository allDatesRepository = mock(AllDatesRepository.class);
        CachingRepository<Dates> datesCachingRepository = new CachingRepository<>(allDatesRepository);

        fetchFromCacheAcrossThreads(datesCachingRepository, datesCachingRepository);

        verify(allDatesRepository, times(1)).save(new Dates(0, now().toDate()));
    }

    @Test
    public void shouldFetchFromDBToFillCacheIfAnObjectIsInTheDBButNotInTheCache() throws Exception {
        Date today = now().toDate();
        when(datesRepository.fetch(new Dates(0, today))).thenReturn(new Dates(10, today));

        CachingRepository<Dates> repoWhichCaches = new CachingRepository<>(datesRepository);
        Dates dateFromCache = repoWhichCaches.fetch(new Dates(0, today));
        repoWhichCaches.fetch(new Dates(0, today));
        repoWhichCaches.fetch(new Dates(0, today));
        repoWhichCaches.fetch(new Dates(0, today));

        assertEquals(new Integer(10), dateFromCache.id());
        assertEquals(today, dateFromCache.date());

        verify(datesRepository, times(1)).fetch(new Dates(0, today));
        verify(datesRepository, times(0)).save(any(Dates.class));
    }

    private void fetchFromCacheAcrossThreads(final CachingRepository<Dates> repository1, final CachingRepository<Dates> repository2) throws InterruptedException {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                repository1.fetch(new Dates(0, now().toDate()));
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                repository2.fetch(new Dates(0, now().toDate()));
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    private <T> void assertCacheable(CacheableRepository<T> cacheableRepository, final Factory<T> factory) {
        CachingRepository<T> repository = new CachingRepository<>(cacheableRepository);

        expectCallToSaveToRepoAndMimicHibernateSettingIDOnTheObjectToBeSavedItself(cacheableRepository, factory.makeObjectWithIDOfZero());
        when(cacheableRepository.fetch(factory.makeObjectWithIDOfFour())).thenReturn(null, factory.makeObjectWithIDOfFour());

        String message = "Please make sure that the ID is NOT part of the equals() and hashCode() of the object to be cached.\n" +
                "This is because Hibernate changes the object which is sent to the repo. So, the key in the cache gets changed\n" +
                "to be the one which has an ID (say, 4). Next time, when the fetch is called with ID = 0, then it will not\n" +
                "be found in the cache, if ID is a part of hashCode() and equals().";
        assertEquals(message, factory.makeObjectWithIDOfZero(), factory.makeObjectWithIDOfFour());
        assertEquals(message, factory.makeObjectWithIDOfZero().hashCode(), factory.makeObjectWithIDOfFour().hashCode());

        final T objectWithID = factory.makeObjectWithIDOfFour();
        assertEquals(objectWithID, repository.fetch(factory.makeObjectWithIDOfZero()));
        assertEquals(objectWithID, repository.fetch(factory.makeObjectWithIDOfZero()));
        assertEquals(objectWithID, repository.fetch(factory.makeObjectWithIDOfZero()));
        assertEquals(objectWithID, repository.fetch(factory.makeObjectWithIDOfZero()));

        verify(cacheableRepository, times(1)).save(factory.makeObjectWithIDOfZero());
        verify(cacheableRepository, times(1)).flush();
        verify(cacheableRepository, times(2)).fetch(factory.makeObjectWithIDOfZero());
        verifyNoMoreInteractions(cacheableRepository);
    }

    private <T> void expectCallToSaveToRepoAndMimicHibernateSettingIDOnTheObjectToBeSavedItself(CacheableRepository<T> cacheableRepository, T objectWithoutID) {
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object objectWhoseIDNeedsToBeSet = invocationOnMock.getArguments()[0];
                setIdOn(objectWhoseIDNeedsToBeSet);
                return objectWhoseIDNeedsToBeSet;
            }

            private void setIdOn(Object objectWhoseIDNeedsToBeSet) throws NoSuchFieldException, IllegalAccessException {
                Field idField = objectWhoseIDNeedsToBeSet.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(objectWhoseIDNeedsToBeSet, 4);
            }
        }).when(cacheableRepository).save(objectWithoutID);
    }

    private abstract class Factory<T> {
        public abstract T makeObjectWithIDOfZero();

        public abstract T makeObjectWithIDOfFour();
    }
}
