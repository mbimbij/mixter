package mixter.domain.core.message;

import mixter.doc.Aggregate;
import mixter.doc.Projection;
import mixter.domain.DecisionProjectionBase;
import mixter.domain.Event;
import mixter.domain.EventPublisher;
import mixter.domain.core.message.events.MessageDeleted;
import mixter.domain.core.message.events.MessageQuacked;
import mixter.domain.core.message.events.MessageRequacked;
import mixter.domain.identity.UserId;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Aggregate
public class Message {
    private DecisionProjection projection;

    public Message(List<Event> history) {
        projection = new DecisionProjection(history);
    }

    public static MessageId quack(UserId authorId, String message, EventPublisher eventPublisher) {
        MessageId messageId = MessageId.generate();
        ;
        eventPublisher.publish(new MessageQuacked(messageId, message, authorId));
        return messageId;
    }

    public void reQuack(UserId userId, EventPublisher eventPublisher, UserId authorId, String message) {
        if (projection.publishers.contains(userId)) {
            return;
        }
        MessageRequacked event = new MessageRequacked(projection.getId(), userId, authorId, message);
        eventPublisher.publish(event);
    }

    public void delete(UserId authorId, EventPublisher eventPublisher) {
        if (!Objects.equals(projection.getAuthor(), authorId)) {
            return;
        }
        eventPublisher.publish(new MessageDeleted(projection.getId()));
    }

    @Projection
    private class DecisionProjection extends DecisionProjectionBase {
        private MessageId id;
        public Set<UserId> publishers = new HashSet<>();
        private UserId author;

        public DecisionProjection(List<Event> history) {
            super.register(MessageQuacked.class, this::apply);
            super.register(MessageRequacked.class, this::apply);
            history.forEach(this::apply);
        }

        private void apply(MessageQuacked event) {
            id = event.getMessageId();
            author = event.getAuthorId();
            publishers.add(event.getAuthorId());
        }

        private void apply(MessageRequacked event) {
            publishers.add(event.getUserId());
        }

        public MessageId getId() {
            return id;
        }

        public UserId getAuthor() {
            return author;
        }
    }
}
