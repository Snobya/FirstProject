package com.avant.model

import com.avant.api.GAuthAPI
import com.avant.entity.Event
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.nikichxp.util.Json
import com.nikichxp.util.JsonUtil
import com.nikichxp.util.TelegramBotMethods
import org.apache.http.impl.client.HttpClients
import org.jboss.logging.Logger
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestTemplate
import java.nio.charset.Charset
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors

@Service
class GSheetsModel(
		val configService: ConfigService,
		var gAuthAPI: GAuthAPI) {
	
	val logger = Logger.getLogger(this::class.java)
	val sheetsIds = configService.getConfig("sheets.id")
	
	@Synchronized
	fun createEventTable(event: Event): String? {
		val headers = HttpHeaders()
		headers.set("charset", "UTF-8")
		headers.contentType = MediaType.APPLICATION_JSON
		
		val arr = event.dates.stream()
			.map { date -> Json.of("properties", Json.of("title", date.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))).json() }
			.collect(Collectors.toList<Any>())
		
		val entity = HttpEntity(
				Json.of(
						"properties", Json
					.of("title", event.title))
					.and("sheets", arr)
					.toString().toByteArray(), headers)
		
		val response = restTemplate.postForEntity(
				"https://sheets.googleapis.com/v4/spreadsheets?access_token=" + gAuthAPI.accessToken!!, entity, String::class.java)
		
		if (debug) {
			logger.info(response)
			TelegramBotMethods.sendMessage("34080460", response.toString())
		}
		
		if (response.statusCodeValue != 200) {
			logger.warn("Warning, response is not 200")
			return null
		}
		
		val spreadsheetId = JsonUtil.of(response.body!!).getX("spreadsheetId")
		
		sheetsIds.params[event.id] = spreadsheetId
		sheetsIds.save()
		
		return spreadsheetId
	}
	
	@Synchronized
	fun updatePages(event: Event) {
		val response = restTemplate
			.getForEntity("https://sheets.googleapis.com/v4/spreadsheets/${sheetsIds.params[event.id]}", String::class.java)
		if (response.statusCodeValue == 404) {
			createEventTable(event)
			return
		}
		val eventDates = event.dates.map { it.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) }.toMutableSet()
		JsonParser().parse(response.body).asJsonObject.getAsJsonArray("sheets").forEach { sheet ->
			val name = sheet.asJsonObject.getAsJsonObject("properties").get("title").asString
			eventDates.remove(name)
		}
		for (date in eventDates) {
			createPage(sheetsIds.params[event.id]!!, date)
		}
		return
	}
	
	@Synchronized
	fun createPage(sheetId: String, title: String): Int {
		val requestJson = "{\"requests\":[{\"addSheet\":{\"properties\":{\"title\":\"$title\"}}}]}"
		
		val headers = HttpHeaders()
		headers.set("charset", "UTF-8")
		headers.contentType = MediaType.APPLICATION_JSON
		
		val entity = HttpEntity(requestJson.toByteArray(), headers)
		
		val response = restTemplate.postForEntity(
				"https://sheets.googleapis.com/v4/spreadsheets/" + sheetId + ":batchUpdate" +
						"?access_token=" + gAuthAPI.accessToken,
				entity,
				String::class.java)
		
		if (debug) {
			logger.info(response)
		}
		
		if (response.statusCodeValue == 200) {
			writeArrayToTable(sheetId, title, 1, arrayOf("ID заказа", "ФИО", "Телефон", "Тип", "Размещение", "Аванс",
					"Полная цена", "Коммент"))
		}
		
		return response.statusCodeValue
	}
	
	// TODO Update method for page - to make new dates appear
	// TODO Cache the first line
	
	@Synchronized
	fun getFirstFreeLine(sheetId: String?, page: String): Int {
		try {
			val x3 = restTemplate
				.getForEntity("https://sheets.googleapis.com/v4/spreadsheets/" + sheetId + "/values/" + page + "!" + "A1:A10000"
						+ "?majorDimension=COLUMNS&access_token="
						+ gAuthAPI.accessToken, String::class.java).body!!
			
			return JsonUtil.of(x3).getJson().getAsJsonArray("values").get(0).asJsonArray.size() + 1
		} catch (ingored: Exception) {
			return 1
		}
		
	}
	
	/**
	 * Simple write a line to table
	 *
	 * @param sheetId sheet ID, it's long value in URL
	 * @param page    page title, like 'Page1' or '2017-05-12T11:30'
	 * @param row     a row of writing - 1,2,3... to write on A1..E1, A2..E2 etc. Use -1 to get first free line
	 * @param data    all the rows
	 */
	fun writeArrayToTable(sheetId: String, page: String, row: Int, data: Array<String>) {
		val rowX = if (row < 0) getFirstFreeLine(if (sheetId == "default") sheetId else sheetId, page) else row
		writeToTable(sheetId, page + "!" + ROWSTART + rowX + ":" + (ROWSTART.toInt() + data.size - 1).toChar() + rowX,
				arrayOf(data))
	}
	
	fun autoWriteToTable(sheetId: String, page: String, vararg data: String) =
		writeArrayToTable(sheetId, page, -1, data as Array<String>)
	
	@Synchronized
	fun writeToTable(sheetId: String, range: String, data: Array<Array<String>>, retry: Boolean = false) {
		val arrays = arrayOfNulls<Json.JsonArr>(data.size)
		for (i in arrays.indices) {
			arrays[i] = Json.JsonArr(data[i])
		}
		
		val headers = HttpHeaders()
		headers.set("charset", "UTF-8")
		headers.contentType = MediaType.APPLICATION_JSON
		
		val map = HashMap<String, Any>()
		map.put("valueInputOption", "RAW")
		
		val dataMap = HashMap<String, Any>()
		dataMap.put("range", range)
		dataMap.put("values", data)
		
		map.put("data", dataMap)
		val entity = HttpEntity(gson.toJson(map).toByteArray(Charset.forName("UTF-8")), headers)
		
		val response = restTemplate.postForEntity(
				"https://sheets.googleapis.com/v4/spreadsheets/$sheetId/values:batchUpdate" +
						"?access_token=${gAuthAPI.accessToken}",
				entity,
				String::class.java)
		
		if (response.statusCodeValue != 200) {
			if (retry) {
				throw IllegalArgumentException()
			}
			logger.warn("WARNING! Error writing to sheet " + sheetId + " data: " + Arrays.toString(data))
			logger.warn(response)
			try {
				createPage(sheetId, range.split(Regex("!"))[0])
				writeToTable(sheetId, range, data, true)
			} catch (e: IllegalArgumentException) {
				logger.warn("And this won't be fixed by rename")
			}
		}
	}
	
	@Synchronized
	fun updateCellsWhere(sheetId: String, page: String, criteria: (List<String>) -> Boolean, updateFx: (MutableList<String>) -> MutableList<String>) {
		val resp = restTemplate.getForEntity("https://sheets.googleapis.com/v4/spreadsheets/$sheetId/values/$page" +
				"?access_token=${gAuthAPI.accessToken}", String::class.java)
		val arrays = JsonParser().parse(resp.body).asJsonObject.getAsJsonArray("values").map {
			it.asJsonArray.map { it.asString }
		}
		
		(0 until arrays.size).filter {
			criteria.invoke(arrays[it])
		}.stream().forEach {
			//				clearData(sheetId, "$page!A${it + 1}:Z${it + 1}")
			writeArrayToTable(sheetId = sheetId, page = page, row = it + 1, data = updateFx.invoke(arrays[it].toMutableList()).toTypedArray())
		}
	}
	
	@Synchronized
	fun clearData(sheetId: String, range: String) {
		val response = restTemplate.postForEntity(
				"https://sheets.googleapis.com/v4/spreadsheets/$sheetId/values/$range:clear" +
						"?access_token=${gAuthAPI.accessToken}",
				"{}",
				String::class.java)
	}
	
	fun checkIfTableExist(sheetId: String, pageTextId: String): Boolean {
		val response = restTemplate.getForEntity(
				"https://sheets.googleapis.com/v4/spreadsheets/$sheetId?access_token=" + gAuthAPI.accessToken,
				String::class.java)
		
		if (debug) {
			println(response)
		}
		
		if (response.statusCodeValue != 200) {
			return false
		}
		try {
			for (json in JsonUtil.of(response.body!!).getJson().getAsJsonArray("sheets")) {
				if (JsonUtil.of(json.toString()).getX("properties.title") == pageTextId) {
					return true
				}
			}
		} catch (ex: Exception) {
			return false
		}
		
		return createPage(sheetId, pageTextId) == 200
	}
	
	companion object {
		
		var debug = true
		var gson = Gson()
		private var restTemplate: RestTemplate
		/**
		 * every row will start from this one. if A - A12..E12, C - C12..G12 etc
		 */
		private val ROWSTART = 'A'
		
		init {
			val requestFactory = HttpComponentsClientHttpRequestFactory(HttpClients.createDefault())
			
			restTemplate = RestTemplate(requestFactory)
			restTemplate.errorHandler = object : ResponseErrorHandler {
				override fun hasError(clientHttpResponse: ClientHttpResponse): Boolean {
					return false
				}
				
				override fun handleError(clientHttpResponse: ClientHttpResponse) {
					if (clientHttpResponse.rawStatusCode == 403) {
						println("Warning! Illegal permissions!")
					}
				}
			}
		} // configure rest template
	}
	
}