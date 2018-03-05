package io.sdkman

import com.typesafe.config.ConfigFactory

trait Configuration {

  private lazy val config = ConfigFactory.load()

  lazy val mongoHost = config.getString("mongo.host")

  lazy val mongoPort = config.getInt("mongo.port")

  lazy val mongoDatabase = config.getString("mongo.database")

  lazy val mongoUsername = config.getString("mongo.username")

  lazy val mongoPassword = config.getString("mongo.password")

  lazy val smtpHost = config.getString("smtp.host")

  lazy val smtpPort = config.getInt("smtp.port")

  lazy val smtpFromEmail = config.getString("smtp.email.from")

  lazy val smtpToEmail = config.getString("smtp.email.to")

  lazy val smtpUser = config.getString("smtp.user")

  lazy val smtpPassword = config.getString("smtp.password")
}
