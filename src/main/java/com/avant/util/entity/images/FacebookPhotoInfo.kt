package com.avant.util.entity.images

import java.util.ArrayList
import java.util.Comparator

class FacebookPhotoInfo {
	
	var imageList: MutableList<FacebookPhotoEntity> = mutableListOf()
	var id: String? = null
	var height: Int = 0
	var width: Int = 0
	
	fun setImages(list: List<FacebookPhotoEntity>) {
		imageList = ArrayList()
		list.forEach { img ->
			if (img.height > height) {
				imageList.add(img)
			}
		}
		if (imageList.isEmpty()) {
			list.stream().max(Comparator.comparingInt { it.height })
				.ifPresent { imageList.add(it) }
		}
	}
	
	class FacebookPhotoEntity {
		var source: String? = null
		var height: Int = 0
		var width: Int = 0
	}
}
