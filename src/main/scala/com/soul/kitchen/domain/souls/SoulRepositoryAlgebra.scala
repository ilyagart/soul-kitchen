package com.soul.kitchen.domain.souls

import com.soul.kitchen.domain.souls.Destination.Destination

trait SoulRepositoryAlgebra[F[_]] {
  def create(soul: Soul): F[Soul]
  def get(id: Long): F[Option[Soul]]
  def update(soul: Soul): F[Option[Soul]]
  def delete(id: Long): F[Unit]
  def findByOwner(owner: String): F[Option[Soul]]
  def findByDestination(destination: Destination): F[List[Soul]]
  def list(): F[List[Soul]]
}
