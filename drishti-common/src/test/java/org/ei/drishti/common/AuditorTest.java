package org.ei.drishti.common;

import org.ei.drishti.common.audit.AuditMessage;
import org.ei.drishti.common.audit.Auditor;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.List;

import static org.ei.drishti.common.audit.AuditMessageType.NORMAL;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AuditorTest {
    @Test
    public void shouldHaveOnlyAsManyMessagesAsTheSizeOfTheAuditorLog() {
        Auditor auditor = new Auditor(2);
        auditor.audit(NORMAL).with("data", "Message 1").done();
        auditor.audit(NORMAL).with("data", "Message 2").done();
        auditor.audit(NORMAL).with("data", "Message 3").done();

        assertThat(auditor.messagesSince(0).size(), is(2));
    }

    @Test
    public void shouldKeepOnlyTheNewestMessagesWhenRemovingMessagesWhichOverflow() {
        Auditor auditor = new Auditor(2);
        auditor.audit(NORMAL).with("data", "Message 1").done();
        auditor.audit(NORMAL).with("data", "Message 2").done();
        auditor.audit(NORMAL).with("data", "Message 3").done();

        List<AuditMessage> messages = auditor.messagesSince(0);
        assertData(messages.get(0), is("Message 2"));
        assertData(messages.get(1), is("Message 3"));
    }

    @Test
    public void shouldGiveAllMessageWhenMessageIndexBeingSearchedForIsZeroOrNegative() {
        Auditor auditor = new Auditor(3);
        auditor.audit(NORMAL).with("data", "Message 1").done();
        auditor.audit(NORMAL).with("data", "Message 2").done();
        auditor.audit(NORMAL).with("data", "Message 3").done();
        auditor.audit(NORMAL).with("data", "Message 4").done();
        auditor.audit(NORMAL).with("data", "Message 5").done();

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
        auditor.audit(NORMAL).with("data", "Message 1").done();
        auditor.audit(NORMAL).with("data", "Message 2").done();
        auditor.audit(NORMAL).with("data", "Message 3").done();

        long messageIndexOfLastMessage = auditor.messagesSince(0).get(2).index();

        auditor.audit(NORMAL).with("data", "Message 4").done();
        auditor.audit(NORMAL).with("data", "Message 5").done();

        List<AuditMessage> newMessages = auditor.messagesSince(messageIndexOfLastMessage);
        assertThat(newMessages.size(), is(2));
        assertData(newMessages.get(0), is("Message 4"));
        assertData(newMessages.get(1), is("Message 5"));
    }

    @Test
    public void shouldBeAbleToSearchForMessagesByIndexEvenIfThereHaveBeenManyMessagesInBetween() {
        Auditor auditor = new Auditor(3);
        auditor.audit(NORMAL).with("data", "Message 1").done();

        long messageIndexOfLastMessage = auditor.messagesSince(0).get(0).index();

        auditor.audit(NORMAL).with("data", "Message 2").done();
        auditor.audit(NORMAL).with("data", "Message 3").done();
        auditor.audit(NORMAL).with("data", "Message 4").done();
        auditor.audit(NORMAL).with("data", "Message 5").done();
        auditor.audit(NORMAL).with("data", "Message 6").done();
        auditor.audit(NORMAL).with("data", "Message 7").done();

        List<AuditMessage> messages = auditor.messagesSince(messageIndexOfLastMessage);
        assertThat(messages.size(), is(3));
        assertData(messages.get(0), is("Message 5"));
        assertData(messages.get(1), is("Message 6"));
        assertData(messages.get(2), is("Message 7"));
    }

    @Test
    public void shouldNotFindAnyMessagesIfAllMessagesHaveAlreadyBeenSeen() {
        Auditor auditor = new Auditor(3);
        auditor.audit(NORMAL).with("data", "Message 1").done();
        auditor.audit(NORMAL).with("data", "Message 2").done();
        auditor.audit(NORMAL).with("data", "Message 3").done();

        long messageIndexOfLastMessage = auditor.messagesSince(0).get(2).index();

        List<AuditMessage> newMessages = auditor.messagesSince(messageIndexOfLastMessage);
        assertThat(newMessages.size(), is(0));
    }

    @Test
    public void shouldNotFindAnyMessagesWhenAWrongMessageIndexIfProvided() {
        Auditor auditor = new Auditor(3);
        auditor.audit(NORMAL).with("data", "Message 1").done();
        auditor.audit(NORMAL).with("data", "Message 2").done();
        auditor.audit(NORMAL).with("data", "Message 3").done();

        long messageIndexOfLastMessage = auditor.messagesSince(0).get(2).index();

        List<AuditMessage> newMessages = auditor.messagesSince(messageIndexOfLastMessage + 10);
        assertThat(newMessages.size(), is(0));
    }

    private void assertData(AuditMessage message, Matcher<String> expectedDataMatcher) {
        assertThat(message.data().get("data"), expectedDataMatcher);
    }

}
