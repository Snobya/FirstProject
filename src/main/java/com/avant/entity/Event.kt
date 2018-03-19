package com.avant.entity

import com.avant.model.DefaultData
import org.springframework.data.annotation.Id
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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
		if (date.startDate.isBefore(closestEvent)) {
			this.closestEvent = date.startDate
		}
	}
	
	class EventDate(var id: String = UUID.randomUUID().toString().substring(0..7),
	                var startDate: LocalDateTime, var endDate: LocalDateTime) {
		
		var offers = ArrayList<EventOffer>()
		
	}
	
	/**
	 * Event offer class.
	 * prices contains all the prices in map: key-value
	 */
	data class EventOffer(var name: String, var prices: MutableMap<String, Double>)
	
}