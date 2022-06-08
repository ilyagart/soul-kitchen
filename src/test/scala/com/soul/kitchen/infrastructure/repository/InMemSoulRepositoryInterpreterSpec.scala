package com.soul.kitchen.infrastructure.repository

import cats.effect.IO
import cats.effect.concurrent.Ref
import com.soul.kitchen.domain.souls.{Destination, Soul}
import org.scalatest.funspec.AnyFunSpec

class InMemSoulRepositoryInterpreterSpec extends AnyFunSpec {

  describe("Soul repo ") {
    it("gets and creates souls") {

      val test: IO[(Soul, Option[Soul])] = for {
        ref <- Ref[IO].of(Map.empty[Long, Soul])
        dbInit = InMemSoulRepositoryInterpreter(ref)
        firstSoul <- dbInit.create(
          Soul(1, "name", "owner", Destination.Heaven, 10)
        )
        secondSoul <- dbInit.get(firstSoul.id)
      } yield (firstSoul, secondSoul)
      val (firstSoul, secondSoul) = test.unsafeRunSync()
      secondSoul match {
        case Some(value) => assert(firstSoul == value)
        case None        => fail("something is wrong")
      }
    }
  }
}

object InMemSoulRepositoryInterpreterSpec {}
