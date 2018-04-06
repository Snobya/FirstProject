package com.avant.api

import com.avant.model.GSheetsModel
import com.avant.util.Ret
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/google/sheets")
class GSheetsAPI(
		val gSheetsModel: GSheetsModel) {
	
	/**
	 * Gets the map: what sheet id is mapped to event id.
	 */
	@GetMapping("/id-map")
	fun idMap(): ResponseEntity<*> {
		return Ret.ok(gSheetsModel.sheetsIds.params)
	}
	
	/**
	 * Set Sheet ID that responsible for Event ID. DOES NOT COPY data from table A to B
	 */
	@PostMapping("/id-set/lite")
	fun liteIdSet(@RequestParam sheetId: String, @RequestParam eventId: String): ResponseEntity<*> {
		gSheetsModel.sheetsIds.params[eventId] = sheetId
		gSheetsModel.sheetsIds.save()
		return Ret.ok("Done")
	}
	
//	@PostMapping("/id-set/copy")
//	fun copyIdSet(@RequestParam sheetId: String, @RequestParam eventId: String): ResponseEntity<*> {
//		return Ret.error(500, "Not implemented")
//	}
}