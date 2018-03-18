package com.avant.util

import org.springframework.http.ResponseEntity

object Ret {

	fun ok(vararg data: Any) = status("OK", 200, *data)

	fun status(code: Int, vararg data: Any) = status(if (code !in 200..299) "error" else "ok", code, *data)

	fun error(code: Int, message: Any) = status(message.toString(), code)

	fun status(message: String, code: Int, vararg data: Any): ResponseEntity<*> {
		val map = HashMap<String, Any>()
		if (code !in 200..299) {
			map["message"] = message
		}
		if (data.isNotEmpty()) {
			map["data"] = data
		}
		return ResponseEntity.status(code).body(map)
	}

	fun code(code: Int, message: String) = status(code, message)
	fun code(code: Int, vararg data: Any) = if (data.size == 1) {
		status(code, data[0])
	} else {
		status(code, data)
	}
}