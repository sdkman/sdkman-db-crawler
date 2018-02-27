package io.sdkman

import org.mongodb.scala.ScalaObservable
import org.mongodb.scala.model.Sorts.ascending

import scala.concurrent.Future

trait VersionsRepo extends MongoConnection {

  def findAllVersions(): Future[Seq[Version]] =
    versionsCollection
      .find()
      .sort(ascending("candidate", "version", "platform"))
      .map(doc => doc: Version)
      .toFuture()
}

case class Version(candidate: String, version: String, platform: String, url: String)