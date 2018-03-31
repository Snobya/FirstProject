package com.avant.auth

import com.avant.entity.*
import com.avant.repo.MongoEntityInformationCreator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level
import java.util.logging.Logger


@Controller
class AuthController(
		mongoEntityInformationCreator: MongoEntityInformationCreator,
		val authReasonController: AuthReasonController) {
	
	val userRepo = mongoEntityInformationCreator.getSimpleMongoRepository(User::class.java)
	val tokenRepo = mongoEntityInformationCreator.getSimpleMongoRepository(RefreshToken::class.java)
	
	private val activeTokens = ConcurrentHashMap<String, AccessToken>()
	private val activeUsers = ConcurrentHashMap<String, User>()
	
	private val logger = Logger.getLogger("AuthController")
	
	init {
		logger.log(Level.INFO, "Auth loaded.")
	}
	
	@Scheduled(fixedDelay = 60_000)
	fun checkTokens() {
		activeTokens.values.stream().filter { it.isExpired }
			.forEach { token -> activeTokens.remove(token.accessToken) }
	}
	
	fun login(login: String, pass: String): RefreshToken {
		return login(authReasonController.login(login, pass))
	}
	
	fun login(reason: AuthReason): RefreshToken {
		val token = RefreshToken(user = reason.user)
		return tokenRepo.insert(token)
	}
	
	fun createAccessToken(refreshToken: RefreshToken) = AccessToken(refreshToken).also { activeTokens[it.accessToken] = it }
	
	fun createAccessToken(refreshToken: String) = createAccessToken(
			tokenRepo.findById(refreshToken)
				.orElseThrow { IllegalArgumentException("Wrong refresh token") })
	
	fun getUser(accessToken: String): User =
		(activeTokens[accessToken] ?: throw NotLoggedInException("Access token wrong or expired")).user
	
	fun getUserOrNull(id: String): User? = activeUsers[id]
	
	fun renewToken(accessToken: String): Boolean = activeTokens[accessToken]?.updateTimeout() != null
	
	fun logout(token: String): Boolean {
		fun deleteAccessToken(token: String): Boolean {
			activeTokens[token]?.also {
				activeTokens.remove(token).also {
					activeUsers.remove(it?.userId)
				}
				tokenRepo.deleteById(it.parentRefreshToken)
			} ?: return false
			return true
		}
		
		fun deleteRefreshToken(token: String): Boolean {
			var ret = false
			activeTokens.keys.forEach {
				if (activeTokens[it]?.parentRefreshToken == token) {
					deleteAccessToken(it)
					ret = true
				}
			}
			return ret
		}
		
		return if (deleteAccessToken(token))
			true
		else deleteRefreshToken(token)
	}
	
}
