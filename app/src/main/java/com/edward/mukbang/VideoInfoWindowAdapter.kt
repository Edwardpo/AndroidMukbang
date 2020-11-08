package com.edward.mukbang

import android.app.Activity
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class VideoInfoWindowAdapter(activity: Activity): GoogleMap.InfoWindowAdapter {

    private val contents: View =
        activity.layoutInflater.inflate(R.layout.video_info_window_contents, null)

    override fun getInfoContents(marker: Marker?): View {
        val restaurantNameTextView = contents.findViewById<TextView>(R.id.textview_name)
        restaurantNameTextView.text = marker?.title

        val latLngTextView = contents.findViewById<TextView>(R.id.textview_latlng)
        latLngTextView.text = "${marker?.position?.latitude}, ${marker?.position?.longitude}"

        return contents
    }

    override fun getInfoWindow(marker: Marker?): View? {
        return null
    }
}