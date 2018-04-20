package com.avant.model

import com.avant.entity.Payment
import com.avant.repo.MongoEntityInformationCreator
import org.springframework.stereotype.Service
import com.liqpay.LiqPay
import org.jboss.logging.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.*
import javax.servlet.http.HttpServletRequest

@Service
class LiqPayModel
@Autowired constructor(mongoEntityInformationCreator: MongoEntityInformationCreator,
                       @Lazy val orderModel: OrderModel,
                       val mailSender: MailSender) {

	private val repo = mongoEntityInformationCreator.getSimpleMongoRepository(Payment::class.java)
	private val logger = Logger.getLogger(this::class.java)

	private lateinit var liqpay: LiqPay

	init {
		try {
			liqpay = LiqPay(System.getenv("LIQ_PUBLIC_KEY"), System.getenv("LIQ_PRIVATE_KEY")) //fictional key
		} catch (e: Exception) {
			try {
				val br = BufferedReader(InputStreamReader(FileInputStream("C:/liqpay.txt")))
				liqpay = LiqPay(br.readLine(), br.readLine())
			} catch (e2: Exception) {
				Logger.getLogger(this::class.java).warn("LiqPay init failed!")
			}
		}
	}

	fun getAPI() = liqpay
	fun processRequest(req: HttpServletRequest) {
		//TODO: analyze if success or fail
		//TODO: check tokens
		println("Payment-page")
		req.parameterMap.forEach { k, v -> logger.warn(" -- " + k + " : " + Arrays.toString(v) + " --- ") }
		val resp = String(Base64.getDecoder().decode(req.getParameter("data")), Charset.forName("UTF-8"))
		println(resp)
		val payment = Payment(json = resp)
		repo.save(payment)
		orderModel.depositOrder(payment)
	}
}