package com.avant.auth

import com.avant.entity.AuthReason
import com.avant.repo.MongoEntityInformationCreator
import com.google.common.hash.Hashing
import org.springframework.stereotype.Service

@Service
class AuthReasonController(
		mongoEntityInformationCreator: MongoEntityInformationCreator) {
	
	val userRepo = mongoEntityInformationCreator.getSimpleMongoRepository(User::class.java)
	val reasonRepo = mongoEntityInformationCreator.getSimpleMongoRepository(AuthReason::class.java)
	
	fun register(login: String, pass: String): AuthReason {
		val user = userRepo.save(User(login))
		return reasonRepo.save(AuthReason(id = login, authData = pass, mail = login, user = user))
	}
	
	fun login(login: String, pass: String): AuthReason {
		val reason = reasonRepo.findById(login).orElseThrow { IllegalAccessError("Wrong login.") }
		if (reason.authData != pass) {
			throw IllegalAccessError("Wrong pass.")
		}
		return reason
	}
}