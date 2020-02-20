package com.soul.kitchen.domain.souls

import cats.data.EitherT
import com.soul.kitchen.domain.{SoulAlreadyExistsError, SoulNotFoundError}

trait SoulValidationAlgebra[F[_]] {
  def exists(soulId: Option[Long]): EitherT[F, SoulNotFoundError.type, Unit]
  def doesNotExist(soul: Soul): EitherT[F, SoulAlreadyExistsError, Unit]
}
