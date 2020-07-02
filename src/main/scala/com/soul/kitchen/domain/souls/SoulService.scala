package com.soul.kitchen.domain.souls

import cats.Monad
import cats.data.EitherT
import com.soul.kitchen.domain.{ SoulAlreadyExistsError, SoulNotFoundError }

class SoulService[F[_]: Monad](
    soulRepositoryAlgebra: SoulRepositoryAlgebra[F],
    soulValidationAlgebra: SoulValidationAlgebra[F]
) {
  def create(soul: Soul): EitherT[F, SoulAlreadyExistsError, Soul] =
    for {
      _    <- soulValidationAlgebra.doesNotExist(soul)
      soul <- EitherT.liftF(soulRepositoryAlgebra.create(soul))
    } yield soul

  def update(soul: Soul): EitherT[F, SoulNotFoundError.type, Soul] =
    for {
      _ <- soulValidationAlgebra.exists(soul.id)
      updated <- EitherT.fromOptionF(
                  soulRepositoryAlgebra.update(soul),
                  SoulNotFoundError
                )
    } yield updated

  def get(id: Long): EitherT[F, SoulNotFoundError.type, Soul] =
    EitherT.fromOptionF(soulRepositoryAlgebra.get(id), SoulNotFoundError)

  def list(): F[List[Soul]] = soulRepositoryAlgebra.list()

  def delete(soul: Soul): EitherT[F, SoulNotFoundError.type, Unit] =
    for {
      _ <- soulValidationAlgebra.exists(soul.id)
      _ <- EitherT.liftF(soulRepositoryAlgebra.delete(soul.id))
    } yield ()
}

object SoulService {
  def apply[F[_]: Monad](
      soulRepositoryAlgebra: SoulRepositoryAlgebra[F],
      soulValidationAlgebra: SoulValidationAlgebra[F]
  ): SoulService[F] =
    new SoulService(soulRepositoryAlgebra, soulValidationAlgebra)
}
