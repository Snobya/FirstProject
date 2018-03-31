package com.avant.entity

import com.avant.auth.User
import com.avant.util.IDGenerator
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef

open class AccessToken(@Id var accessToken: String = IDGenerator.longId(),
                       @DBRef @JsonIgnore var refreshToken: RefreshToken,
                       @DBRef @JsonIgnore var user: User) {
	
	constructor(refreshToken: RefreshToken) : this(refreshToken = refreshToken, user = refreshToken.user)
	
	val parentRefreshToken: String
		get() = refreshToken.refreshToken
	val userId: String
		get() = user.id
	
	var timeout: Long = System.currentTimeMillis() + defaultTimeout
	
	val isValid: Boolean
		get() = System.currentTimeMillis() < timeout
	val isExpired: Boolean
		get() = !isValid
	
	fun updateTimeout() = also {
		this.timeout = System.currentTimeMillis() + defaultTimeout
	}
	
	companion object {
		var defaultTimeout: Long = 3600 * 1000;
	}
}
