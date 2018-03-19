package com.avant.repo

import com.avant.entity.Event
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDateTime

interface EventRepository : MongoRepository<Event, String> {

	fun findByClosestEventBetween(start: LocalDateTime, end: LocalDateTime): List<Event>

}