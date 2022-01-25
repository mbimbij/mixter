package mixter.domain.core.message.events;

import mixter.domain.AggregateId;
import mixter.domain.Event;
import mixter.domain.core.message.MessageId;

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
