package com.tlmurphy.short

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import java.net.URL
import scala.collection.immutable
import scala.util.{Random, Try}

final case class Url(url: String)
// For the URL model, shortUrlName will be used as the key as it should be unique
final case class ShortUrl(shortUrl: String, originalUrl: String) {
  override def toString: String = s"($shortUrl) -> ($originalUrl)"
}
final case class ShortUrls(urls: immutable.Seq[ShortUrl])
case class ResponseFailure(description: String)

object UrlRegistry {
  sealed trait Command
  final case class GetShortUrls(replyTo: ActorRef[ShortUrls]) extends Command
  final case class CreateShortUrl(
      originalUrl: Url,
      replyTo: ActorRef[Either[CreateFailure, CreateSuccess]]
  ) extends Command
  final case class GetShortUrl(
      name: String,
      replyTo: ActorRef[Either[GetFailure, GetSuccess]]
  ) extends Command
  final case class DeleteShortUrl(
      name: String,
      replyTo: ActorRef[Either[DeleteFailure, DeleteSuccess]]
  ) extends Command
  final case class ResolveShortUrl(
      name: String,
      replyTo: ActorRef[Either[ResolveShortUrlFailure, ResolveShortUrlSuccess]]
  ) extends Command

  final case class CreateSuccess(shortUrl: ShortUrl)
  final case class CreateFailure(description: String)
  final case class DeleteFailure(description: String)
  final case class DeleteSuccess(description: String)
  final case class GetSuccess(shortUrl: ShortUrl)
  final case class GetFailure(description: String)
  final case class ResolveShortUrlSuccess(shortUrl: String)
  final case class ResolveShortUrlFailure(description: String)

  def validUrl(url: String): Boolean = Try(new URL(url).toURI).isSuccess

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(urls: Set[ShortUrl]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetShortUrls(replyTo) =>
        replyTo ! ShortUrls(urls.toSeq)
        Behaviors.same
      case CreateShortUrl(originalUrl, replyTo) =>
        // handle error as well as handle a url that already has a short URL made for it
        if (!validUrl(originalUrl.url)) {
          replyTo ! Left(CreateFailure("URL is not valid."))
          Behaviors.same
        } else if (urls.exists(_.originalUrl == originalUrl.url)) {
          replyTo ! Left(CreateFailure("URL mapping already exists."))
          Behaviors.same
        } else {
          val randomAlphanumeric = new Random().alphanumeric.take(7).mkString
          val newShortUrl = ShortUrl(randomAlphanumeric, originalUrl.url)
          replyTo ! Right(CreateSuccess(newShortUrl))
          registry(urls + newShortUrl)
        }
      case GetShortUrl(name, replyTo) =>
        urls.find(_.shortUrl == name) match {
          case Some(url) =>
            replyTo ! Right(GetSuccess(url))
          case None =>
            replyTo ! Left(GetFailure(s"$name does not exist."))
        }
        Behaviors.same
      case DeleteShortUrl(name, replyTo) =>
        if (!urls.exists(_.shortUrl == name)) {
          replyTo ! Left(DeleteFailure(s"$name does not exist."))
          Behaviors.same
        } else {
          replyTo ! Right(DeleteSuccess(s"Short URL $name deleted."))
          registry(urls.filterNot(_.shortUrl == name))
        }
      case ResolveShortUrl(name, replyTo) =>
        urls.find(_.shortUrl == name).map(_.originalUrl) match {
          case Some(url) =>
            replyTo ! Right(ResolveShortUrlSuccess(url))
          case None =>
            replyTo ! Left(ResolveShortUrlFailure(s"$name does not exist."))
        }
        Behaviors.same
    }
}
