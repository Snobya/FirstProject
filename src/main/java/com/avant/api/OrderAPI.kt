package com.avant.api

import com.avant.entity.Event
import com.avant.entity.Order
import com.avant.repo.EventRepository
import com.avant.util.Ret
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.FileNotFoundException
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/order")
class OrderAPI {
	
	@Autowired
	private lateinit var eventRepo: EventRepository
	
	@PostMapping("/create")
	fun create(req: HttpServletRequest,
	           @RequestParam eventId: String,
	           @RequestParam dateId: String,
	           @RequestParam usersCount: Int,
	           @RequestParam(required = false) mail: String? = null,
	           @RequestParam(required = false) comment: String? = null): ResponseEntity<*> {
		
		val event: Event = eventRepo.findById(eventId).orElseThrow { FileNotFoundException("Order not found.") }
		val order = Order(event = event, dateId = dateId)
		order.mail = mail
		order.comment = comment
		
		//		for (n in 0 until usersCount) {
		//			currentDeposit += order.addPerson(
		//					Order.Person(
		//							req.getParameter("name" + n),
		//							req.getParameter("phone" + n),
		//							req.getParameter("document" + n),
		//							event.getOffer(req.getParameter("offer" + n)),
		//							req.getParameter("type" + n),
		//							req.getParameter("class" + n),
		//							LocalDate.parse(req.getParameter("bday" + n))
		//					)
		//			)
		//		}
		//		PersonTracker.logCustomers(order)
		//		orderRepo.save(order)
		//
		//		if (mail != null) {
		//			async { MailSender.sendMail("Вы сделали заказ!", getOrderedDetails(order) + getThanksPayment(order), mail) }
		//		}
		//
		//		return requestPayment(order.getId(), currentDeposit + 0.0, "1")
		//
		return Ret.code(500, "Not implemented")
	}
	
}