package com.avant.repo

import com.avant.entity.Order
import org.springframework.data.mongodb.repository.MongoRepository

interface OrderRepo : MongoRepository<Order, String> {
}