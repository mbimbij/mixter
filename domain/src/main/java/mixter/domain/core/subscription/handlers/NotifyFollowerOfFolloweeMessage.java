package mixter.domain.core.subscription.handlers;

import mixter.domain.EventPublisher;
import mixter.domain.core.message.events.MessageQuacked;
import mixter.domain.core.subscription.FollowerRepository;
import mixter.domain.core.subscription.SubscriptionId;
import mixter.domain.core.subscription.SubscriptionRepository;
import mixter.domain.core.subscription.events.FolloweeMessageQuacked;
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
        Set<UserId> followers = followerRepository.getFollowers(authorId);
        followers.forEach(followerId -> {
            SubscriptionId subscriptionId = new SubscriptionId(followerId, authorId);
            eventPublisher.publish(new FolloweeMessageQuacked(subscriptionId, messageQuacked.getMessageId()));
        });
    }
}
