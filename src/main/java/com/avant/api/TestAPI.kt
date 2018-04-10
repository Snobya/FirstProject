package com.avant.api

import com.avant.util.Ret
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.ZonedDateTime

@RestController
@RequestMapping("/api/test")
class TestAPI {
	
	@GetMapping("/time")
	fun time(): ResponseEntity<*> {
		return Ret.ok(ZonedDateTime.now())
	}
	
}