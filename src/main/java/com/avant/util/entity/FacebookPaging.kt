package com.avant.util.entity


class FacebookPaging {
	var next: String? = null
	var previous: String? = null
	var cursors: Cursor? = null
	
	class Cursor {
		var before: String? = null
		var after: String? = null
	}
}