package com.avant.util

import com.avant.entity.Event
import java.time.LocalDateTime


fun <T> tryElse(function: () -> T, elseValue: T): T = try {
	function()
} catch (e: Exception) {
	elseValue
}