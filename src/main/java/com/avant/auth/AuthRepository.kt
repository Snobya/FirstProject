package com.avant.auth

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface AuthRepository : MongoRepository<AuthToken, String> {
	
	@Query("{'timeout' : {\$lt : ?0}}")
	fun findTimeouted(now: Long?): List<AuthToken>
	
}
