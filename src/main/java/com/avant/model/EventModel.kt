package com.avant.model

import com.avant.entity.Event
import com.avant.repo.EventRepository
import com.avant.util.Locks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.io.FileNotFoundException
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class EventModel {

	@Autowired
	private lateinit var eventRepo: EventRepository

	fun closest(count: Int): List<Event> = eventRepo.findAll().take(count)

	fun between(firstDay: LocalDateTime, lastDay: LocalDateTime): List<Event> =
			eventRepo.findByClosestDateBetween(firstDay, lastDay)

	fun create(title: String, info: String, image: String) =
			eventRepo.save(Event(title = title).apply {
				this.info = info
				this.headImg = image
			})

	fun edit(eventId: String, title: String?, info: String?, image: String?) = updateEvent(eventId) {
		title?.apply { it.title = this }
		info?.apply { it.info = this }
		image?.apply { it.info = this }
	}


	fun addContent(eventId: String, key: String, value: String) = updateEvent(eventId) {
		it.content.add(key to value)
	}

	fun removeContent(eventId: String, key: String) = updateEvent(eventId) {
		it.content.removeIf { it.first == key }
	}


	fun addPhotos(eventId: String, vararg photos: String) = updateEvent(eventId) {
		photos.forEach { photo -> it.photos.add(photo) }
	}


	fun addEventDate(eventId: String, startDate: LocalDateTime, endDate: LocalDateTime) = updateEvent(eventId) {
		it.addDate(Event.EventDate(startDate = startDate, endDate = endDate))
	}


	fun addEventOffer(eventId: String, dateId: String, name: String, prices: Map<String, Double>) = updateEvent(eventId) {
		val date = it.dates.stream().filter { it.id == dateId }.findAny().orElseThrow { FileNotFoundException("No such date found") }
		date.offers.add(Event.EventOffer(name, prices = prices.toMutableMap()))
	}


	private fun updateEvent(eventId: String, function: (Event) -> Unit): Event {
		var event: Event? = null
		Locks.withBlock(eventId) {
			event = getEvent(eventId)
			function.invoke(event!!)
			eventRepo.save(event!!)
		}
		return event ?: throw IllegalStateException("UpdateEvent must return event, null recieved instead")
	}

	fun getEvent(eventId: String): Event = eventRepo.findById(eventId).orElseThrow { FileNotFoundException("Event doesn't exist") }

}