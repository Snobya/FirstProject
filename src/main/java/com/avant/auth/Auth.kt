package com.avant.auth

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
annotation class Auth(vararg val value: AuthType = arrayOf())

enum class AuthType constructor(val value: String) {
	ADMIN("ADMIN"), ANY("ANY")
}

enum class AuthSource { // possible: SMS, TELEGRAM etc.
	MAIL_PASS
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
annotation class AuthParam(val value: String = "")