package com.avant.util

import java.util.*

object IDGenerator {
	
	fun longId() = UUID.randomUUID().toString().filter { it != '-' }
	fun shortId() = UUID.randomUUID().toString().substring(0..7)
}