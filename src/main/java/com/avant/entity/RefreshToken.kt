package com.avant.entity

import com.avant.auth.User
import com.avant.util.IDGenerator
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef

open class RefreshToken(@Id override var id: String = IDGenerator.longId(),
                        @DBRef @JsonIgnore var user: User) : Saveable {
	
	
	// for JSON mapping
	val userId: String
		get() = user.id
	
	val refreshToken: String
		get() = id
}
