package com.avant.model

import com.avant.auth.User
import com.avant.util.int
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserModelTest {
	
	@Autowired
	private lateinit var userService: UserService
	@Autowired
	private lateinit var mongoTemplate: MongoTemplate
	
	private var userId: String = ""
	private val mail = "test-admin${Random().int(0..1000)}@corp.com"
	
	@BeforeAll
	fun createUser() {
		userService.register(mail, "123456")
		userId = mongoTemplate
			.find(Query(Criteria("mail").`is`(mail)), User::class.java)
			.find { true }?.id ?: throw IllegalArgumentException("What? Where is ID?")
	}
	
	@Test
	fun testEdit() {
		userService.
	}
	
	@AfterAll
	fun deleteUser() {
		assertNotEquals(0, mongoTemplate.remove(Query(Criteria("mail").`is`(mail)), User::class.java)
			.deletedCount)
	}
	
	
}