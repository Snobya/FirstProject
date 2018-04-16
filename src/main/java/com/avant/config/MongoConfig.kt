package com.avant.config

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import java.time.*
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import java.util.*


@Configuration
class MongoConfig : AbstractMongoConfiguration() {
	
	private val client: MongoClient = MongoClient(
			MongoClientURI("mongodb://root:root@ds245238.mlab.com:45238/avant-2")
	)
	
	private val dbName = "avant-2"
	
	override fun mongoClient(): MongoClient = client
	override fun getDatabaseName(): String = dbName
	
	fun getMongoOperations() = MongoTemplate(mongoClient(), databaseName)
	
	@Bean
	override fun customConversions(): MongoCustomConversions {
		val converters = ArrayList<Converter<*, *>>()
		converters.add(DateToZonedDateTimeConverter())
		converters.add(ZonedDateTimeToDateConverter())
		return MongoCustomConversions(converters)
	}
	
	@Bean
	override fun mongoTemplate(): MongoTemplate {
		val converter = MappingMongoConverter(
				DefaultDbRefResolver(mongoDbFactory()), MongoMappingContext())
		converter.setCustomConversions(customConversions())
		converter.afterPropertiesSet()
		return MongoTemplate(mongoDbFactory(), converter)
	}
	
}


class DateToZonedDateTimeConverter : Converter<Date, ZonedDateTime> {
	
	override fun convert(source: Date?): ZonedDateTime? {
		return if (source == null) null else {
			ZonedDateTime.ofInstant(source.toInstant(), ZoneId.of("+2"))
		}
	}
}

class ZonedDateTimeToDateConverter : Converter<ZonedDateTime, Date> {
	
	override fun convert(source: ZonedDateTime?): Date? {
		return if (source == null) null else Date.from(source.toInstant())
	}
}