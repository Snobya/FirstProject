package com.avant.entity

import com.avant.auth.User
import com.avant.model.DefaultData
import com.avant.util.findOne
import com.avant.util.toMillis
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import java.time.*
import java.util.*
import kotlin.collections.HashMap

data class Event(@Id var id: String = UUID.randomUUID().toString(),
                 var title: String) {
	
	@DBRef
	var curator: User? = null
	var info: String = "Here is nothing here yet."
	var headImg = mutableListOf<String>()
	var content = mutableListOf<Pair<String, String>>()
	var photos = mutableListOf<String>() // photos id's
	var closestEvent: ZonedDateTime = ZonedDateTime.of(LocalDate.of(2030, 1, 1), LocalTime.now(), ZoneId.of("+2"))
	var dates = mutableListOf<EventDate>()
	val datelist: List<ZonedDateTime>
		get() = dates.map { it.startDate }
	
	
	fun addDate(date: EventDate) {
		dates.add(date)
		findClosestDate()
	}
	
	fun getDate(id: String): EventDate = dates.findOne { it.id == id }
			?: throw IllegalArgumentException("Wrong date id")
	
	fun findClosestDate(): ZonedDateTime {
		closestEvent = this.datelist.sorted().first()
		return closestEvent
	}
	
	// frontend:
	val timeStampList: List<Long>
		get() = dates.map { it.startDateTimeStamp }
	
	class EventDate(var id: String = UUID.randomUUID().toString().substring(0..7),
	                var startDate: ZonedDateTime, var endDate: ZonedDateTime) {
		
		var offers = ArrayList<EventOffer>()
		var hasFreePlaces = false
		
		// frontend:
		val startDateTimeStamp: Long
			get() = startDate.toInstant().toEpochMilli()
		val endDateTimeStamp: Long
			get() = startDate.toInstant().toEpochMilli()
		
		fun getDepositPrice(name: String, type: String): Double =
			offers.findOne { it.name == name }?.run {
				this.offerTypes.findOne { it.type == type }?.deposit
			} ?: throw IllegalArgumentException("Such request not found")
		
		fun getFullPrice(name: String, type: String): Double =
			offers.findOne { it.name == name }?.run {
				this.offerTypes.findOne { it.type == type }?.price
			} ?: throw IllegalArgumentException("Such request not found")
		
		fun addOffer(eventOffer: EventOffer) {
			offers.removeIf { it.name == eventOffer.name }
			offers.add(eventOffer)
		}
	}
	
	/**
	 * Event offer class.
	 * Name - is type of accomodation (deluxe, economy, etc.)
	 * price map is: student - 2500, teacher - 3500
	 * prices contains all the prices in map: key-value
	 */
	data class EventOffer(var name: String, var currency: String = "UAH",
	                      var offerTypes: MutableList<EventOfferType>) {
		
		constructor(name: String, deposits: Map<String, Double>, prices: Map<String, Double>,
		            currency: String) : this(name = name, currency = currency, offerTypes = mutableListOf()) {
			prices.forEach { type, value ->
				offerTypes.add(EventOfferType(type = type,
						price = value,
						deposit = deposits[type] ?: value))
			}
		}
		
	}
	
	//	var deposits: MutableMap<String, Double>,
	//	var prices: MutableMap<String, Double>
	
	data class EventOfferType(var type: String, var price: Double, var deposit: Double = price)
	
}


