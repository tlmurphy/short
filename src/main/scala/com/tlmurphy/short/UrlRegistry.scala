package com.tlmurphy.short

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import java.net.URL
import scala.collection.immutable
import scala.util.{Random, Try}

object UrlRegistry {
  // Data Models
  final case class Url(url: String)
  final case class ShortUrl(shortUrl: String, originalUrl: String) {
    override def toString: String = s"($shortUrl) -> ($originalUrl)"
  }
  final case class ShortUrls(urls: immutable.Seq[ShortUrl])

  // Actor/API models
  type ResponseActorRef = ActorRef[Response]
  sealed trait Command
  final case class GetShortUrls(replyTo: ActorRef[ShortUrls]) extends Command
  final case class CreateShortUrl(originalUrl: Url, replyTo: ResponseActorRef)
      extends Command
  final case class GetShortUrl(name: String, replyTo: ResponseActorRef)
      extends Command
  final case class DeleteShortUrl(name: String, replyTo: ResponseActorRef)
      extends Command
  final case class ResolveShortUrl(name: String, replyTo: ResponseActorRef)
      extends Command

  def validUrl(url: String): Boolean = Try(new URL(url).toURI).isSuccess

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(urls: Set[ShortUrl]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetShortUrls(replyTo) =>
        replyTo ! ShortUrls(urls.toSeq)
        Behaviors.same
      case CreateShortUrl(originalUrl, replyTo) =>
        if (!validUrl(originalUrl.url)) {
          replyTo ! Left(ResponseFailure("URL is not valid."))
          Behaviors.same
        } else if (urls.exists(_.originalUrl == originalUrl.url)) {
          replyTo ! Left(ResponseFailure("URL mapping already exists."))
          Behaviors.same
        } else {
          val randomAlphanumeric = new Random().alphanumeric.take(7).mkString
          val newShortUrl = ShortUrl(randomAlphanumeric, originalUrl.url)
          replyTo ! Right(
            ResponseSuccess(
              s"$newShortUrl successfully created",
              Some(newShortUrl)
            )
          )
          registry(urls + newShortUrl)
        }
      case GetShortUrl(name, replyTo) =>
        urls.find(_.shortUrl == name) match {
          case Some(url) =>
            replyTo ! Right(
              ResponseSuccess(s"$url successfully retrieved", Some(url))
            )
          case None =>
            replyTo ! Left(ResponseFailure(s"$name does not exist."))
        }
        Behaviors.same
      case DeleteShortUrl(name, replyTo) =>
        if (!urls.exists(_.shortUrl == name)) {
          replyTo ! Left(ResponseFailure(s"$name does not exist."))
          Behaviors.same
        } else {
          replyTo ! Right(
            ResponseSuccess(s"Short URL $name successfully deleted.")
          )
          registry(urls.filterNot(_.shortUrl == name))
        }
      case ResolveShortUrl(name, replyTo) =>
        urls.find(_.shortUrl == name) match {
          case Some(url) =>
            replyTo ! Right(
              ResponseSuccess(s"$url successfully resolved", Some(url))
            )
          case None =>
            replyTo ! Left(
              ResponseFailure("The requested resource does not exist")
            )
        }
        Behaviors.same
    }
}
