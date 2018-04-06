package com.avant.util

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun String.join(middle: String = "", vararg others: String): String {
	val ret = StringBuilder(this)
	others.forEach {
		ret.append(middle).append(it)
	}
	return ret.toString()
}

fun Any.print(prefix: String = "") {
	println(prefix + this.toString())
}

fun <T> T.isAnyOf(vararg objs: T): Boolean {
	return objs.any { this == it }
}

fun Random.int(range: IntRange): Int = this.nextInt(range.endInclusive - range.start) + range.start

fun <T> Iterable<T>.findOne(function: (T) -> Boolean): T? {
	for (e in this) {
		if (function(e)) {
			return e
		}
	}
	return null
}

fun LocalDateTime.toMillis(): Long = this.atZone(ZoneOffset.ofTotalSeconds(0)).toInstant().toEpochMilli()

fun ignoreErrors(function: () -> Unit) = try {
	function()
} catch (e: Exception) {
}