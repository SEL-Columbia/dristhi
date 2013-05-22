package org.ei.drishti.service;

import org.ei.drishti.contract.PostNatalCareInformation;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.ei.drishti.service.MCTSSMSService.MCTSServiceCode.PNC_7_Days;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DrishtiMCTSServiceTest {
    private DrishtiMCTSService service;
    @Mock
    private MCTSSMSService mctsSMSService;
    @Mock
    private AllMothers mothers;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new DrishtiMCTSService(mctsSMSService, mothers);
    }

    @Test
    public void shouldSendMCTSSMSWhenPNCVisitHappensAndMotherExists() throws Exception {
        when(mothers.exists("CASE X")).thenReturn(true);
        when(mothers.findByCaseId("CASE X")).thenReturn(new Mother("CASE X", "EC CASE X", "123"));

        service.pncProvided(new PostNatalCareInformation("CASE X", "ANM X", "1", "20", "2013-01-01"));

        verify(mctsSMSService).send(PNC_7_Days, "123", LocalDate.parse("2013-01-01"));
    }

    @Test
    public void shouldNotSendMCTSSMSWhenPNCVisitHappensAndMotherDoesNotExists() throws Exception {
        when(mothers.exists("CASE X")).thenReturn(false);

        service.pncProvided(new PostNatalCareInformation("CASE X", "ANM X", "1", "20", "2013-01-01"));

        verifyZeroInteractions(mctsSMSService);
    }
}
