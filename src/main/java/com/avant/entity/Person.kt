package com.avant.entity

import java.time.LocalDate
import java.time.Month

class Person(var name: String, var phone: String, var offerName: String, var offerType: String) {
	
	constructor(name: String, phone: String, document: String, offerName: String, offerType: String, birthday: LocalDate)
			: this(name, phone, offerName, offerType) {
		this.document = document
		this.birthday = birthday
	}
	
	var document: String? = null
	var birthday: LocalDate = LocalDate.of(1970, Month.JANUARY, 1)
}