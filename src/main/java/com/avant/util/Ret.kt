package com.avant.util

import org.springframework.http.ResponseEntity

/**
 * Wrapper class for ResponseEntity
 * Every object produced by this class is
 */
object Ret {
	
	fun ok(vararg param: Any): ResponseEntity<Any> = code(200, param)
	
	fun code(status: Int, vararg param: Any): ResponseEntity<Any> {
		val ret = ResponseEntity.status(status)
		val map = mutableMapOf<Any, Any>()
		when (param.size) {
			1    -> map["status"] = param[0]
			0    -> map["status"] = ""
			else -> for (i in 0 until param.size step 2) {
				map[i] = i + 1
			}
		}
		return ret.body(map)
	}
	
}