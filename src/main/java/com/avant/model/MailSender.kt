package com.avant.model

import com.avant.entity.Order
import org.springframework.stereotype.Service

@Service
class MailSender {
	
	fun sendCreateOrderEmail(order: Order) {
	
	}
	
	fun sendDepositEmail(order: Order, cash: Double) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
	
}