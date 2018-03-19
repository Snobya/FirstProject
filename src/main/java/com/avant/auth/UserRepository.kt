package com.avant.auth

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface UserRepository : MongoRepository<User, String> {
	
	@Query("{'mail' : ?0, 'pass' : ?1}")
	fun findByCredAndPass(cred: String, pass: String): User
	
}
