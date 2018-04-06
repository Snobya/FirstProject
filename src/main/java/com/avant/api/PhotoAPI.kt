package com.avant.api

import com.avant.model.ConfigService
import com.avant.util.entity.images.FacebookAlbumInfo
import com.avant.util.entity.images.FacebookAlbumsResponse
import com.avant.util.entity.images.FacebookPhotoInfo
import com.google.gson.Gson
import com.nikichxp.util.HttpClient
import com.nikichxp.util.Ret
import kotlinx.coroutines.experimental.launch
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

@RestController
@RequestMapping("/api/photos")
class PhotoAPI(
		configService: ConfigService) {
	
	private var cachedAlbums = LinkedList<FacebookAlbumInfo>()
	private val albumMap = HashMap<String, FacebookAlbumInfo>()
	private var accessToken: String? = "EAAKaUnYlm5sBANq8bE7Gk7CsgycgYcEukpZCe5xFEefZB2OYnhcJ0kZCjjWDxL5X9a4jRV1Kbs3" +
			"CHyySabNPNLTv98sAPqtF7iMfA5oU3bqOJDM0VuFQODro4wxetkzNVfgoFGmZASXlUwZAqsXX0ttIuWQUHjF0ZAfnJ8RS0gnwLXigWg" +
			"HtmpDLX6QR5e4FQlZBCFC9c5HgAZDZD"
	
	private val url = "https://graph.facebook.com/v2.10/609864765786003/albums?fields=" +
			"created_time,name,description,cover_photo,link,id,photo_count,photos.limit(9999)%7Bimages%7D" +
			"&access_token=" + accessToken
	
//	@Scheduled(fixedDelay = (1000 * 60 * 60).toLong()) TODO Disable until FaceBook Fix
	private fun updateAlbums() {
		println("get albums start")
		accessToken = System.getenv("facebook-token")
		if (accessToken == null) {
			try {
				accessToken = BufferedReader(InputStreamReader(FileInputStream("C:/keys/facebook-hello-world"))).readLine()
			} catch (e: IOException) {
				e.printStackTrace()
			}
			
		}
		
		val localCachedAlbums = LinkedList<FacebookAlbumInfo>()
		var response: FacebookAlbumsResponse? = null
		val gson = Gson()
		
		do {
			println(HttpClient.get(url + if (response != null) "&after=" + response.paging!!.next else ""))
			response = gson.fromJson(HttpClient.get(url + if (response != null) "&after=" + response.paging!!.next else ""), FacebookAlbumsResponse::class.java)
			//			System.out.println(response);
			if (response!!.data != null) {
				for (info in response.data!!) {
					localCachedAlbums.add(info)
					Optional.ofNullable(info.cover_photo).ifPresent { cover ->
						launch {
							val data = Gson().fromJson(HttpClient.get("https://graph.facebook.com/v2.10/" + cover.id + "?fields=width,height,images&access_token=" + accessToken), FacebookPhotoInfo::class.java)
							cover.height = data.height
							cover.width = data.width
							cover.setImages(data.imageList)
						}
					}
				}
			}
		} while (response!!.paging?.next != null)
		localCachedAlbums.removeIf { it.name?.toLowerCase() == "timeline photos" || it.name?.toLowerCase() == "mobile uploads" }
		cachedAlbums = localCachedAlbums
		println("get albums end")
		localCachedAlbums.forEach { album -> albumMap[album.id!!] = album }
	}
	
	@GetMapping("/albums")
	fun albums(): ResponseEntity<*> {
		return Ret.ok()
	}
	
	@GetMapping("/album")
	fun album(@RequestParam album: String): ResponseEntity<*> {
		return Ret.ok()
	}
	
}