package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.ANM;
import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.repository.cache.*;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CachingRepositoryTest {
    @Mock
    private ANMCacheableRepository allANMsRepository;
    @Mock
    private IndicatorCacheableRepository indicatorRepository;
    @Mock
    private LocationCacheableRepository locationRepository;
    @Mock
    private DatesCacheableRepository datesRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveNewANMsAndFetchOldOnesFromCache() throws Exception {
        assertCacheable(allANMsRepository, new Factory<ANM>() {
            @Override
            public ANM makeObjectWithIDOfZero() {
                return new ANM("ANM X");
            }

            @Override
            public ANM makeObjectWithIDOfFour() {
                return new ANM(4, "ANM X");
            }
        });
    }

    @Test
    public void shouldSaveNewIndicationsAndFetchOldOnesFromCache() throws Exception {
        assertCacheable(indicatorRepository, new Factory<Indicator>() {
            @Override
            public Indicator makeObjectWithIDOfZero() {
                return new Indicator("BCG");
            }

            @Override
            public Indicator makeObjectWithIDOfFour() {
                return new Indicator(4, "BCG");
            }
        });
    }

    @Test
    public void shouldSaveNewDatesAndFetchOldOnesFromCache() throws Exception {
        assertCacheable(datesRepository, new Factory<Dates>() {
            @Override
            public Dates makeObjectWithIDOfZero() {
                return new Dates(LocalDate.now().toDate());
            }

            @Override
            public Dates makeObjectWithIDOfFour() {
                return new Dates(4, LocalDate.now().toDate());
            }
        });
    }

    @Test
    public void shouldSaveNewLocationsAndFetchOldOnesFromCache() throws Exception {
        assertCacheable(locationRepository, new Factory<Location>() {
            @Override
            public Location makeObjectWithIDOfZero() {
                return new Location("Bherya", "Sub Center", "PHC X");
            }

            @Override
            public Location makeObjectWithIDOfFour() {
                return new Location(4, "Bherya", "Sub Center", "PHC X");
            }
        });
    }

    @Test
    public void shouldNotTryAndSaveAnAlreadySavedItemWhenThereAreCallsFromMultipleThreadsUsingTheSameCachingRepository() throws Exception {
        AllANMsRepository anMsRepository = mock(AllANMsRepository.class);
        CachingRepository<ANM> anmCachingRepository = new CachingRepository<>(anMsRepository);

        fetchFromCacheAcrossThreads(anmCachingRepository, anmCachingRepository);

        verify(anMsRepository, times(1)).save(new ANM(0, "ANM X"));
    }

    private void fetchFromCacheAcrossThreads(final CachingRepository<ANM> repository1, final CachingRepository<ANM> repository2) throws InterruptedException {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                repository1.fetch(new ANM(0, "ANM X"));
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                repository2.fetch(new ANM(0, "ANM X"));
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
        when(cacheableRepository.fetch(factory.makeObjectWithIDOfFour())).thenReturn(factory.makeObjectWithIDOfFour());

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

        verify(cacheableRepository, times(1)).save(factory.makeObjectWithIDOfZero());
        verify(cacheableRepository, times(1)).fetch(factory.makeObjectWithIDOfFour());
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
