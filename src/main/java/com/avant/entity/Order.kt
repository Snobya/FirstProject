package com.avant.entity

import org.springframework.data.annotation.Id
import java.util.*

class Order(@Id var id: String = UUID.randomUUID().toString(), var event: Event, dateId: String) {
	
	var mail: String? = null
	var comment: String? = null
	
	var dateInfo: Event.EventDate = event.getDate(dateId)
	var orderDeposit = 0.0
	var currency = "UAH" // TODO Add currency here
	
	var persons = mutableListOf<Person>()
	
	fun addPerson(person: Person) = this.persons + person
	
	fun getDepositPrice(): Double = persons.map { dateInfo.getDepositPrice(it.offerName, it.offerType) }.sum()
	fun getFullPrice(): Double = persons.map { dateInfo.getFullPrice(it.offerName, it.offerType) }.sum()
	
	
}