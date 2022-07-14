package com.tlmurphy.short

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.collection.immutable
import scala.util.Random

final case class Url(url: String)
// For the URL model, shortUrlName will be used as the key as it should be unique
final case class ShortUrl(shortUrl: String, originalUrl: String) {
  override def toString: String = s"($shortUrl) -> ($originalUrl)"
}
final case class ShortUrls(urls: immutable.Seq[ShortUrl])

object UrlRegistry {
  sealed trait Command
  final case class GetShortUrls(replyTo: ActorRef[ShortUrls]) extends Command
  final case class CreateShortUrl(
      originalUrl: Url,
      replyTo: ActorRef[ActionPerformed]
  ) extends Command
  final case class GetShortUrl(
      name: String,
      replyTo: ActorRef[GetShortUrlResponse]
  ) extends Command
  final case class DeleteShortUrl(
      name: String,
      replyTo: ActorRef[ActionPerformed]
  ) extends Command
  final case class ResolveShortUrl(
      name: String,
      replyTo: ActorRef[ResolveShortUrlResponse]
  ) extends Command

  final case class GetShortUrlResponse(maybeShortUrl: Option[ShortUrl])
  final case class ResolveShortUrlResponse(maybeShortUrl: Option[String])
  final case class ActionPerformed(description: String)

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(urls: Set[ShortUrl]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetShortUrls(replyTo) =>
        replyTo ! ShortUrls(urls.toSeq)
        Behaviors.same
      case CreateShortUrl(originalUrl, replyTo) =>
        val randomAlphanumeric = new Random().alphanumeric.take(7).mkString
        val newShortUrl = ShortUrl(randomAlphanumeric, originalUrl.url)
        replyTo ! ActionPerformed(s"Short URL ${newShortUrl.shortUrl} created.")
        registry(urls + newShortUrl)
      case GetShortUrl(name, replyTo) =>
        replyTo ! GetShortUrlResponse(urls.find(_.shortUrl == name))
        Behaviors.same
      case DeleteShortUrl(name, replyTo) =>
        replyTo ! ActionPerformed(s"Short URL $name deleted.")
        registry(urls.filterNot(_.shortUrl == name))
      case ResolveShortUrl(name, replyTo) =>
        replyTo ! ResolveShortUrlResponse(
          urls.find(_.shortUrl == name).map(_.originalUrl)
        )
        Behaviors.same
    }
}
