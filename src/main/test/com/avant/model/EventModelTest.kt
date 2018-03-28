package com.avant.model

import com.avant.entity.Event
import com.avant.repo.EventRepo
import com.avant.util.isAnyOf
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
	private lateinit var eventRepo: EventRepo
	
	private val testId = "test-event-id"
	
	@BeforeAll
	fun setup() {
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
		assertNotEquals(eventModel.between(LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(30)).size, 0)
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
		val title = "test-content"
		val value = UUID.randomUUID().toString()
		eventModel.addContent(testId, title, value)
		assertEquals(eventModel.getEvent(testId).content.filter { it.first == title }[0].second, value)
	}
	
	@Test
	fun removeContent() {
		addContent()
		eventModel.removeContent(eventId = testId, key = "test-content")
		assertEquals(eventModel.getEvent(testId).content.filter { it.first == "test-content" }.size, 0)
	}
	
	@Test
	fun addPhotos() {
		val event = eventModel.getEvent(testId)
		eventModel.addPhotos(testId, "p1", "p2", "p3")
		assertEquals(event.photos.size + 3, eventModel.getEvent(testId).photos.size)
	}
	
	@Test
	fun removePhotos() {
		eventModel.removePhotos(testId, "p1", "p2", "p3")
		assertEquals(0, eventModel.getEvent(testId).photos.filter { it.isAnyOf("p1", "p2", "p3") }.size)
	}
	
	@Test
	fun addEventDate() {
		val date = LocalDateTime.now().plusDays(1)
		eventModel.addEventDate(testId, date,
				date.plusHours(20)).datelist
		assertTrue(eventModel.getEvent(testId).datelist.contains(date))
	}
	
	@Test
	fun addEventOffer() {
		val date = eventModel.getEvent(testId).dates[0]
		val currency = "UAH"
		eventModel.addEventOffer(testId, date.id, "Deluxe", mapOf("Студент" to 5500.0, "Обычный" to 8000.0), currency)
		eventModel.addEventOffer(testId, date.id, "Semi-Luxe", mapOf("Студент" to 3500.0, "Обычный" to 6000.0), currency)
		eventModel.addEventOffer(testId, date.id, "Economy", mapOf("Студент" to 2200.0, "Обычный" to 3000.0), currency)
		assertEquals(eventModel.getEventOffers(testId, date.id).offers.size, date.offers.size + 3)
		for (offer in eventModel.getEventOffers(testId, date.id).offers) {
			assertEquals(offer.prices.size, 2)
		}
	}
	
	
}