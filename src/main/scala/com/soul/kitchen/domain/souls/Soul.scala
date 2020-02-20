package com.soul.kitchen.domain.souls

import com.soul.kitchen.domain.souls.Destination.Destination
import io.circe.Decoder

import scala.util.Try

object Destination extends Enumeration {
  type Destination = Value
  val Heaven, Hell, Unknown = Value

  def enumDecoder[E <: Enumeration](enum: E): Decoder[E#Value] =
    Decoder.decodeString.flatMap { str =>
      Decoder.instanceTry { _ =>
        Try(enum.withName(str))
      }
    }
}
import com.soul.kitchen.domain.souls.Destination._
case class Soul(id: Option[Long],
                name: String,
                owner: String,
                var destination: Destination = Unknown,
                price: Int) {
  override def toString: String = {
    s"""[$id, $name, $owner, $destination, $price]"""
  }
}
