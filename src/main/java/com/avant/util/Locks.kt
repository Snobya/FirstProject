package com.avant.util

import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import java.util.concurrent.ConcurrentHashMap

object Locks {

	private val locks = ConcurrentHashMap<String, Mutex>()

	suspend fun withLock(lockId: String, function: () -> Unit) {
		locks.getOrPut(lockId, { Mutex() })
				.withLock {
					function.invoke()
				}
	}

	fun withBlock(lockId: String, function: () -> Unit) {
		runBlocking { withBlock(lockId, function) }
	}
}