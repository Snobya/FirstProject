package com.avant.util.entity.images

import com.avant.util.entity.FacebookPaging

class FacebookAlbumInfo : Cloneable {
	var photo_count: Int = 0
	var created_time: String? = null
	var name: String? = null
	var id: String? = null
	var link: String? = null
	var photos: FacebookPhotoListInfoResponse? = null
	var cover_photo: FacebookPhotoInfo? = null
	
	public override fun clone(): Any {
		return super.clone()
	}
	
	inner class FacebookPhotoListInfoResponse {
		var data: Array<FacebookPhotoInfo>? = null
		var paging: FacebookPaging? = null
	}
	
}