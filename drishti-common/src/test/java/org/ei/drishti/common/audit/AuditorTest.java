package org.ei.drishti.common.audit;

import org.ei.drishti.common.audit.AuditMessage;
import org.ei.drishti.common.audit.Auditor;
import org.ei.drishti.common.audit.ForbiddenFieldInAuditMessage;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.ArrayList;
import java.util.List;

import static org.ei.drishti.common.audit.AuditMessageType.NORMAL;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AuditorTest extends BaseUnitTest {
    @Test
    public void shouldHaveOnlyAsManyMessagesAsTheSizeOfTheAuditorLog() {
        Auditor auditor = new Auditor(2);
        audit(auditor, "Message 1");
        audit(auditor, "Message 2");
        audit(auditor, "Message 3");

        assertThat(auditor.messagesSince(0).size(), is(2));
    }

    @Test
    public void shouldKeepOnlyTheNewestMessagesWhenRemovingMessagesWhichOverflow() {
        Auditor auditor = new Auditor(2);
        audit(auditor, "Message 1");
        audit(auditor, "Message 2");
        audit(auditor, "Message 3");

        List<AuditMessage> messages = auditor.messagesSince(0);
        assertData(messages.get(0), is("Message 2"));
        assertData(messages.get(1), is("Message 3"));
    }

    @Test
    public void shouldGiveAllMessageWhenMessageIndexBeingSearchedForIsZeroOrNegative() {
        Auditor auditor = new Auditor(3);
        audit(auditor, "Message 1");
        audit(auditor, "Message 2");
        audit(auditor, "Message 3");
        audit(auditor, "Message 4");
        audit(auditor, "Message 5");

        List<AuditMessage> messages = auditor.messagesSince(0);
        assertThat(messages.size(), is(3));
        assertData(messages.get(0), is("Message 3"));
        assertData(messages.get(1), is("Message 4"));
        assertData(messages.get(2), is("Message 5"));

        messages = auditor.messagesSince(-10);
        assertThat(messages.size(), is(3));
        assertData(messages.get(0), is("Message 3"));
        assertData(messages.get(1), is("Message 4"));
        assertData(messages.get(2), is("Message 5"));
    }

    @Test
    public void shouldBeAbleToFindMessagesSubmittedSinceAGivenIndex() throws Exception {
        Auditor auditor = new Auditor(3);
        audit(auditor, "Message 1");
        audit(auditor, "Message 2");
        audit(auditor, "Message 3");

        long messageIndexOfLastMessage = auditor.messagesSince(0).get(2).index();

        audit(auditor, "Message 4");
        audit(auditor, "Message 5");

        List<AuditMessage> newMessages = auditor.messagesSince(messageIndexOfLastMessage);
        assertThat(newMessages.size(), is(2));
        assertData(newMessages.get(0), is("Message 4"));
        assertData(newMessages.get(1), is("Message 5"));
    }

    @Test
    public void shouldBeAbleToSearchForMessagesByIndexEvenIfThereHaveBeenManyMessagesInBetween() {
        Auditor auditor = new Auditor(3);
        audit(auditor, "Message 1");

        long messageIndexOfLastMessage = auditor.messagesSince(0).get(0).index();

        audit(auditor, "Message 2");
        audit(auditor, "Message 3");
        audit(auditor, "Message 4");
        audit(auditor, "Message 5");
        audit(auditor, "Message 6");
        audit(auditor, "Message 7");

        List<AuditMessage> messages = auditor.messagesSince(messageIndexOfLastMessage);
        assertThat(messages.size(), is(3));
        assertData(messages.get(0), is("Message 5"));
        assertData(messages.get(1), is("Message 6"));
        assertData(messages.get(2), is("Message 7"));
    }

    @Test
    public void shouldNotFindAnyMessagesIfAllMessagesHaveAlreadyBeenSeen() {
        Auditor auditor = new Auditor(3);
        audit(auditor, "Message 1");
        audit(auditor, "Message 2");
        audit(auditor, "Message 3");

        long messageIndexOfLastMessage = auditor.messagesSince(0).get(2).index();

        List<AuditMessage> newMessages = auditor.messagesSince(messageIndexOfLastMessage);
        assertThat(newMessages.size(), is(0));
    }

    @Test
    public void shouldNotFindAnyMessagesWhenAWrongMessageIndexIfProvided() {
        Auditor auditor = new Auditor(3);
        audit(auditor, "Message 1");
        audit(auditor, "Message 2");
        audit(auditor, "Message 3");

        long messageIndexOfLastMessage = auditor.messagesSince(0).get(2).index();

        List<AuditMessage> newMessages = auditor.messagesSince(messageIndexOfLastMessage + 10);
        assertThat(newMessages.size(), is(0));
    }

    @Test
    public void shouldNotAllowTwoMessagesToHaveTheSameIndexEvenIfTheyAreAddedInTheSameMillisecond() {
        DateTime timeNow = DateTime.now();

        Auditor auditor = new Auditor(3);

        mockCurrentDate(timeNow);
        audit(auditor, "Message 1 - Same timestamp as Messages 2 and 3");
        audit(auditor, "Message 2 - Same timestamp as Messages 1 and 3");
        audit(auditor, "Message 3 - Same timestamp as Messages 1 and 2");

        resetDateTimeSource();

        long messageIndexOfLastMessage = auditor.messagesSince(0).get(2).index();

        List<AuditMessage> newMessages = auditor.messagesSince(messageIndexOfLastMessage);
        assertThat(newMessages.size(), is(0));
    }

    @Test(expected = ForbiddenFieldInAuditMessage.class)
    public void shouldNotAllowAddingOfFieldsWhichAreNotSupportedByTheAuditMessageTypeUsed() {
        Auditor auditor = new Auditor(3);
        auditor.audit(NORMAL).with("SOMETHING_OTHER_THAN_data", "Message 1").done();
    }

    @Test
    public void shouldCaptureAllAuditMessagesEvenIfCalledFromMultipleThreads() throws Exception {
        final Auditor auditor = new Auditor(10000);
        List<Thread> threads = new ArrayList<Thread>();

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        auditor.audit(NORMAL).with("data", "abc + " + i + " " + Thread.currentThread().getId()).done();
                    }
                }
            });
            threads.add(thread);
        }

        for (int i = 0; i < 10; i++) {
            threads.get(i).start();
        }

        for (int i = 0; i < 10; i++) {
            threads.get(i).join();
        }

        assertEquals(10000, auditor.messagesSince(0).size());
    }

    private void assertData(AuditMessage message, Matcher<String> expectedDataMatcher) {
        assertThat(message.data().get("data"), expectedDataMatcher);
    }

    private void audit(Auditor auditor, String value) {
        auditor.audit(NORMAL).with("data", value).done();
    }
}
