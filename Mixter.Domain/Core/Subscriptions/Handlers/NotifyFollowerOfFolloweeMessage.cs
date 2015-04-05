using System.Linq;
using Mixter.Domain.Core.Messages;
using Mixter.Domain.Core.Messages.Events;
using Mixter.Domain.Identity;

namespace Mixter.Domain.Core.Subscriptions.Handlers
{
    public class NotifyFollowerOfFolloweeMessage : 
        IEventHandler<MessagePublished>,
        IEventHandler<ReplyMessagePublished>,
        IEventHandler<MessageRepublished>
    {
        private readonly IFollowersRepository _followersRepository;
        private readonly IEventPublisher _eventPublisher;
        private readonly IEventsDatabase _eventsDatabase;
        private readonly ISubscriptionsRepository _subscriptionsRepository;

        public NotifyFollowerOfFolloweeMessage(IFollowersRepository followersRepository, IEventPublisher eventPublisher, IEventsDatabase eventsDatabase, ISubscriptionsRepository subscriptionsRepository)
        {
            _followersRepository = followersRepository;
            _eventPublisher = eventPublisher;
            _eventsDatabase = eventsDatabase;
            _subscriptionsRepository = subscriptionsRepository;
        }

        public void Handle(MessagePublished evt)
        {
            NotifyAllFollowers(evt.Author, evt.Author, evt.Id, evt.Content);
        }

        public void Handle(ReplyMessagePublished evt)
        {
            NotifyAllFollowers(evt.Replier, evt.Replier, evt.ReplyId, evt.ReplyContent);
        }

        public void Handle(MessageRepublished evt)
        {
            var messagePublished = _eventsDatabase.GetEventsOfAggregate(evt.Id).OfType<MessagePublished>().First();

            NotifyAllFollowers(evt.Republisher, messagePublished.Author, evt.Id, messagePublished.Content);
        }

        private void NotifyAllFollowers(UserId followee, UserId author, MessageId messageId, string content)
        {
            foreach (var follower in _followersRepository.GetFollowers(followee))
            {
                var subscription = _subscriptionsRepository.Get(new SubscriptionId(follower, followee));
                subscription.NotifyFollower(_eventPublisher, messageId, content);
            }
        }
    }
}