package mixter.domain.core.message.events;

import lombok.EqualsAndHashCode;
import mixter.domain.AggregateId;
import mixter.domain.Event;
import mixter.domain.core.message.MessageId;

@EqualsAndHashCode
public class MessageDeleted implements Event {
  private MessageId messageId;

  public MessageDeleted(MessageId messageId) {
    this.messageId = messageId;
  }

  @Override
  public AggregateId getId() {
    return messageId;
  }
}
