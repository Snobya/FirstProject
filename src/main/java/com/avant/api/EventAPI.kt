package com.avant.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/event")
class EventAPI {
	
	@GetMapping("/closest")
	fun closest(@RequestParam count: Int): String {
		return "Something"
	}
	
	@GetMapping("/monthly")
	fun monthly(@RequestParam month: String): ResponseEntity<String> {
		return if (month.length > 5) {
			ResponseEntity.ok("Yeah")
		} else {
			ResponseEntity.status(403).body("Not OK")
		}
	}
	
	@PostMapping("/postSomething")
	fun postSomething(@RequestParam text: String, @RequestParam other: String): ResponseEntity<*> {
		return ResponseEntity.ok(text + other)
	}
	
}