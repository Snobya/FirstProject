package com.avant.auth

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef

import java.util.UUID

class AuthToken(@DBRef private val user: User) {
	
	@Id
	private val token: String = UUID.randomUUID().toString()
	private val timeout: Long = System.currentTimeMillis() + 7_200_000L
	
}