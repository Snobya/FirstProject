package com.avant.entity

import com.avant.auth.AuthSource
import com.avant.auth.User
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef

open class AuthReason(@Id override var id: String,
                      var authSource: AuthSource = AuthSource.MAIL_PASS,
                      var authData: String,
                      var mail: String = "",
                      @DBRef var user: User) : Saveable {
	
}