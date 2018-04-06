package com.avant.model

import com.avant.entity.Event
import com.avant.entity.Order
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct

@Service
class GSheetsModelProxy(
		@Lazy val gSheetsModel: GSheetsModel,
		@Lazy val orderModel: OrderModel) {
	
	@PostConstruct
	fun postConstruct() {
		gSheetsModel.sheetsIds!!
	}
	
	suspend fun onEventCreate(event: Event) {
		gSheetsModel.createEventTable(event)
	}
	
	suspend fun onEventUpdate(event: Event) {
		gSheetsModel.updatePages(event)
	}
	
	suspend fun onOrderCreate(order: Order) {
		gSheetsModel.checkIfTableExist(gSheetsModel.sheetsIds.params[order.event.id]!!,
				order.dateInfo.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
		
		for (person in order.persons) {
			gSheetsModel.writeArrayToTable(gSheetsModel.sheetsIds.params[order.event.id]!!,
					order.dateInfo.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
					-1,
					arrayOf<String>(order.id, person.name, person.phone, person.offerType, person.offerName,
							"0", order.getFullPrice().toString(), order.comment ?: "")
			)
		}
	}
	
	suspend fun onOrderUpdate(order: Order) {
		gSheetsModel.updateCellsWhere(gSheetsModel.sheetsIds.params[order.event.id]!!,
				order.dateInfo.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
				{ it[0] == order.id },
				{
					it[5] = DecimalFormat("#,###.##").format(order.orderDeposit)
					return@updateCellsWhere it
				}
		)
	}
	
}