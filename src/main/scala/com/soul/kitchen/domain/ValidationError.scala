package com.soul.kitchen.domain

import com.soul.kitchen.domain.souls.Soul

sealed trait ValidationError
final case class SoulAlreadyExistsError(soul: Soul) extends ValidationError
final case object SoulNotFoundError                 extends ValidationError
