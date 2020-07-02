package com.soul.kitchen.domain.souls

import cats.Applicative
import cats.data.EitherT
import cats.implicits._
import com.soul.kitchen.domain.{ SoulAlreadyExistsError, SoulNotFoundError }

class SoulValidationInterpreter[F[_]: Applicative](
    soulRepository: SoulRepositoryAlgebra[F]
) extends SoulValidationAlgebra[F] {

  def exists(soulId: Long): EitherT[F, SoulNotFoundError.type, Unit] =
    EitherT {
      soulRepository.get(soulId).map {
        case Some(_) => Right(())
        case _       => Left(SoulNotFoundError)
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
