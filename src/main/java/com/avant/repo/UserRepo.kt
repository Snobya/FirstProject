package com.avant.repo

import com.avant.auth.User
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface UserRepo : MongoRepository<User, String> {
	
	fun findByMail(mail: String): Optional<User>
	
}