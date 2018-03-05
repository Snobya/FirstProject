package com.avant.model

import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class DefaultData {
	
	@PostConstruct
	fun postConstruct() {
		instance = this
	}
	
	/** Will return default image. In future will be configured. */
	fun defaultImage(): String {
		return "/someDefaultImage.jpg"
	}
	
	companion object {
		lateinit var instance: DefaultData
	}
	
}