package com.avant.entity

import com.avant.model.DefaultData
import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap

data class Event(@Id var id: String = UUID.randomUUID().toString(), var title: String) {
	
	var info = HashMap<String, String>()
	var headImg: String = DefaultData.instance.defaultImage()
	
	class EventDate(var startDate: LocalDateTime, var endDate: LocalDateTime) {
		
		var offers = ArrayList<EventOffer>()
		
	}
	
	/**
	 * Event offer class. prices contains all the prices in map: key-value
	 */
	data class EventOffer(var name: String, var prices: MutableMap<String, Double>)
	
}