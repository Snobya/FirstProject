package com.avant.entity

import com.avant.util.IDGenerator
import java.time.LocalDateTime

data class BlogPost(override var id: String = IDGenerator.shortId(), var title: String, var content: String,
                    var posted: Long = System.currentTimeMillis()) : Saveable

data class NewsPost(override var id: String = IDGenerator.shortId(), var title: String, var content: String,
                    var posted: Long = System.currentTimeMillis()) : Saveable