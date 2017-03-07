package mixter.infra.repositories

import mixter.domain.identity.UserId
import mixter.domain.message.{MessageId, TimelineMessageProjection, TimelineMessageRepository}

class InMemoryTimelineRepository extends TimelineMessageRepository {

  var messages: Set[TimelineMessageProjection] = Set.empty

  override def save(message:TimelineMessageProjection):TimelineMessageProjection = {
    messages = messages + message
    message
  }

  override def getMessageOfUser(ownerId: UserId): Iterable[TimelineMessageProjection] =
    messages.filter(_.ownerId==ownerId)

  def delete(messageId: MessageId):Unit = {
    messages = messages.filterNot(_.messageId==messageId)
  }
}
