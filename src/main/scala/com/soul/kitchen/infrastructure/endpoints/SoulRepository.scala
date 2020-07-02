package com.soul.kitchen.infrastructure.endpoints

import cats.Monad
import cats.effect._
import cats.implicits._
import com.soul.kitchen.domain.souls.{ Destination, Soul, SoulService }
import com.soul.kitchen.domain.{ SoulAlreadyExistsError, SoulNotFoundError }
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class SoulRepository[F[_]: Monad: Sync] extends Http4sDsl[F] {

  implicit val soulEncoder: Encoder[Soul] = Encoder.instance { soul: Soul =>
    s"$soul".asJson
  }

  implicit val destinationDecoder: Decoder[Destination.Value] =
    Destination.enumDecoder(Destination)

  implicit val soulDecoder: EntityDecoder[F, Soul] =
    jsonOf[F, Soul]

  private def endpoints(soulService: SoulService[F]) =
    getAllSouls(soulService) <+> getSoulById(soulService) <+>
      createSoul(soulService) <+> updateSoul(soulService) <+>
      deleteSoul(soulService)

  private[this] def createSoul(soulService: SoulService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root =>
        val result = for {
          soul     <- req.as[Soul]
          response <- soulService.create(soul).value
        } yield response

        result.flatMap {
          case Right(soul) => Ok(soul.asJson)
          case Left(SoulAlreadyExistsError(soul)) =>
            Conflict(
              s"Soul with following name: ${soul.name} and owner: ${soul.owner} already exists"
            )
        }
    }

  private[this] def getSoulById(soulService: SoulService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / id =>
        soulService.get(id.toLong).value flatMap {
          case Right(soul)             => Ok(soul.asJson)
          case Left(SoulNotFoundError) => NotFound("Soul not found")
        }
    }

  private[this] def getAllSouls(soulService: SoulService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root =>
        for {
          souls <- soulService.list()
          resp  <- Ok(souls.asJson)
        } yield resp
    }

  private[this] def updateSoul(soulService: SoulService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ PUT -> Root =>
        val result = for {
          soul     <- req.as[Soul]
          response <- soulService.update(soul).value
        } yield response

        result.flatMap {
          case Right(value)            => Ok(value.asJson)
          case Left(SoulNotFoundError) => NotFound("Soul not found")
        }
    }

  private[this] def deleteSoul(soulService: SoulService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case DELETE -> Root / soulId =>
        val result = for {
          soul     <- soulService.get(soulId.toLong)
          response <- soulService.delete(soul)
        } yield (soul, response)

        result.value.flatMap {
          case Right(value)            => Ok(s"soul with id: $soulId and name ${value._1.name} got deleted")
          case Left(SoulNotFoundError) => NotFound("unlucky")
        }
    }

}

object SoulRepository {
  def apply[F[_]: Monad: Sync](soulService: SoulService[F]): HttpRoutes[F] =
    new SoulRepository[F].endpoints(soulService)
}
