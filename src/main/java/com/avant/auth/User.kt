package com.avant.auth

import com.avant.entity.Saveable
import com.avant.util.IDGenerator
import org.springframework.data.annotation.Id

class User(private val mail: String) : Saveable {
	
	@Id
	override var id: String = IDGenerator.longId()
	var name: String? = null
	var photo: String? = null
	var phone: String? = null
	
}
