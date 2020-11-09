package com.edward.mukbang

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.model.Place

class VideoInfoWindowAdapter(private var activity: Activity): GoogleMap.InfoWindowAdapter {

    private val contents: View =
        activity.layoutInflater.inflate(R.layout.video_info_window_contents, null)

    override fun getInfoContents(marker: Marker?): View {
        marker?.let { marker ->
            val restaurantNameTextView = contents.findViewById<TextView>(R.id.textview_name)
            val restaurantName = marker.title
            restaurantNameTextView.text = restaurantName

            showThumbnail(restaurantName, marker)
        }
        return contents
    }

    private fun showThumbnail(restaurantName: String, marker: Marker) {
//        val address = (marker?.tag as Place)?.address ?: ""
        YoutubeClient(activity).getNearbyVideos(restaurantName, marker.position, "", completion = { youtubeVideos ->
            if(youtubeVideos.isNotEmpty()) {
                val firstMatchingVideo = youtubeVideos.first()
                showThumbnailForVideo(firstMatchingVideo, marker)
            }
        })
    }

    private fun showThumbnailForVideo(video: YoutubeVideo, marker: Marker){
        Log.d(LOG_TAG, "showThumbnailForVideo ${video.videoId}")
        val thumbnail = contents.findViewById<ImageView>(R.id.imageview_video_thumbnail)
        NetworkClient(activity).getImageRequest(video.thumbnailUrl, onCompletion = { bitmap, error ->
            bitmap?.let {bitmap ->
                marker.tag = video
                Log.d(LOG_TAG, "showThumbnailForVideo setting the thumbnail $bitmap")
                thumbnail.setImageBitmap(bitmap)
            }
            error?.let {
                Log.e(LOG_TAG, "showThumbnailForVideo encountered error: $it")
            }
        })
    }


    override fun getInfoWindow(marker: Marker?): View? {
        return null
    }

    companion object {
        val LOG_TAG = VideoInfoWindowAdapter.javaClass.simpleName
    }
}