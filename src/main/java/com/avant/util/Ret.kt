package com.avant.util

import org.springframework.http.ResponseEntity

object Ret {
	
	fun ok() = status(200, "OK")
	fun ok(vararg data: Any) = statusAny("OK", 200, *data)
	fun ok(vararg data: Pair<Any, Any>) = status("OK", 200, *data)
	
	fun status(code: Int, vararg data: Any) = statusAny(if (code !in 200..299) "error" else "ok", code, *data)
	fun status(code: Int, vararg data: Pair<Any, Any>) = status(if (code !in 200..299) "error" else "ok", code, *data)
	
	fun error(code: Int, message: Any) = status(message.toString(), code)
	
	private fun statusAny(message: String, code: Int, vararg data: Any) = when {
		data.size == 1     -> status(message, code, "data" to data[0])
		data.size % 2 != 0 -> status(message, code, "data" to data)
		else               -> {
			val args = mutableListOf<Pair<Any, Any>>()
			for (i in 0 until data.size step 2) {
				args += data[i].toString() to data[i + 1]
			}
			status(message, code, *args.toTypedArray())
		}
	}
	
	fun status(message: String, code: Int, vararg data: Pair<Any, Any>): ResponseEntity<*> {
		val map = HashMap<String, Any>()
		if (code !in 200..299) {
			map["message"] = message
			map["status"] = "error"
		} else {
			map["status"] = "ok"
		}
		data.forEach { map[it.first.toString()] = it.second }
		return ResponseEntity.status(code).body(map)
	}
}