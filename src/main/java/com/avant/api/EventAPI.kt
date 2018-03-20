package com.avant.api

import com.avant.model.EventModel
import com.avant.repo.EventRepository
import com.avant.util.Ret
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * API for events and their editing.
 */
@RestController
@RequestMapping("/api/event")
class EventAPI {
	
	@Autowired
	private lateinit var eventRepo: EventRepository
	@Autowired
	private lateinit var eventModel: EventModel
	
	/**
	 * Find {count} closest unique events
	 */
	@GetMapping("/closest")
	fun closest(@RequestParam count: Int): ResponseEntity<*> {
		return Ret.ok(eventModel.closest(count))
	}
	
	/**
	 * Returns events by month.
	 * Current version will return just the closest events, not everything. Meaning, if event starts in june and july,
	 * request with {month} 2018-07 will return empty result. This will be fixed someday.
	 * @param month year-month, like 2017-04 or 2018-12
	 */
	@GetMapping("/month")
	fun monthly(@RequestParam month: String): ResponseEntity<*> {
		val firstDay = LocalDate.parse(month + "-01")
		val lastDay = firstDay.withMonth(firstDay.lengthOfMonth())
		return Ret.ok(eventModel.between(firstDay.atStartOfDay(),
				lastDay.atTime(23, 59)))
	}
	
	/**
	 * Finds events starts between two dates.
	 * Bugs: as in /month
	 * @param beforeDate date, like 2017-03-21
	 * @param afterDate date as a beforeDate, and after beforeDate
	 * @see monthly
	 */
	@GetMapping("/between")
	fun between(@RequestParam beforeDate: String, @RequestParam afterDate: String): ResponseEntity<*> =
		Ret.ok(eventModel.between(LocalDate.parse(beforeDate).atStartOfDay(),
				LocalDate.parse(afterDate).atTime(23, 59)))
	
	/**
	 * Create event with no dates
	 * @param title title of event
	 * @param info is info of event
	 * @param img is photo ID, which can be obtained via FileAPI (WARNING: FileAPI is suspended sue to Amazon S3 issues)
	 */
	@PostMapping("/create")
	fun createEvent(@RequestParam title: String,
	                @RequestParam info: String,
	                @RequestParam img: String): ResponseEntity<*> {
		return Ret.ok(eventModel.create(title, info, img))
	}
	
	/**
	 * Edits event by passed {id}.
	 * Other params will be applied only if passed
	 */
	@PostMapping("/edit")
	fun editEvent(@RequestParam id: String,
	              @RequestParam(required = false) title: String?,
	              @RequestParam(required = false) info: String?,
	              @RequestParam(required = false) image: String?): ResponseEntity<*> {
		return Ret.ok(eventModel.edit(id, title, info, image))
	}
	
	/**
	 * Get the event content
	 */
	@GetMapping("/content")
	fun content(@RequestParam id: String): ResponseEntity<*> {
		return Ret.ok(eventModel.getEvent(id).content)
	}
	
	/**
	 * Add content to event by it's title and content
	 */
	@PostMapping("/content/add")
	fun contentAdd(@RequestParam id: String,
	               @RequestParam title: String,
	               @RequestParam content: String): ResponseEntity<*> {
		return Ret.ok(eventModel.addContent(id, title, content).content)
	}
	
	/**
	 * Removes content by name
	 */
	@PostMapping("/content/remove")
	fun contentRemove(@RequestParam id: String,
	                  @RequestParam title: String): ResponseEntity<*> {
		return Ret.ok(eventModel.removeContent(id, title).content)
	}
	
	/**
	 * Shows offers of event
	 */
	@GetMapping("/dates")
	fun dates(@RequestParam id: String): ResponseEntity<*> {
		return ResponseEntity.ok(eventModel.getEvent(id).dates)
	}
	
	/**
	 * Shows event's dates
	 */
	@GetMapping("/date/list")
	fun dateList(@RequestParam id: String): ResponseEntity<*> {
		return ResponseEntity.ok(eventModel.getEvent(id).datelist)
	}
	
	/**
	 * Add a date to event. Notice: this doesn't add any offers to event.
	 * Notice: dates should be send like that: 2018-02-28T15:40:20 (seconds can be dropped)
	 */
	@PostMapping("/date/add")
	fun dateAdd(@RequestParam id: String,
	            @RequestParam startDate: String,
	            @RequestParam endDate: String): ResponseEntity<*> {
		return Ret.ok(eventModel.addEventDate(
				id, LocalDateTime.parse(startDate), LocalDateTime.parse(endDate)
		).dates)
	}
	
	/**
	 * Edit order's dates. Notice: dates should be send like that: 2018-02-28T15:40:20 (seconds can be dropped)
	 */
	@PostMapping("/date/edit")
	fun dateEdit(@RequestParam id: String,
	             @RequestParam dateId: String,
	             @RequestParam(required = false) startDate: String?,
	             @RequestParam(required = false) endDate: String?): ResponseEntity<*> {
		return Ret.ok(eventModel.editEventDate(id, dateId,
				startDate?.let { LocalDateTime.parse(it) },
				endDate?.let { LocalDateTime.parse(it) }).dates)
	}
	
	/**
	 * Offers manipulation will be added later (20-21.03)
	 */
	@PostMapping("/offers/todo")
	fun todoOffers(): ResponseEntity<*> {
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