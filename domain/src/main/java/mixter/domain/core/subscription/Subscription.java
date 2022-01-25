package mixter.domain.core.subscription;

import mixter.doc.Aggregate;
import mixter.doc.Projection;
import mixter.domain.DecisionProjectionBase;
import mixter.domain.Event;
import mixter.domain.EventPublisher;
import mixter.domain.core.message.MessageId;
import mixter.domain.core.subscription.events.FolloweeMessageQuacked;
import mixter.domain.core.subscription.events.UserFollowed;
import mixter.domain.core.subscription.events.UserUnfollowed;
import mixter.domain.identity.UserId;

import java.util.List;

@Aggregate
public class Subscription {

    private final DecisionProjection projection;

    public Subscription(List<Event> history) {
        projection = new DecisionProjection(history);
    }

    public static void follow(UserId follower, UserId followee, EventPublisher eventPublisher) {
        eventPublisher.publish(new UserFollowed(new SubscriptionId(follower, followee)));
    }

    public void unfollow(EventPublisher eventPublisher) {
        eventPublisher.publish(new UserUnfollowed(projection.getSubscriptionId()));
    }

    public void notifyFollower(MessageId messageId, EventPublisher eventPublisher) {
        eventPublisher.publish(new FolloweeMessageQuacked(projection.getSubscriptionId(), messageId));
    }

    @Projection
    private class DecisionProjection extends DecisionProjectionBase {
        private SubscriptionId subscriptionId;

        public DecisionProjection(List<Event> history) {
            super.register(UserFollowed.class, this::apply);
            history.forEach(this::apply);
        }

        public void apply(UserFollowed event) {
            subscriptionId = event.getSubscriptionId();
        }

        public SubscriptionId getSubscriptionId() {
            return subscriptionId;
        }
    }
}
