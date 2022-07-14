package com.tlmurphy.short

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable

final case class Url(name: String, shortUrlName: String)
final case class Urls(urls: immutable.Seq[Url])

object UrlRegistry {
  sealed trait Command
  final case class GetUrls(replyTo: ActorRef[Urls]) extends Command
  final case class CreateUrl(url: Url, replyTo: ActorRef[ActionPerformed])
      extends Command
  final case class GetUrl(name: String, replyTo: ActorRef[GetUrlResponse])
      extends Command
  final case class DeleteUrl(name: String, replyTo: ActorRef[ActionPerformed])
      extends Command

  final case class GetUrlResponse(maybeUrl: Option[Url])
  final case class ActionPerformed(description: String)

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(urls: Set[Url]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetUrls(replyTo) =>
        replyTo ! Urls(urls.toSeq)
        Behaviors.same
      case CreateUrl(url, replyTo) =>
        replyTo ! ActionPerformed(s"Url ${url.name} created.")
        registry(urls + url)
      case GetUrl(name, replyTo) =>
        replyTo ! GetUrlResponse(urls.find(_.name == name))
        Behaviors.same
      case DeleteUrl(name, replyTo) =>
        replyTo ! ActionPerformed(s"Url $name deleted.")
        registry(urls.filterNot(_.name == name))
    }
}
