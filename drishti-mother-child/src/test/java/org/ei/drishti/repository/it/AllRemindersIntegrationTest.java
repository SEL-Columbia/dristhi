package org.ei.drishti.repository.it;

import org.ei.drishti.domain.Reminder;
import org.ei.drishti.repository.AllReminders;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti.xml")
public class AllRemindersIntegrationTest {
    @Autowired
    AllReminders reminders;

    @Before
    public void setUp() throws Exception {
        reminders.removeAll();
    }

    @Test
    public void shouldSaveAReminder() throws Exception {
        Reminder reminder = new Reminder("Theresa", "Thaayi 1", "ANM phone no", "Case X", "ANC 1", "due");

        reminders.add(reminder);

        List<Reminder> allTheRemindersInDB = reminders.getAll();
        assertEquals(1, allTheRemindersInDB.size());
        assertEquals(reminder, allTheRemindersInDB.get(0));
    }
}
