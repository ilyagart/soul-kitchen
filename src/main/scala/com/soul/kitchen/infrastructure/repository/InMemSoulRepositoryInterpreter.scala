package com.soul.kitchen.infrastructure.repository

import cats.Applicative
import cats.implicits._
import com.soul.kitchen.domain.souls.Destination.Destination
import com.soul.kitchen.domain.souls.{Soul, SoulRepositoryAlgebra}

import scala.collection.mutable
import scala.util.Random

class InMemSoulRepositoryInterpreter[F[_]: Applicative]
    extends SoulRepositoryAlgebra[F] {

  private[this] val souls: mutable.Map[Long, Soul] = mutable.Map()

  private[this] val generator: Random.type = Random

  def create(soul: Soul): F[Soul] = {
//    val id = generator.nextId().toLong
    val id = generator.nextInt(9999).toLong
    val freshSoul = soul.copy(Some(id))
    souls += (id -> freshSoul)
    freshSoul.pure[F]
  }

  def get(id: Long): F[Option[Soul]] =
    souls get id match {
      case Some(soul) => soul.some.pure[F]
      case _          => none[Soul].pure[F]
    }

  def update(soul: Soul): F[Option[Soul]] = {
    val id = soul.id.get
    souls.update(id, soul)
    souls.get(id).pure[F]
  }

  def delete(id: Long): F[Unit] =
    souls.find(_._1 == id).foreach(s => souls -= s._1).pure[F]

  def findByOwner(owner: String): F[Option[Soul]] =
    souls.values.find(_.owner == owner).pure[F]

  def findByDestination(destination: Destination): F[List[Soul]] =
    souls.values.filter(_.destination == destination).toList.pure[F]

  def list(): F[List[Soul]] = souls.values.toList.pure[F]
}

object InMemSoulRepositoryInterpreter {
  def apply[F[_]: Applicative]: InMemSoulRepositoryInterpreter[F] =
    new InMemSoulRepositoryInterpreter[F]()
}
