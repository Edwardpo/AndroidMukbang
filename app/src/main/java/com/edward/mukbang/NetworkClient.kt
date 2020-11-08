package com.edward.mukbang

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class NetworkClient(var context: Context) {

    fun getJSONArrayRequest(urlString: String, onCompletion: (jsonArray: JSONArray?, error: VolleyError?) -> Unit) {
        val queue = Volley.newRequestQueue(context)
        val jsonArrayRequest = JsonArrayRequest(
            urlString,
            JSONArrayResponseListener(onCompletion),
            Response.ErrorListener { error ->
                onCompletion(null, error)
            })
        queue.add(jsonArrayRequest)
    }

    fun getJsonObjectRequest(urlString: String, onCompletion: (jsonObject: JSONObject?, error: VolleyError?) -> Unit) {
        val queue = Volley.newRequestQueue(context)
        val jsonObjectRequest = JsonObjectRequest(urlString,
            null,
            JSONObjectResponseListener(onCompletion),
            Response.ErrorListener { error ->
                onCompletion(null, error)
            })
        queue.add(jsonObjectRequest)
    }

    fun getImageRequest(urlString: String, onCompletion:(bitmap: Bitmap?, error: VolleyError?) -> Unit) {
        val queue = Volley.newRequestQueue(context)

        val imageRequest = ImageRequest(urlString,
            ImageResponseListener(onCompletion),
            320,
            180,
            ImageView.ScaleType.CENTER_CROP,
            Bitmap.Config.RGB_565,Response.ErrorListener { error ->
                onCompletion(null, error)
        })

        queue.add(imageRequest)
    }

    class JSONArrayResponseListener(var onCompletion: (jsonArray: JSONArray?, error: VolleyError?) -> Unit): Listener<JSONArray> {
        override fun onResponse(response: JSONArray?) {
            onCompletion(response, null)
        }
    }
    class JSONObjectResponseListener(var onCompletion: (jsonObject: JSONObject?, error: VolleyError?) -> Unit): Listener<JSONObject> {
        override fun onResponse(response: JSONObject?) {
            onCompletion(response, null)
        }
    }

    class ImageResponseListener(var onCompletion: (bitmap: Bitmap?, error: VolleyError?) -> Unit): Listener<Bitmap> {
        override fun onResponse(response: Bitmap?) {
            onCompletion(response, null)
        }

    }

}