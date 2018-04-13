package com.avant.api

import com.avant.entity.BlogPost
import com.avant.repo.MongoEntityInformationCreator
import com.avant.util.Ret
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.FileNotFoundException

@RestController
@RequestMapping("/api/blog")
class BlogAPI @Autowired constructor(mongoEntityInformationCreator: MongoEntityInformationCreator) {
	
	private val repo = mongoEntityInformationCreator.getSimpleMongoRepository(BlogPost::class.java)
	
	@GetMapping("/list")
	fun list(@RequestParam page: Int, @RequestParam(defaultValue = "10") pageSize: Int? = null): ResponseEntity<*> {
		return Ret.ok(repo.findAll(PageRequest.of(page, pageSize ?: 10, Sort.by(Sort.Direction.DESC, "posted"))))
	}
	
	@GetMapping("/{id}")
	fun id(@PathVariable id: String): ResponseEntity<*> {
		return Ret.ok(repo.findById(id).orElseThrow { FileNotFoundException("News not found") })
	}
	
	@PostMapping("/create")
	fun create(@RequestParam title: String, @RequestParam content: String,
	           @RequestParam(required = false) image: String?): ResponseEntity<*> {
		return Ret.ok(repo.save(BlogPost(title = title, content = content, image = image)))
	}
	
	@PostMapping("/update")
	fun update(@RequestParam id: String, @RequestParam(required = false) title: String?,
	           @RequestParam(required = false) content: String?,
	           @RequestParam(required = false) image: String?): ResponseEntity<*> {
		val post = repo.findById(id).orElseThrow { FileNotFoundException("News not found") }
		title?.apply { post.title = this }
		content?.apply { post.content = this }
		image?.apply { post.image = this }
		return Ret.ok(repo.save(post))
	}
}