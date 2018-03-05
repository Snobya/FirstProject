package com.avant.util

fun String.join(middle: String = "", vararg others: String): String {
	val ret = StringBuilder(this)
	others.forEach {
		ret.append(middle).append(it)
	}
	return ret.toString()
}