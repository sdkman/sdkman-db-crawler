package io.sdkman

import com.mongodb.ConnectionString
import org.mongodb.scala._
import org.mongodb.scala.connection.ClusterSettings

trait MongoConnection extends Configuration {

  def credential = MongoCredential.createCredential(mongoUsername, mongoDatabase, mongoPassword.toCharArray)

  lazy val clusterSettings = ClusterSettings.builder()
    .applyConnectionString(new ConnectionString(s"mongodb://$mongoHost:$mongoPort"))
    .build()

  lazy val clientSettings = MongoClientSettings.builder()
    .credential(credential)
    .clusterSettings(clusterSettings)
    .build()

  lazy val mongoClient = if (mongoHost == "localhost") MongoClient(s"mongodb://$mongoHost:$mongoPort") else MongoClient(clientSettings)

  def db = mongoClient.getDatabase(mongoDatabase)

  def versionsCollection = db.getCollection("versions")
}

