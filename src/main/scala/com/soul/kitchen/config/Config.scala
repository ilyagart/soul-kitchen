package com.soul.kitchen.config

case class ServerConfig(port: Int, host: String)
case class AppConfig(server: ServerConfig)
