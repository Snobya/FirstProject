package com.avant.model

import com.avant.auth.AuthController
import com.avant.entity.Event
import com.avant.repo.EventRepo
import com.avant.util.Locks
import com.avant.util.findOne
import kotlinx.coroutines.experimental.launch
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.io.FileNotFoundException
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Service
class EventModel(
		val eventRepo: EventRepo,
		val gSheetsModelProxy: GSheetsModelProxy,
		val authController: AuthController) {
	
	fun ping() = "ping"
	
	fun closest(count: Int): List<Event> = eventRepo.findAll(PageRequest.of(0, count, Sort.Direction.ASC, "closestEvent")).toList()
	
	fun between(firstDay: LocalDateTime, lastDay: LocalDateTime): List<Event> =
		eventRepo.findByClosestEventBetween(firstDay, lastDay)
	
	fun all() = eventRepo.findAll()
	
	fun list(page: Int, pageSize: Int) = eventRepo.findAll(PageRequest.of(page, pageSize))
	
	fun create(title: String, info: String, image: Array<String>) =
		eventRepo.save(Event(title = title).apply {
			this.info = info
			this.headImg = image.toMutableList()
		}).also {
			launch { gSheetsModelProxy.onEventCreate(it) }
		}
	
	fun edit(eventId: String, title: String? = null, info: String? = null, image: Array<String>? = null) = updateEvent(eventId) {
		title?.apply { it.title = this }
		info?.apply { it.info = this }
		image?.apply { it.headImg = this.toMutableList() }
	}
	
	fun setCurator(eventId: String, userId: String) = updateEvent(eventId) {
		it.curator = authController.getUserById(userId)
	}
	
	fun deleteEvent(eventId: String) = Locks.withBlock(eventId) {
		eventRepo.deleteById(eventId)
	}
	
	fun addContent(eventId: String, key: String, value: String) = updateEvent(eventId) {
		it.content.removeIf { it.first == key }
		it.content.add(key to value)
	}
	
	fun removeContent(eventId: String, key: String) = updateEvent(eventId) {
		it.content.removeIf { it.first == key }
	}
	
	fun setContent(eventId: String, content: Map<String, String>) = updateEvent(eventId) {
		it.content.clear()
		content.forEach { key, value -> it.content.add(key to value) }
	}
	
	fun addPhotos(eventId: String, vararg photos: String) = updateEvent(eventId) {
		photos.forEach { photo -> it.photos.add(photo) }
	}
	
	fun setPhotos(eventId: String, vararg photos: String) = updateEvent(eventId) {
		it.photos = photos.toMutableList()
	}
	
	fun removePhotos(eventId: String, vararg photos: String) = updateEvent(eventId) {
		it.photos.removeAll(photos)
	}
	
	fun addEventDate(eventId: String, startDate: ZonedDateTime, endDate: ZonedDateTime) = updateEvent(eventId) {
		it.addDate(Event.EventDate(startDate = startDate, endDate = endDate))
		launch {
			gSheetsModelProxy.onEventUpdate(it)
		}
	}
	
	fun editEventDate(eventId: String, dateId: String, startDate: ZonedDateTime? = null, endDate: ZonedDateTime? = null) = updateEvent(eventId) {
		val date = (it.dates.findOne { it.id == dateId } ?: throw FileNotFoundException("DateId not found"))
		startDate?.apply { date.startDate = this }
		endDate?.apply { date.endDate = this }
		it.findClosestDate()
		launch {
			gSheetsModelProxy.onEventUpdate(it)
		}
	}
	
	fun addEventOffer(eventId: String, dateId: String, name: String, deposits: Map<String, Double>,
	                  prices: Map<String, Double>, currency: String?) = updateEvent(eventId) {
		val date = it.dates.findOne { it.id == dateId } ?: throw FileNotFoundException("No such date found")
		date.addOffer(Event.EventOffer(name = name, deposits = deposits.toMutableMap(), prices = prices.toMutableMap(),
				currency = currency ?: "UAH"))
	}
	
	fun removeEventOffer(eventId: String, dateId: String, name: String) = updateEvent(eventId) {
		val date = it.dates.findOne { it.id == dateId } ?: throw FileNotFoundException("No such date found")
		date.offers.removeIf { it.name == name }
	}
	
	fun getEventOffers(eventId: String, dateId: String) = getEvent(eventId).dates.find { it.id == dateId }
			?: throw FileNotFoundException("No such date id")
	
	fun setFreeSeats(eventId: String, dateId: String, hasFreePlaces: Boolean) = updateEvent(eventId) {
		it.getDate(dateId).hasFreePlaces = hasFreePlaces
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