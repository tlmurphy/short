package com.tlmurphy.short

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable

// For the URL model, shortUrlName will be used as the key as it should be unique
final case class Url(shortUrl: String, originalUrl: String) {
  override def toString: String = s"($shortUrl) -> ($originalUrl)"
}
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
  final case class ResolveShortUrl(
      name: String,
      replyTo: ActorRef[ResolveShortUrlResponse]
  ) extends Command

  final case class GetUrlResponse(maybeUrl: Option[Url])
  final case class ResolveShortUrlResponse(maybeShortUrl: Option[String])
  final case class ActionPerformed(description: String)

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(urls: Set[Url]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetUrls(replyTo) =>
        replyTo ! Urls(urls.toSeq)
        Behaviors.same
      case CreateUrl(url, replyTo) =>
        replyTo ! ActionPerformed(s"Short URL ${url.shortUrl} created.")
        registry(urls + url)
      case GetUrl(name, replyTo) =>
        replyTo ! GetUrlResponse(urls.find(_.shortUrl == name))
        Behaviors.same
      case DeleteUrl(name, replyTo) =>
        replyTo ! ActionPerformed(s"Short URL $name deleted.")
        registry(urls.filterNot(_.shortUrl == name))
      case ResolveShortUrl(name, replyTo) =>
        replyTo ! ResolveShortUrlResponse(
          urls.find(_.shortUrl == name).map(_.originalUrl)
        )
        Behaviors.same
    }
}
