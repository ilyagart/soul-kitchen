package com.soul.kitchen

import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._
import com.soul.kitchen.config._
import com.soul.kitchen.domain.souls._
import com.soul.kitchen.infrastructure.endpoints._
import com.soul.kitchen.infrastructure.repository._
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.server.{Router, Server}
import pureconfig._
import pureconfig.module.catseffect.syntax._

object Server extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    createServer.use(_ => IO.never).as(ExitCode.Success)

  def createServer: Resource[IO, Server[IO]] =
    for {
      config <- Resource.liftF(ConfigSource.default.loadF[IO, AppConfig])
      soulsCache <- Resource.liftF(Ref[IO].of(Map.empty[Long, Soul]))
      soulRepo = InMemSoulRepositoryInterpreter[IO](soulsCache)
      soulValidator = SoulValidationInterpreter[IO](soulRepo)
      soulService = SoulService[IO](soulRepo, soulValidator)
      httpApp = Router("/soul" -> SoulRepository[IO](soulService)).orNotFound
      _ <- Resource.liftF(IO(DbInitializer.initializeDb(soulRepo)))
      server <- BlazeServerBuilder[IO]
        .bindHttp(config.server.port, config.server.host)
        .withHttpApp(httpApp)
        .resource
    } yield server
}
