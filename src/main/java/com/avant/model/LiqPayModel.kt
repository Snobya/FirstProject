package com.avant.model

import com.avant.entity.Payment
import com.avant.repo.MongoEntityInformationCreator
import org.springframework.stereotype.Service
import com.liqpay.LiqPay
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.*
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest

@Service
class LiqPayModel
@Autowired constructor(mongoEntityInformationCreator: MongoEntityInformationCreator) {
	
	@Autowired
	@Lazy
	private lateinit var orderModel: OrderModel
	@Autowired
	private lateinit var sheetsModel: GSheetsModel
	@Autowired
	private lateinit var mailSender: MailSender
	
	private val repo = mongoEntityInformationCreator.getSimpleMongoRepository(Payment::class.java)
	private val logger = Logger.getLogger("LiqPayModel")
	
	private var liqpay: LiqPay = try {
		LiqPay(System.getenv("LIQ_PUBLIC_KEY"), System.getenv("LIQ_PRIVATE_KEY")) //fictional key
	} catch (e: Exception) {
		val br = BufferedReader(InputStreamReader(FileInputStream("C:/liqpay.txt")))
		LiqPay(br.readLine(), br.readLine())
	}
	
	fun getAPI() = liqpay
	fun processRequest(req: HttpServletRequest) {
		//TODO: analyze if success or fail
		//TODO: check tokens
		println("Payment-page")
		req.parameterMap.forEach { k, v -> logger.warning(" -- " + k + " : " + Arrays.toString(v) + " --- ") }
		val resp = String(Base64.getDecoder().decode(req.getParameter("data")), Charset.forName("UTF-8"))
		println(resp)
		val payment = Payment(json = resp)
		repo.save(payment)
		orderModel.depositOrder(payment)
	}
}