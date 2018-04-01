package com.avant.api

import com.avant.model.UserService
import com.avant.util.Ret
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Api for curators and logins
 */
@RestController
@RequestMapping("/api/user")
class UserAPI(
		val userService: UserService) {
	
	/**
	 * Register a new curator.
	 * @param isAdmin admins can create new users or modify existing. otherwise, created user is manager/moderator
	 */
	@PostMapping("/register")
	fun register(@RequestParam login: String, @RequestParam pass: String,
	             @RequestParam(defaultValue = "false") isAdmin: Boolean? = null): ResponseEntity<*> {
		return Ret.ok(userService.register(login, pass).user)
	}
	
	@PostMapping("/setData")
	fun setData(@RequestParam id: String,
	            @RequestParam(required = false) name: String? = null,
	            @RequestParam(required = false) photo: String? = null,
	            @RequestParam(required = false) phone: String? = null): ResponseEntity<*> {
		return Ret.ok(userService.setData(id, name, photo, phone))
	}
	
}