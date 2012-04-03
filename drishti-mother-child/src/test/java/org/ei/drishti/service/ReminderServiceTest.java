package org.ei.drishti.service;

import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.Reminder;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.repository.AllReminders;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReminderServiceTest {
    @Mock
    private AllReminders allReminders;
    @Mock
    private AllMothers allMothers;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveReminder() throws Exception {
        ReminderService service = new ReminderService(allReminders, allMothers);
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "Thaayi 1", "Theresa").withAnmPhoneNumber("ANM phone no"));

        service.reminderForMother("Case X", "ANC 1", "due");

        verify(allReminders).add(new Reminder("Theresa", "Thaayi 1", "ANM phone no", "Case X", "ANC 1", "due"));
    }
}
