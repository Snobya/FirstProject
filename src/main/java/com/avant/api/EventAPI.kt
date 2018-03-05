package com.avant.api

import com.avant.entity.Event
import com.avant.model.EventModel
import com.avant.repo.EventRepository
import com.avant.util.Ret
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/event")
class EventAPI {
	
	@Autowired
	private lateinit var eventRepo: EventRepository
	@Autowired
	private lateinit var eventModel: EventModel
	
	@GetMapping("/closest")
	fun closest(@RequestParam count: Int): String {
		return "Something"
	}
	
	/**
	 * Returns events by month
	 * @param month year-month, like 2017-04 or 2018-12
	 */
	@GetMapping("/monthly")
	fun monthly(@RequestParam month: String): ResponseEntity<String> {
		return if (month.length > 5) {
			ResponseEntity.ok("Yeah")
		} else {
			ResponseEntity.status(403).body("Not OK")
		}
	}
	
	/**
	 * Finds events starts between two dates.
	 * @param beforeDate date, like 2017-03-21
	 * @param afterDate date as a beforeDate, and after beforeDate
	 */
	@GetMapping("/between")
	fun between(@RequestParam beforeDate: String, @RequestParam afterDate: String): ResponseEntity<*> {
		return Ret.ok("TODO")
	}
	
	/**
	 * Create event with no dates
	 */
	@PostMapping("/create")
	fun createEvent(): ResponseEntity<*> {
		return Ret.code(500, "Not implemented")
	}
	
	@PostMapping("/date")
	fun addDate(): ResponseEntity<*> {
		return Ret.code(500, "Not implemented")
	}
	
	/**
	 * Sets info in event.
	 * @param eventId is event's id
	 * @param key is title of name
	 * @param value is html-markup-content
	 */
	@PostMapping("/info")
	fun setInfo(@RequestParam eventId: String, @RequestParam key: String, @RequestParam value: String): ResponseEntity<*> {
		return Ret.code(500, "Not implemented")
	}
	
	/**
	 * Deletes info in event
	 * @param key is key to be deleted
	 */
	@PostMapping("/info/delete")
	fun deleteInfo(@RequestParam eventId: String, @RequestParam key: String): ResponseEntity<*> {
		return Ret.code(500, "Not implemented")
	}
	
	//	@PostMapping("/postSomething")
	//	fun postSomething(@RequestParam text: String, @RequestParam other: String): ResponseEntity<*> {
	//		return ResponseEntity.ok(text + other)
	//	}
	//
	//	@PostMapping("/generate")
	//	fun generateEvent(@RequestParam(required = false) name: String?,
	//	                  @RequestParam(required = false) startDateString: String?,
	//	                  @RequestParam(required = false) endDateString: String?): ResponseEntity<*> {
	//		return ResponseEntity.ok(eventRepo.save(Event(title = name
	//				?: UUID.randomUUID().toString().substring(0..7)).also {
	//			if (startDateString != null) {
	//				it.startDate = LocalDateTime.parse(startDateString)
	//			}
	//			if (endDateString != null) {
	//				it.endDate = LocalDateTime.parse(endDateString)
	//			}
	//		}))
	//	}
	//
	//	@GetMapping("/rank")
	//	fun getEvent(@RequestParam(defaultValue = "50") rank: Int,
	//	             @RequestParam(defaultValue = "0") page: Int) =
	//		eventRepo.findByRankGreaterThan(rank, PageRequest.of(page, 5))
	//
	
}