package com.avant.entity

import com.google.gson.JsonParser
import org.springframework.data.annotation.Id
import java.util.*

class Payment(@Id override var id: String = UUID.randomUUID().toString().slice(0..7), json: String) : Saveable {
	
	var cash: Double
	var orderId: String
	var liqOrderId: String
	var card: String
	var bank: String
	var cardType: String
	var ip: String
	var currency: String
	var timestamp: Long
	
	
	
	init {
		val jsonObject = JsonParser().parse(json).asJsonObject
		this.cash = jsonObject.get("amount").asDouble
		this.timestamp = jsonObject.get("create_date").asLong
		this.orderId = jsonObject.get("order_id").asString
		this.liqOrderId = jsonObject.get("liqpay_order_id").asString
		this.card = jsonObject.get("sender_card_mask2").asString
		this.bank = jsonObject.get("sender_card_bank").asString
		this.cardType = jsonObject.get("sender_card_type").asString
		this.ip = jsonObject.get("ip").asString
		this.currency = jsonObject.get("currency").asString
	}
}