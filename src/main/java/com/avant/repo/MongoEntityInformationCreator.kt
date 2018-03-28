package com.avant.repo

import com.avant.entity.Saveable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository
import org.springframework.stereotype.Service

@Service
class MongoEntityInformationCreator @Autowired
constructor(private val mongoOperations: MongoOperations) {
	
	fun <T : Saveable> getEntityInfo(entity: T) = getEntityInfo(entity::class.java)
	
	fun <T : Saveable> getEntityInfo(clazz: Class<T>): MongoEntityInformation<T, String> {
		return object : MongoEntityInformation<T, String> {
			override fun getJavaType(): Class<T> = clazz
			override fun isNew(entity: T): Boolean = mongoOperations.findById(entity.id, entity.javaClass) == null
			override fun getId(entity: T): String = entity.id
			override fun getIdType(): Class<String> = String::class.java
			override fun getIdAttribute(): String = "id"
			
			override fun getCollectionName(): String {
				val char0 = Character.toLowerCase(clazz.simpleName[0])
				return (char0 + clazz.simpleName.substring(1))
			}
		}
	}
	
	fun <T : Saveable> getSimpleMongoRepository(clazz: Class<T>): SimpleMongoRepository<T, String> {
		return SimpleMongoRepository(getEntityInfo(clazz), mongoOperations)
	}
	
}
