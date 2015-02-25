package org.opensrp.reporting.repository.cache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.reporting.domain.ANM;
import org.opensrp.reporting.domain.Indicator;
import org.opensrp.reporting.repository.cache.ANMCacheableRepository;
import org.opensrp.reporting.repository.cache.IndicatorCacheableRepository;
import org.opensrp.reporting.repository.cache.ReadOnlyCacheableRepository;
import org.opensrp.reporting.repository.cache.ReadOnlyCachingRepository;

public class ReadOnlyCachingRepositoryTest {
    @Mock
    private ANMCacheableRepository anmsRepository;
    @Mock
    private IndicatorCacheableRepository indicatorRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldOnlyFetchNewANMsAndFetchOldOnesFromCache() throws Exception {
        assertCacheableReadOnly(anmsRepository, new FactoryForReadOnlyCache<ANM>() {
            @Override
            public ANM objectNotInDB() {
                return new ANM(1, "ANM Y");
            }

            @Override
            public ANM objectInDB() {
                return new ANM(4, "ANM X");
            }
        });
    }

    @Test
    public void shouldOnlyFetchNewIndicationsAndFetchOldOnesFromCache() throws Exception {
        assertCacheableReadOnly(indicatorRepository, new FactoryForReadOnlyCache<Indicator>() {
            @Override
            public Indicator objectNotInDB() {
                return new Indicator(1, "BCG");
            }

            @Override
            public Indicator objectInDB() {
                return new Indicator(4, "IUD");
            }
        });
    }

    private <T> void assertCacheableReadOnly(ReadOnlyCacheableRepository<T> cacheableRepository, final FactoryForReadOnlyCache<T> factory) {
        ReadOnlyCachingRepository<T> repository = new ReadOnlyCachingRepository<>(cacheableRepository);

        T objectInDB = factory.objectInDB();
        T objectNotInDB = factory.objectNotInDB();
        when(cacheableRepository.fetch(objectInDB)).thenReturn(objectInDB);
        when(cacheableRepository.fetch(objectNotInDB)).thenReturn(null);

        assertEquals(objectInDB, repository.fetch(objectInDB));
        assertEquals(objectInDB, repository.fetch(objectInDB));
        assertEquals(null, repository.fetch(objectNotInDB));

        verify(cacheableRepository, times(1)).fetch(objectInDB);
        verify(cacheableRepository, times(1)).fetch(objectNotInDB);
        verifyNoMoreInteractions(cacheableRepository);
    }

    private abstract class FactoryForReadOnlyCache<T> {
        public abstract T objectNotInDB();

        public abstract T objectInDB();
    }


}
