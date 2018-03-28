package com.avant.util

import java.util.*

object IDGenerator {
	
	fun shortId() = UUID.randomUUID().toString().substring(0..7)
}