package com.avant.config

import org.apache.tomcat.util.http.fileupload.IOUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.io.FileInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/")
class HomeRouter {
	
	private val resourceDir = System.getProperty("user.dir") + "/src/main/resources/static"
	
	@RequestMapping("/")
	fun homeRoute(response: HttpServletResponse) {
		dispatch("/index.html", response)
	}
	
	// test functions below
	
	@GetMapping("/test")
	fun test(request: HttpServletRequest, response: HttpServletResponse) {
		request.reader.lines().forEach { println(it) }
	}
	
	@RequestMapping("/**")
	fun router(request: HttpServletRequest, response: HttpServletResponse) {
		dispatch(request.requestURI, response)
	}
	
	private fun dispatch(path: String, response: HttpServletResponse) {
		response.characterEncoding = "UTF-8"
		try {
			IOUtils.copy(FileInputStream(resourceDir + path), response.outputStream)
		} catch (e: Exception) {
			response.writer.write("404: File not found")
		}
	}
	
}