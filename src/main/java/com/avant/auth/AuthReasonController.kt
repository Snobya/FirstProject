package com.avant.auth

import com.avant.entity.AuthReason
import com.avant.repo.MongoEntityInformationCreator
import com.avant.repo.UserRepo
import com.google.common.hash.Hashing
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service

@Service
class AuthReasonController(
		mongoEntityInformationCreator: MongoEntityInformationCreator,
		val mongoTemplate: MongoTemplate,
		val userRepo: UserRepo) {
	
	val reasonRepo = mongoEntityInformationCreator.getSimpleMongoRepository(AuthReason::class.java)
	
	fun register(login: String, pass: String): AuthReason {
		if (userRepo.findByMail(login).isPresent) {
			throw IllegalStateException("User with this mail already exists!")
		}
		val user = userRepo.save(User(login))
		return reasonRepo.save(AuthReason(id = login, authSource = AuthSource.MAIL_PASS,
				authData = pass, mail = login, user = user))
	}
	
	fun login(login: String, pass: String): AuthReason {
		val reason = reasonRepo.findById(login).orElseThrow { IllegalAccessError("Wrong login.") }
		if (reason.authData != pass) {
			throw IllegalAccessError("Wrong pass.")
		}
		return reason
	}
}