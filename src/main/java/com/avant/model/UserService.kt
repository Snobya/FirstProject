package com.avant.model

import com.avant.auth.*
import com.avant.entity.AuthReason
import com.avant.repo.MongoEntityInformationCreator
import com.avant.util.Locks
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

@Service
class UserService(
		mongoEntityInformationCreator: MongoEntityInformationCreator,
		val authController: AuthController,
		val authReasonController: AuthReasonController,
		val mongoTemplate: MongoTemplate) {
	
	val userRepo = mongoEntityInformationCreator.getSimpleMongoRepository(User::class.java)
	
	fun register(login: String, pass: String): AuthReason {
		return authReasonController.register(login, pass)
	}
	
	fun setData(id: String, name: String?, photo: String?, phone: String?) = updateUser(id) {
		name?.apply { it.name = this }
		photo?.apply { it.photo = this }
		phone?.apply { it.phone = this }
	}
	
	fun curatorList(): List<User> =
		mongoTemplate.find(Query.query(Criteria("isCurator").`is`(true)), User::class.java)
	
	fun setCurator(id: String, curator: Boolean?) = updateUser(id) {
		if (it.name == null && curator != false) {
			throw IllegalArgumentException("Name is not set.")
		}
		it.isCurator = curator ?: !it.isCurator
	}
	
	private fun updateUser(id: String, action: (User) -> Unit): User {
		val user: User = authController.getUserById(id)
		Locks.withBlock(id) {
			action(user)
			userRepo.save(user)
		}
		return user
	}
}