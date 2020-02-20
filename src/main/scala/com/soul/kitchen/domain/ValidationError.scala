package com.soul.kitchen.domain

import com.soul.kitchen.domain.souls.Soul

sealed trait ValidationError
case class SoulAlreadyExistsError(soul: Soul) extends ValidationError
case object SoulNotFoundError extends ValidationError
