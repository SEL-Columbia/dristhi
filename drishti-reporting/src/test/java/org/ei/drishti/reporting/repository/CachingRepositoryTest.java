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
            public ANM makeObjectWithoutID() {
                return new ANM("ANM X");
            }

            @Override
            public ANM makeObjectWithID() {
                return new ANM(5, "ANM X");
            }
        });
    }

    @Test
    public void shouldSaveNewIndicationsAndFetchOldOnesFromCache() throws Exception {
        assertCacheable(indicatorRepository, new Factory<Indicator>() {
            @Override
            public Indicator makeObjectWithoutID() {
                return new Indicator("BCG");
            }

            @Override
            public Indicator makeObjectWithID() {
                return new Indicator(1, "BCG");
            }
        });
    }

    @Test
    public void shouldSaveNewDatesAndFetchOldOnesFromCache() throws Exception {
        assertCacheable(datesRepository, new Factory<Dates>() {
            @Override
            public Dates makeObjectWithoutID() {
                return new Dates(LocalDate.now().toDate());
            }

            @Override
            public Dates makeObjectWithID() {
                return new Dates(1, LocalDate.now().toDate());
            }
        });
    }

    @Test
    public void shouldSaveNewLocationsAndFetchOldOnesFromCache() throws Exception {
        assertCacheable(locationRepository, new Factory<Location>() {
            @Override
            public Location makeObjectWithoutID() {
                return new Location(1, "Bherya", "Sub Center", "PHC X");
            }

            @Override
            public Location makeObjectWithID() {
                return new Location(1, "Bherya", "Sub Center", "PHC X");
            }
        });
    }

    private <T> void assertCacheable(CacheableRepository<T> cacheableRepository, Factory<T> factory) {
        CachingRepository<T> repository = new CachingRepository<T>(cacheableRepository);
        T objectWithoutID = factory.makeObjectWithoutID();
        T objectWithID = factory.makeObjectWithID();
        when(cacheableRepository.fetch(objectWithoutID)).thenReturn(objectWithID);

        assertEquals(objectWithID, repository.fetch(factory.makeObjectWithoutID()));
        assertEquals(objectWithID, repository.fetch(factory.makeObjectWithoutID()));
        assertEquals(objectWithID, repository.fetch(factory.makeObjectWithoutID()));

        verify(cacheableRepository, times(1)).fetch(objectWithoutID);
        verify(cacheableRepository, times(1)).save(objectWithoutID);
        verifyNoMoreInteractions(cacheableRepository);
    }

    private abstract class Factory<T> {
        public abstract T makeObjectWithoutID();
        public abstract T makeObjectWithID();
    }
}
