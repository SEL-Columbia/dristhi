package org.ei.drishti.common;

import org.ei.drishti.common.audit.AuditMessage;
import org.ei.drishti.common.audit.Auditor;
import org.junit.Test;

import java.util.List;

import static org.ei.drishti.common.audit.AuditMessageType.NORMAL;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AuditorTest {
    @Test
    public void shouldHaveOnlyAsManyMessagesAsTheSizeOfTheAuditorLog() {
        Auditor auditor = new Auditor(2);
        auditor.audit(NORMAL, "Message 1");
        auditor.audit(NORMAL, "Message 2");
        auditor.audit(NORMAL, "Message 3");

        assertThat(auditor.messagesSince(0).size(), is(2));
    }

    @Test
    public void shouldKeepOnlyTheNewestMessagesWhenRemovingMessagesWhichOverflow() {
        Auditor auditor = new Auditor(2);
        auditor.audit(NORMAL, "Message 1");
        auditor.audit(NORMAL, "Message 2");
        auditor.audit(NORMAL, "Message 3");

        List<AuditMessage> messages = auditor.messagesSince(0);
        assertThat(messages.get(0).message(), is("Message 2"));
        assertThat(messages.get(1).message(), is("Message 3"));
    }

    @Test
    public void shouldGiveAllMessageWhenMessageIndexBeingSearchedForIsZeroOrNegative() {
        Auditor auditor = new Auditor(3);
        auditor.audit(NORMAL, "Message 1");
        auditor.audit(NORMAL, "Message 2");
        auditor.audit(NORMAL, "Message 3");
        auditor.audit(NORMAL, "Message 4");
        auditor.audit(NORMAL, "Message 5");

        List<AuditMessage> messages = auditor.messagesSince(0);
        assertThat(messages.size(), is(3));
        assertThat(messages.get(0).message(), is("Message 3"));
        assertThat(messages.get(1).message(), is("Message 4"));
        assertThat(messages.get(2).message(), is("Message 5"));

        messages = auditor.messagesSince(-10);
        assertThat(messages.size(), is(3));
        assertThat(messages.get(0).message(), is("Message 3"));
        assertThat(messages.get(1).message(), is("Message 4"));
        assertThat(messages.get(2).message(), is("Message 5"));
    }

    @Test
    public void shouldBeAbleToFindMessagesSubmittedSinceAGivenIndex() throws Exception {
        Auditor auditor = new Auditor(3);
        auditor.audit(NORMAL, "Message 1");
        auditor.audit(NORMAL, "Message 2");
        auditor.audit(NORMAL, "Message 3");

        long messageIndexOfLastMessage = auditor.messagesSince(0).get(2).index();

        auditor.audit(NORMAL, "Message 4");
        auditor.audit(NORMAL, "Message 5");

        List<AuditMessage> newMessages = auditor.messagesSince(messageIndexOfLastMessage);
        assertThat(newMessages.size(), is(2));
        assertThat(newMessages.get(0).message(), is("Message 4"));
        assertThat(newMessages.get(1).message(), is("Message 5"));
    }

    @Test
    public void shouldBeAbleToSearchForMessagesByIndexEvenIfThereHaveBeenManyMessagesInBetween() {
        Auditor auditor = new Auditor(3);
        auditor.audit(NORMAL, "Message 1");

        long messageIndexOfLastMessage = auditor.messagesSince(0).get(0).index();

        auditor.audit(NORMAL, "Message 2");
        auditor.audit(NORMAL, "Message 3");
        auditor.audit(NORMAL, "Message 4");
        auditor.audit(NORMAL, "Message 5");
        auditor.audit(NORMAL, "Message 6");
        auditor.audit(NORMAL, "Message 7");

        List<AuditMessage> messages = auditor.messagesSince(messageIndexOfLastMessage);
        assertThat(messages.size(), is(3));
        assertThat(messages.get(0).message(), is("Message 5"));
        assertThat(messages.get(1).message(), is("Message 6"));
        assertThat(messages.get(2).message(), is("Message 7"));
    }

    @Test
    public void shouldNotFindAnyMessagesIfAllMessagesHaveAlreadyBeenSeen() {
        Auditor auditor = new Auditor(3);
        auditor.audit(NORMAL, "Message 1");
        auditor.audit(NORMAL, "Message 2");
        auditor.audit(NORMAL, "Message 3");

        long messageIndexOfLastMessage = auditor.messagesSince(0).get(2).index();

        List<AuditMessage> newMessages = auditor.messagesSince(messageIndexOfLastMessage);
        assertThat(newMessages.size(), is(0));
    }

    @Test
    public void shouldNotFindAnyMessagesWhenAWrongMessageIndexIfProvided() {
        Auditor auditor = new Auditor(3);
        auditor.audit(NORMAL, "Message 1");
        auditor.audit(NORMAL, "Message 2");
        auditor.audit(NORMAL, "Message 3");

        long messageIndexOfLastMessage = auditor.messagesSince(0).get(2).index();

        List<AuditMessage> newMessages = auditor.messagesSince(messageIndexOfLastMessage + 10);
        assertThat(newMessages.size(), is(0));
    }
}
