package com.avant.auth

import org.springframework.data.annotation.Id

import java.util.UUID

class User(private val mail: String, private var pass: String) : Cloneable {
	@Id
	private val id: String = UUID.randomUUID().toString()
	private val name: String? = null
	private val photo: String? = null
	private val phone: String? = null
	
	fun wipePrivateData(): User? {
		try {
			val clone = this.clone() as User
			clone.pass = ""
			return clone
		} catch (e: CloneNotSupportedException) {
			e.printStackTrace()
		}
		
		return null
	}
	
	
}
