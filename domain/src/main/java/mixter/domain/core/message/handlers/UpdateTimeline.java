package mixter.domain.core.message.handlers;

import mixter.domain.core.message.TimelineMessageProjection;
import mixter.domain.core.message.TimelineMessageRepository;
import mixter.domain.core.message.events.MessageDeleted;
import mixter.domain.core.message.events.MessageQuacked;

public class UpdateTimeline {
    private final TimelineMessageRepository timelineRepository;

    public UpdateTimeline(TimelineMessageRepository timelineRepository) {
        this.timelineRepository = timelineRepository;
    }

    public void apply(MessageQuacked messageQuacked) {
        timelineRepository.save(new TimelineMessageProjection(messageQuacked.getAuthorId(),
                messageQuacked.getAuthorId(),
                messageQuacked.getMessage(),
                messageQuacked.getMessageId()));
    }

    public void apply(MessageDeleted messageDeleted) {
        timelineRepository.delete(messageDeleted.getMessageId());
    }
}
