package com.avant.auth

import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.lang.reflect.Method
import java.util.Arrays

class AccessInterceptor : HandlerInterceptorAdapter() {
	
	
	@Throws(Exception::class)
	override fun preHandle(request: HttpServletRequest?, response: HttpServletResponse?, handler: Any?): Boolean {
		
		if (request!!.requestURL.toString().startsWith("http:") && !request.requestURL.toString().contains("localhost")) {
			response!!.sendRedirect("https" + request.requestURL.toString().substring(4))
			return false
		}
		
		//Logging
		println(request.requestURL.toString() + "  ->  {" +
				request.parameterMap
					.keys.stream()
					.map { key -> key + ":" + request.getParameter(key) }
					.reduce { s1, s2 -> s1 + ", " + s2 }.orElse("")
				+ "}"
		)
		
		//Auth
		
		val method: Method
		var auth: Auth? = null
		try {
			method = (handler as HandlerMethod).method
			auth = if (method.getAnnotation(Auth::class.java) != null)
				method.getAnnotation(Auth::class.java)
			else
				method.declaringClass.getAnnotation(Auth::class.java)
		} catch (e1: ClassCastException) {
			println("Class cast exception. Auth may be failed.")
		} catch (e: Exception) {
			e.printStackTrace()
		}
		
		if (auth == null) {
			return true
		}
		
		for (type in auth.value) {
			when (type) {
				AuthType.ANY -> if (getUser(request) != null) {
					return true
				}
				else         -> {
				}
			}
		}
		val user: User?
		try {
			user = getUser(request)
		} catch (e: Exception) {
			return true //TODO false
		}
		
		if (user == null) {
			return true //TODO false
		}
		println("Check auth: " + user)
		return true
	}
	
	companion object {
		
		fun getUser(request: HttpServletRequest): User? {
			var token: String?
			try {
				token = Arrays.stream(request.cookies)
					.filter { cookie -> cookie.name.contains("authToken") }
					.map { it.getValue() }
					.findAny().orElseGet { if (request.getParameter("token") != null) request.getParameter("token") else request.getParameter("sessionId") }
			} catch (e: Exception) {
				token = if (request.getParameter("token") != null)
					request.getParameter("token")
				else
					request.getParameter("sessionId")
			}
			
			return if (token == null) {
				null
			} else AuthController.getUser(token)
		}
	}
	
}
