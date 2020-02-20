package com.soul.kitchen.infrastructure.repository

import com.soul.kitchen.domain.souls.Destination._
import com.soul.kitchen.domain.souls.{Soul, SoulRepositoryAlgebra}

object DbInitializer {

  def initializeDb[F[_]](
    soulRepositoryAlgebra: SoulRepositoryAlgebra[F]
  ): Unit = {
    soulRepositoryAlgebra.create(Soul(None, "John", "Plato", Heaven, 100))
    soulRepositoryAlgebra.create(Soul(None, "Martin", "Luther", Hell, 42))
    soulRepositoryAlgebra.create(Soul(None, "Arthur", "Pendragon", Unknown, 73))
    soulRepositoryAlgebra.create(Soul(None, "Trinity", "Morpheus", Heaven, 1))
  }
}
