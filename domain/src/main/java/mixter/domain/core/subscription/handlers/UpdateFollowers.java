package mixter.domain.core.subscription.handlers;

import mixter.doc.Handler;
import mixter.domain.core.subscription.FollowerRepository;
import mixter.domain.core.subscription.events.UserFollowed;
import mixter.domain.identity.UserId;

@Handler
public class UpdateFollowers {

    private final FollowerRepository repository;

    public UpdateFollowers(FollowerRepository repository) {
        this.repository = repository;
    }

    public void apply(UserFollowed userFollowed) {
        UserId followee = userFollowed.getSubscriptionId().getFollowee();
        UserId follower = userFollowed.getSubscriptionId().getFollower();
        repository.saveFollower(followee, follower);
    }
}
