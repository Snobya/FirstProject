package com.avant.repo

import com.avant.entity.Event
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface EventRepository : MongoRepository<Event, String> {
	
	fun findByRankGreaterThan(rank: Int, pageable: Pageable): List<Event>
	
}