package com.avant.entity

import org.springframework.data.annotation.Id
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.util.*

class Order(@Id var id: String = UUID.randomUUID().toString(), var event: Event, dateId: String) {
	
	var mail: String? = null
	var comment: String? = null
	
	var dateInfo: Event.EventDate = event.getDate(dateId)
	var orderDeposit = 0.0
	
	
	class Person(var name: String, var phone: String, var offerName: String, var offerType: String) {
		var document: String? = null
		var birthday: LocalDate = LocalDate.of(1970, Month.JANUARY, 1)
		
		
	}
	
}