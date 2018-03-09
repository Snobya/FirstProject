package com.avant.config

import com.avant.auth.AccessInterceptor
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.*

@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan
class WebApplicationConfig : WebMvcConfigurer {
	
	private val RESOURCE_LOCATIONS = arrayOf("classpath:/META-INF/resources/",
			"classpath:/resources/", "classpath:/static/", "classpath:/public/",
			"classpath:/resources/static/", "classpath:/src/main/resources/static/")
	
	override fun addResourceHandlers(registry: ResourceHandlerRegistry?) {
		if (!registry!!.hasMappingForPattern("/api/**")) {
			registry.addResourceHandler("/**").addResourceLocations(
					*RESOURCE_LOCATIONS)
		}
	}
	
	override fun addCorsMappings(registry: CorsRegistry?) {
		registry!!.addMapping("/**")
			.allowedHeaders("*")
			.allowedMethods("*")
			.allowedOrigins("*")
			.maxAge(java.lang.Long.MAX_VALUE)
	}
	
	override fun addInterceptors(registry: InterceptorRegistry?) {
		registry!!.addInterceptor(AccessInterceptor()).addPathPatterns("/**")
	}
	
}