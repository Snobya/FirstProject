package com.avant.config

import com.avant.util.Ret
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class ControllerAdvisor : ResponseBodyAdvice<Any> {
	
	val ignoredClasses = setOf(IOException::class, ArithmeticException::class,
			MissingServletRequestParameterException::class, HttpRequestMethodNotSupportedException::class)
	
	override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>) = true
	
	override fun beforeBodyWrite(body: Any, returnType: MethodParameter, selectedContentType: MediaType,
	                             selectedConverterType: Class<out HttpMessageConverter<*>>,
	                             request: ServerHttpRequest, response: ServerHttpResponse): Any {
		response.headers.add("content-type", "application/json; charset=UTF-8")
		return body
	}
	
	@ExceptionHandler(Exception::class)
	fun handle(ex: Exception, request: HttpServletRequest): ResponseEntity<*> {
		if (request.requestURL.toString().contains("localhost")) {
			ex.printStackTrace()
			return Ret.code(800, "Exception")
		}
		val sb = StringBuilder()
			.append(request.remoteAddr)
			.append(" @ ")
			.append(Optional.ofNullable(request.getHeader("user-agent")).orElse("unknown device"))
			.append(" : ")
			.append(request.requestURL)
			.append("  ->  {")
			.append(request.parameterMap
				.keys.stream()
				.map { key -> key + ":" + request.getParameter(key) }
				.reduce { s1, s2 -> s1 + ", " + s2 }
				.orElse(""))
			.append("}\n")
			.append(ex.javaClass.name).append("  ->  ")
			.append(ex.localizedMessage).append("\n")
		ex.stackTrace
			.filter { it.className.startsWith("com.avant") }
			.forEach {
				sb.append(it.className).append(" : ").append(it.methodName).append(" @ line ")
					.append(it.lineNumber).append("\n")
			}
		
		ex.printStackTrace()
		if (!ignoredClasses.contains(ex::class)) {
			// TODO logging to telegram
		}
		
		println(sb)
		
		return when (ex.javaClass.name) {
			"NullPointerException"     -> Ret.code(503, sb)
			"IllegalArgumentException" -> Ret.code(406, sb)
			"MissingServletRequestParameterException",
			"HttpRequestMethodNotSupportedException",
			"FileNotFoundException",
			"IOException"              -> Ret.code(404, sb)
			else                       -> Ret.code(503, sb)
		}
	}
	
}