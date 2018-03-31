package com.avant.api

import com.avant.model.ConfigService
import com.nikichxp.util.Async
import com.nikichxp.util.JsonUtil
import com.nikichxp.util.Ret
import kotlinx.coroutines.experimental.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpResponse
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestTemplate
import java.io.IOException
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/api/google/auth")
class GAuthAPI(
		context: ApplicationContext,
		val configService: ConfigService) {
	private val redirect_uri = "https://avant-2.herokuapp.com/api/google/auth/proceed"
	private var client_id: String? = null
	private var client_secret: String? = null
	private var refresh_token: String = configService.getParam("google.auth", "refresh-token", "null")
	private var access_token: String? = null
	
	val accessToken: String?
		get() {
			if (this.access_token == null) {
				try {
					this.updateAccessToken()
				} catch (var2: Exception) {
					var2.printStackTrace()
				}
				
			}
			
			return this.access_token
		}
	
	init {
		Async.async {
			this.client_id = context.environment.getProperty("google.client.id")
			this.client_secret = System.getenv("google_client_secret")
			
			try {
				this.updateAccessToken()
			} catch (var3: Exception) {
				println("Cannot update token. Need re-auth.")
			}
			
			println("GSheets service started, " + (this.refresh_token != "null") + " & " + (this.access_token != null))
		}
	}
	
	@GetMapping
	fun auth(@RequestParam(value = "redirect", required = false) redirect: Boolean?,
	         resp: HttpServletResponse) {
		val request = "https://accounts.google.com/o/oauth2/v2/auth?redirect_uri=$redirect_uri" +
				"&response_type=$RESPONSE_TYPE&client_id=$client_id" +
				"&scope=$SCOPE&access_type=$ACCESS_TYPE&prompt=$PROMPT"
		if (redirect != null && redirect) {
			resp.sendRedirect(request)
		}
		resp.writer.write(request)
	}
	
	@GetMapping("/status")
	fun status(): ResponseEntity<*> {
		return when {
			this.access_token == null  -> Ret.code(401, "Failure")
			this.refresh_token == null -> Ret.code(401, "refresh failure")
			else                       -> Ret.ok("OK")
		}
	}
	
	@GetMapping("/proceed")
	fun proceed(@RequestParam("code") code: String): ResponseEntity<*> {
		val headers = HttpHeaders()
		headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
		val map = LinkedMultiValueMap<String, String>()
		map.add("code", code)
		map.add("client_id", this.client_id)
		map.add("client_secret", this.client_secret)
		map.add("grant_type", "authorization_code")
		map.add("redirect_uri", this.redirect_uri)
		val request = HttpEntity(map, headers)
		val response = restTemplate.postForEntity("https://www.googleapis.com/oauth2/v4/token", request, String::class.java, *arrayOfNulls(0))
		println(response.body)
		
		return try {
			if (response.statusCodeValue == 200) {
				this.refresh_token = JsonUtil.of(response.body!!).getX("refresh_token")
			}
			
			configService.addParam("google.auth", "refresh-token", this.refresh_token)
			
			launch { updateAccessToken() }
			
			Ret.ok(response.body!!)
		} catch (e: Exception) {
			Ret.code(503, "Something went wrong!")
		}
	}
	
	@Scheduled(cron = "0 0 * * * *")
	private fun updateAccessToken() {
		val headers = HttpHeaders()
		headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
		val map = LinkedMultiValueMap<String, String>()
		map.add("refresh_token", this.refresh_token)
		map.add("client_id", this.client_id)
		map.add("client_secret", this.client_secret)
		map.add("grant_type", "refresh_token")
		val request = HttpEntity(map, headers)
		val response = restTemplate.postForEntity("https://www.googleapis.com/oauth2/v4/token", request, String::class.java, *arrayOfNulls(0))
		this.access_token = JsonUtil.of(response.body!!).getX("access_token")
		if (this.access_token != null) {
			println("Access token granted successfully")
		}
	}
	
	companion object {
		private val SCOPE = "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive.file+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive.readonly+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fspreadsheets+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fspreadsheets.readonly"
		private val ACCESS_TYPE = "offline"
		private val PROMPT = "consent"
		private val RESPONSE_TYPE = "code"
		private val GRANT_TYPE_1 = "authorization_code"
		private val GRANT_TYPE_2 = "refresh_token"
		private val restTemplate = RestTemplate()
		
		init {
			restTemplate.errorHandler = object : ResponseErrorHandler {
				@Throws(IOException::class)
				override fun hasError(clientHttpResponse: ClientHttpResponse): Boolean {
					return false
				}
				
				@Throws(IOException::class)
				override fun handleError(clientHttpResponse: ClientHttpResponse) {
				}
			}
		}
	}
}