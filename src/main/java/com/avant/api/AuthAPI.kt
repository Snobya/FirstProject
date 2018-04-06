package com.avant.api

import com.avant.auth.AuthController
import com.avant.auth.AuthReasonController
import com.avant.util.Ret
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthAPI(
		val authController: AuthController,
		val authReasonController: AuthReasonController) {
	
	/**
	 * Returns refresh token by login and pass. Returns code 403 if access is denied (wrong login or pass).
	 */
	@PostMapping("/login")
	fun login(@RequestParam login: String, @RequestParam pass: String): ResponseEntity<*> {
		return Ret.ok(authController.login(login, pass))
	}
	
	/**
	 * Grant access token via provided refresh token
	 */
	@PostMapping("/access")
	fun access(@RequestParam refreshToken: String): ResponseEntity<*> {
		return Ret.ok(authController.createAccessToken(refreshToken))
	}
	
	/**
	 * Reset timeout for access token
	 */
	@PostMapping("/update")
	fun update(@RequestParam token: String): ResponseEntity<*> {
		authController.renewToken(token).run { return if (this) Ret.ok("Successfully") else Ret.error(403, "Token is invalid.") }
	}
	
}