package com.avant.model

import com.avant.repo.EventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EventModel {
	
	@Autowired
	private lateinit var eventRepo: EventRepository
}