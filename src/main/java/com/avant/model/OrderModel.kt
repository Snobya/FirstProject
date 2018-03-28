package com.avant.model

import com.avant.entity.Event
import com.avant.entity.Order
import com.avant.entity.Person
import com.avant.repo.OrderRepo
import com.avant.util.Locks
import kotlinx.coroutines.experimental.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.FileNotFoundException

@Service
class OrderModel {
	
	@Autowired
	private lateinit var orderRepo: OrderRepo
	@Autowired
	private lateinit var eventModel: EventModel
	@Autowired
	private lateinit var mailSender: MailSender
	@Autowired
	private lateinit var liqPayModel: LiqPayModel
	
	fun createOrder(eventId: String, dateId: String, mail: String?, comment: String?): Order {
		val event: Event = eventModel.getEvent(eventId)
		val order = Order(event = event, dateId = dateId)
		order.mail = mail
		order.comment = comment
		if (mail != null) {
			mailSender.sendCreateOrderEmail(order)
		}
		return orderRepo.save(order)
	}
	
	fun addPersons(id: String, persons: Iterable<Person>) = updateOrder(id) { order ->
		persons.forEach {
			order.addPerson(it)
		}
	}
	
	fun setMail(id: String, mail: String) = updateOrder(id) {
		if (it.mail == null) {
			launch { mailSender.sendCreateOrderEmail(it) }
		}
		it.mail == mail
	}
	
	fun requestOrderPayment(orderId: String, cash: Double?, sandbox: Boolean? = true): List<Pair<String, String>> {
		val order = getOrder(orderId)
		var currency = order.currency
		
		currency = when (currency.toLowerCase()) {
			"$", "usd"    -> "USD"
			"eur", "евро" -> "EUR"
			else          -> "UAH"
		}
		
		val params = HashMap<String, String>()
		params["action"] = "pay"
		params["amount"] = cash?.toString() ?: order.getDepositPrice().toString()
		params["currency"] = currency
		params["description"] = "Оплата туристических услуг, Клуб Авантюристов"
		params["order_id"] = orderId
		params["version"] = "3"
		params["sandbox"] = if (sandbox != false) "1" else "0"
		params["server_url"] = "https://avant-html.herokuapp.com/api/payment/process" // TODO Change website
		params["result_url"] = "https://avant-html.herokuapp.com/event/" +
				order.event.id + "?orderid=" + orderId
		val html = liqPayModel.getAPI().cnb_form(params)
		
		return listOf("form" to html, "orderId" to orderId)
	}
	
	private fun updateOrder(orderId: String, function: (Order) -> Unit): Order {
		var order: Order? = null
		Locks.withBlock(orderId) {
			order = getOrder(orderId)
			function.invoke(order!!)
			orderRepo.save(order!!)
		}
		return order ?: throw IllegalStateException("UpdateEvent must return event, null recieved instead")
	}
	
	fun getEvent(id: String): Event = eventModel.getEvent(id)
	fun getOrder(id: String): Order = orderRepo.findById(id).orElseThrow { FileNotFoundException("Event doesn't exist") }
	
}