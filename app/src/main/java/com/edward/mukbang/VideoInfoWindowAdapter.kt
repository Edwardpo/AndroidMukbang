package com.edward.mukbang

import android.app.Activity
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.model.Place
import java.net.URL

class VideoInfoWindowAdapter(private var activity: Activity, private var videoClickedListener: OnVideoClickedListener): GoogleMap.InfoWindowAdapter {

    private val contents: View =
        activity.layoutInflater.inflate(R.layout.video_info_window_contents, null)

    override fun getInfoContents(marker: Marker?): View {
        marker?.let { marker ->
            val restaurantNameTextView = contents.findViewById<TextView>(R.id.textview_name)
            val restaurantName = marker.title
            restaurantNameTextView.text = restaurantName

            val address = (marker?.tag as Place)?.address ?: ""

            showThumbnail("Lombardis Pizzeria", marker.position, "")
//            showThumbnail(restaurantName, marker.position, address)
        }
        return contents
    }

    private fun showThumbnail(restaurantName: String, position: LatLng, address: String) {
        YoutubeClient(activity).getNearbyVideos(restaurantName, position, address, completion = { youtubeVideos ->
            if(youtubeVideos.isNotEmpty()) {
                val firstMatchingVideo = youtubeVideos.first()
                showThumbnailForVideo(firstMatchingVideo)
            }
        })
    }

    private fun showThumbnailForVideo(video: YoutubeVideo){
        Log.d(LOG_TAG, "showThumbnailForVideo ${video.videoId}")
        val thumbnail = contents.findViewById<ImageView>(R.id.imageview_video_thumbnail)
        NetworkClient(activity).getImageRequest(video.thumbnailUrl, onCompletion = { bitmap, error ->
            bitmap?.let {bitmap ->
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

    private class VideosAdapter(private val youtubeVideos: List<YoutubeVideo>): RecyclerView.Adapter<VideoViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_video, parent, false)
            return VideoViewHolder(view)
        }

        override fun getItemCount(): Int {
            return youtubeVideos.size
        }

        override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
            holder.setVideo(youtubeVideos.get(position))
        }

    }

    private class VideoViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView) {
        fun setVideo(video: YoutubeVideo) {

        }
    }

    interface OnVideoClickedListener {
        fun onVideoClicked(video: YoutubeVideo)
    }

    companion object {
        val LOG_TAG = VideoInfoWindowAdapter.javaClass.simpleName
    }
}