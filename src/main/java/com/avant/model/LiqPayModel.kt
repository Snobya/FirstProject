package com.avant.model

import org.springframework.stereotype.Service
import com.liqpay.LiqPay
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

@Service
class LiqPayModel {
	
	private var liqpay: LiqPay = try {
		LiqPay(System.getenv("LIQ_PUBLIC_KEY"), System.getenv("LIQ_PRIVATE_KEY")) //fictional key
	} catch (e: Exception) {
		val br = BufferedReader(InputStreamReader(FileInputStream("C:/liqpay.txt")))
		LiqPay(br.readLine(), br.readLine())
	}
	
	fun getAPI() = liqpay
}