package com.soul.kitchen.infrastructure.repository

import cats.Monad
import cats.effect.concurrent.Ref
import cats.implicits._
import com.soul.kitchen.domain.souls.Destination.Destination
import com.soul.kitchen.domain.souls.{Soul, SoulRepositoryAlgebra}

import scala.util.Random

class InMemSoulRepositoryInterpreter[F[_]: Monad](
    soulsRepo: Ref[F, Map[Long, Soul]]
) extends SoulRepositoryAlgebra[F] {

  private[this] val generator: Random.type = Random

  def create(soul: Soul): F[Soul] = {
    val id = generator.nextInt().toLong
    val freshSoul = soul.copy(id)
    for {
      _ <- soulsRepo.update(_ ++ Map(id -> freshSoul))
    } yield freshSoul
  }

  def get(id: Long): F[Option[Soul]] = {
    soulsRepo.get.flatMap(map =>
      map.get(id) match {
        case Some(soul) => soul.some.pure[F]
        case _          => none[Soul].pure[F]
      }
    )
  }

  def update(soul: Soul): F[Option[Soul]] = {
    for {
      _ <- soulsRepo.update(existingMap => existingMap.updated(soul.id, soul))
      soulOpt <- get(soul.id)
    } yield soulOpt
  }

  def delete(id: Long): F[Unit] = {
    for {
      _ <- soulsRepo.update { existingMap =>
        existingMap.removed(id)
        existingMap
      }
    } yield ()
  }

  def findByOwner(owner: String): F[Option[Soul]] = {
    for {
      souls <- soulsRepo.get
    } yield souls.find { case (_, soul) => soul.owner == owner }.map {
      case (_, soul) => soul
    }
  }

  def findByDestination(destination: Destination): F[List[Soul]] =
    for {
      souls <- soulsRepo.get
    } yield souls
      .filter { case (_, soul) => soul.destination == destination }
      .map { case (_, soul) => soul }
      .toList

  def list(): F[List[Soul]] = for {
    map <- soulsRepo.get
  } yield map.values.toList
}

object InMemSoulRepositoryInterpreter {
  def apply[F[_]: Monad](
      soulsRepo: Ref[F, Map[Long, Soul]]
  ): InMemSoulRepositoryInterpreter[F] = {

    new InMemSoulRepositoryInterpreter[F](soulsRepo)
  }
}
