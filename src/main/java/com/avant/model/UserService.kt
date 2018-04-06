package com.avant.model

import com.avant.auth.AuthController
import com.avant.auth.AuthReasonController
import com.avant.auth.User
import com.avant.repo.MongoEntityInformationCreator
import com.avant.util.Locks
import org.springframework.stereotype.Service

@Service
class UserService(
		mongoEntityInformationCreator: MongoEntityInformationCreator,
		val authController: AuthController,
		val authReasonController: AuthReasonController) {
	
	val userRepo = mongoEntityInformationCreator.getSimpleMongoRepository(User::class.java)
	
	fun register(login: String, pass: String) = authReasonController.register(login, pass)
	
	fun setData(id: String, name: String?, photo: String?, phone: String?) = updateUser(id) {
		name?.apply { it.name = this }
		photo?.apply { it.photo = this }
		phone?.apply { it.phone = this }
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