package com.avant.model

import com.avant.entity.Event
import com.avant.repo.EventRepository
import com.avant.util.print
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class EventModelTest {
	
	@Autowired
	private lateinit var eventModel: EventModel
	@Autowired
	private lateinit var eventRepo: EventRepository
	
	private val testId = "test-event-id"
	
	@BeforeAll
	fun createIfNotExist() {
		var event: Event
		try {
			event = eventModel.getEvent(testId)
		} catch (e: Exception) {
			event = eventModel.create("Test event", "Test info", "test-pic.jpg")
			eventRepo.deleteById(event.id)
			event.id = testId
			eventRepo.save(event)
		}
		assertEquals(event.id, testId)
	}
	
	@Test
	fun getEvent() {
		assertNotNull(eventModel.getEvent(testId))
	}
	
	@Test
	fun closest() {
		assertNotEquals(eventModel.closest(1).also { it.forEach { it.id.print() } }.size, 0)
	}
	
	@Test
	fun between() {
		assertNotEquals(eventModel.between(LocalDateTime.now(), LocalDateTime.now().plusYears(30)).size, 0)
	}
	
	@Test
	fun edit() {
		val testDescription = "Info is now: " + UUID.randomUUID().toString().slice(0..8)
		eventModel.edit(eventId = testId, info = testDescription)
		assertEquals(eventModel.getEvent("test-event-id").info, testDescription)
	}
	
	@Test
	fun createAndDelete() {
		val id = eventModel.create("Test event #2", "Gotta be deleted", "something.jpg").id
		eventModel.deleteEvent(id)
		assertThrows(Exception::class.java, { eventModel.getEvent(id) })
	}
	
	@Test
	fun addContent() {
	}
	
	@Test
	fun removeContent() {
	}
	
	@Test
	fun addPhotos() {
	}
	
	@Test
	fun addEventDate() {
	}
	
	@Test
	fun addEventOffer() {
	}
	
	
}