package com.soul.kitchen.domain.souls

import cats.Applicative
import cats.data.EitherT
import cats.implicits._
import com.soul.kitchen.domain.{SoulAlreadyExistsError, SoulNotFoundError}

class SoulValidationInterpreter[F[_]: Applicative](
  soulRepository: SoulRepositoryAlgebra[F]
) extends SoulValidationAlgebra[F] {

  def exists(soulId: Option[Long]): EitherT[F, SoulNotFoundError.type, Unit] =
    EitherT {
      soulId match {
        case Some(id) =>
          soulRepository.get(id).map {
            case Some(_) => Right(())
            case _       => Left(SoulNotFoundError)
          }
        case _ =>
          Either.left[SoulNotFoundError.type, Unit](SoulNotFoundError).pure[F]
      }
    }

  def doesNotExist(soul: Soul): EitherT[F, SoulAlreadyExistsError, Unit] =
    EitherT {
      soulRepository.findByOwner(soul.owner).map { matches =>
        if (matches.isEmpty) Right(()) else Left(SoulAlreadyExistsError(soul))
      }
    }
}

object SoulValidationInterpreter {
  def apply[F[_]: Applicative](
    soulRepositoryAlgebra: SoulRepositoryAlgebra[F]
  ): SoulValidationInterpreter[F] =
    new SoulValidationInterpreter(soulRepositoryAlgebra)
}
