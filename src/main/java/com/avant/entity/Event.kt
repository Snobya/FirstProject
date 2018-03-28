package com.avant.entity

import com.avant.model.DefaultData
import com.avant.util.findOne
import com.avant.util.toMillis
import org.springframework.data.annotation.Id
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.HashMap

data class Event(@Id var id: String = UUID.randomUUID().toString(),
                 var title: String) {
	
	var info: String = "Here is nothing here yet."
	var headImg: String = DefaultData.instance.defaultImage()
	var content = mutableListOf<Pair<String, String>>()
	var photos = mutableListOf<String>() // photos id's
	var closestEvent: LocalDateTime = LocalDateTime.of(LocalDate.of(2030, 1, 1), LocalTime.now())
	var dates = mutableListOf<EventDate>()
	val datelist: List<LocalDateTime>
		get() = dates.map { it.startDate }
	
	
	fun addDate(date: EventDate) {
		dates.add(date)
		findClosestDate()
	}
	
	fun getDate(id: String): EventDate = dates.findOne { it.id == id }
			?: throw IllegalArgumentException("Wrong date id")
	
	fun findClosestDate(): LocalDateTime {
		closestEvent = this.datelist.sorted().first()
		return closestEvent
	}
	
	// frontend:
	val timeStampList: List<Long>
		get() = dates.map { it.startDateTimeStamp }
	
	class EventDate(var id: String = UUID.randomUUID().toString().substring(0..7),
	                var startDate: LocalDateTime, var endDate: LocalDateTime) {
		
		var offers = ArrayList<EventOffer>()
		
		// frontend:
		val startDateTimeStamp = startDate.toMillis()
		val endDateTimeStamp = startDate.toMillis()
		
		fun getDepositPrice(name: String, type: String): Double =
			offers.findOne { it.name == name }?.run { this.prices[type] }
					?: throw IllegalArgumentException("Such request not found")
	}
	
	/**
	 * Event offer class.
	 * Name - is type of accomodation (deluxe, economy, etc.)
	 * price map is: student - 2500, teacher - 3500
	 * prices contains all the prices in map: key-value
	 */
	data class EventOffer(var currency: String = "UAH", var name: String, var prices: MutableMap<String, Double>)
	
}


