package com.edward.mukbang

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import java.net.URLEncoder

class YoutubeClient(private val context: Context) {
    private val networkClient: NetworkClient = NetworkClient(context)

    fun getNearbyVideos(videoName: String, position: LatLng, address: String, completion: (List<YoutubeVideo>) -> Unit){
        val videoNameEncoded = URLEncoder.encode("$videoName $address", "UTF-8")
//        val locationEncoded = URLEncoder.encode("${position.latitude},${position.longitude}", "UTF-8")
//        val locationRadius = "0.75mi"
//        val urlStringWithLocation = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&q=$videoNameEncoded&key=${context.getString(R.string.youtube_api_key)}&location=$locationEncoded&locationRadius=$locationRadius"
        val urlString = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&q=$videoNameEncoded&key=${context.getString(R.string.youtube_api_key)}"
        Log.d(LOG_TAG, "getNearbyVideos url string is: $urlString")
        networkClient.getJsonObjectRequest(urlString, onCompletion = { jsonObject, error ->
            jsonObject?.let {
                var youtubeVideos = ArrayList<YoutubeVideo>()
                val videos = it.getJSONArray("items")
                for(i in 0 until videos.length()) {
                    val videoId = videos.getJSONObject(i).getJSONObject("id").getString("videoId")
                    val thumbnailUrl = videos.getJSONObject(i).getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url")
                    youtubeVideos.add(YoutubeVideo(videoId, thumbnailUrl))
                }
                Log.d(LOG_TAG, "getNearbyVideos $it")
                completion(youtubeVideos)
            }
            error?.let {
                Log.e(LOG_TAG, "getNearbyVideos $it")
            }
        })
    }

    companion object {
        val LOG_TAG = YoutubeClient.javaClass.simpleName
    }
}