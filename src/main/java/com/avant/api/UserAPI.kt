package com.avant.api

import com.avant.model.UserService
import com.avant.util.Ret
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Api for curators and logins
 */
@RestController
@RequestMapping("/api/user")
class UserAPI(
		val userService: UserService) {
	
	@GetMapping("/{id}")
	fun getById(@PathVariable id: String) = userService.getUser(id).run { Ret.ok(this) }
	
	/**
	 * Register a new curator.
	 * @param isAdmin admins can create new users or modify existing. otherwise, created user is manager/moderator
	 */
	@PostMapping("/register")
	fun register(@RequestParam login: String, @RequestParam pass: String,
	             @RequestParam(defaultValue = "false") isAdmin: Boolean? = null): ResponseEntity<*> {
		return Ret.ok(userService.register(login, pass).user)
	}
	
	@GetMapping("/curators")
	fun getCuratorList() = Ret.ok(userService.curatorList())
	
	/**
	 * Set status or user by ID to curator.
	 * if user's name is null - throws IllegalArgumentException (HTTP code 406).
	 * if isCurator not passed - switches curator flag in user (true - false)
	 */
	@PostMapping("/curator/set")
	fun setCurator(@RequestParam id: String, @RequestParam(required = false) isCurator: Boolean?) =
		Ret.ok(userService.setCurator(id, isCurator))
	
	@PostMapping("/setData")
	fun setData(@RequestParam id: String,
	            @RequestParam(required = false) name: String? = null,
	            @RequestParam(required = false) photo: String? = null,
	            @RequestParam(required = false) phone: String? = null): ResponseEntity<*> {
		return Ret.ok(userService.setData(id, name, photo, phone))
	}
	
	
}