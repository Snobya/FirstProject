package com.avant.model

import com.avant.entity.ConfigPair
import com.avant.repo.MongoEntityInformationCreator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * TODO Put this to a special library
 */
@Service
class ConfigService (mongoEntityInformationCreator: MongoEntityInformationCreator) {
	
	init {
		ConfigPair.configServiceInstance = this
	}
	
	private val repo = mongoEntityInformationCreator.getSimpleMongoRepository(ConfigPair::class.java)
	
	fun getConfig(name: String): ConfigPair = repo.findById(name).orElseGet {
		repo.save(ConfigPair(name))
	}
	
	fun addParam(name: String, param: String, value: String) = getConfig(name).apply { this.params[param] = value }.save()
	
	fun getParam(name: String, param: String): String? = getConfig(name).params[param]
	
	fun getParam(name: String, param: String, defaultValue: String): String =
		getConfig(name).params[param] ?: defaultValue.apply { addParam(name, param, this) }
	
	fun save(config: ConfigPair): ConfigPair = repo.save(config)
	
	
}