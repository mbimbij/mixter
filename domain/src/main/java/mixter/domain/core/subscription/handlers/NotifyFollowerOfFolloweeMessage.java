package mixter.domain.core.subscription.handlers;

import mixter.domain.EventPublisher;
import mixter.domain.core.message.MessageId;
import mixter.domain.core.message.events.MessageQuacked;
import mixter.domain.core.message.events.MessageRequacked;
import mixter.domain.core.subscription.FollowerRepository;
import mixter.domain.core.subscription.Subscription;
import mixter.domain.core.subscription.SubscriptionId;
import mixter.domain.core.subscription.SubscriptionRepository;
import mixter.domain.identity.UserId;

import java.util.Set;

public class NotifyFollowerOfFolloweeMessage {
    private final FollowerRepository followerRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final EventPublisher eventPublisher;

    public NotifyFollowerOfFolloweeMessage(FollowerRepository followerRepository, SubscriptionRepository subscriptionRepository, EventPublisher eventPublisher) {
        this.followerRepository = followerRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.eventPublisher = eventPublisher;
    }

    public void apply(MessageQuacked messageQuacked) {
        UserId authorId = messageQuacked.getAuthorId();
        MessageId messageId = messageQuacked.getMessageId();
        notifySubscribers(authorId, messageId);
    }

    public void apply(MessageRequacked messageRequacked) {
        UserId authorId = messageRequacked.getAuthorId();
        MessageId messageId = messageRequacked.getMessageId();
        notifySubscribers(authorId, messageId);
    }

    private void notifySubscribers(UserId authorId, MessageId messageId) {
        Set<UserId> followers = followerRepository.getFollowers(authorId);
        for (UserId followerId : followers) {
            Subscription subscription = subscriptionRepository.getById(new SubscriptionId(followerId, authorId));
            subscription.notifyFollower(messageId, eventPublisher);
        }
    }
}
