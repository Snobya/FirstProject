package com.avant.entity

import com.avant.model.ConfigService
import com.avant.util.IDGenerator

data class BlogPost(override var id: String = IDGenerator.shortId(), var title: String, var content: String,
                    var posted: Long = System.currentTimeMillis()) : Saveable

data class NewsPost(override var id: String = IDGenerator.shortId(), var title: String, var content: String,
                    var posted: Long = System.currentTimeMillis()) : Saveable

data class ConfigPair(override var id: String, var params: MutableMap<String, String> = mutableMapOf()) : Saveable {
	
	fun save() {
		configServiceInstance.save(this)
	}
	
	companion object {
		lateinit var configServiceInstance: ConfigService
	}
	
}