package io.sdkman

import com.mongodb.ConnectionString
import com.typesafe.config.ConfigFactory
import org.mongodb.scala._
import org.mongodb.scala.connection.ClusterSettings

trait MongoConnection {

  lazy val config = ConfigFactory.load()

  lazy val mongoHost = config.getString("mongo.host")

  lazy val mongoPort = config.getInt("mongo.port")

  lazy val mongoDatabase = config.getString("mongo.database")

  lazy val mongoUsername = config.getString("mongo.username")

  lazy val mongoPassword = config.getString("mongo.password")

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

