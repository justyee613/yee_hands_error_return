/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package com.google.mediapipe.examples.gesturerecognizer.fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app.databinding.ItemGestureRecognizerResultBinding
import com.google.mediapipe.tasks.components.containers.Category
import java.util.Locale
import kotlin.math.min

import android.media.MediaPlayer
import android.content.Context
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.app.R
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

fun sendNoneGestureCountToDatabase(context: Context, noneGestureCount: Int) {
    val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    val email = sharedPreferences.getString("email", "") ?: ""
    val password = sharedPreferences.getString("password", "") ?: ""

    val URL = "http://0w0chen3060-2.ddns.net:7777/app_login/insert_record.php"

    val stringRequest = object : StringRequest(
        Request.Method.POST, URL,
        Response.Listener { response ->
            try {
                val jsonObject = JSONObject(response)
                val status = jsonObject.getString("status")
                val reason = jsonObject.getString("reason")
                if (status == "success") {
                    Toast.makeText(context, "Record inserted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle the case where the record insertion fails
                    Toast.makeText(context, "Record insertion failed: $reason", Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        },
        Response.ErrorListener { error ->
            // Handle error response from the server
            Toast.makeText(context, "Error: ${error.toString().trim()}", Toast.LENGTH_SHORT).show()
        }) {
        @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val data = HashMap<String, String>()
            data["email"] = email
            data["error_count"] = noneGestureCount.toString()
            return data
        }
    }

    val requestQueue = Volley.newRequestQueue(context)
    requestQueue.add(stringRequest)
}

class GestureRecognizerResultsAdapter :
    RecyclerView.Adapter<GestureRecognizerResultsAdapter.ViewHolder>() {
    companion object {
        private const val NO_VALUE = "--"
    }

    private var adapterCategories: MutableList<Category?> = mutableListOf()
    private var adapterSize: Int = 0
    private var noneGestureCount: Int = 0
    private var lastNoneGestureCount: Int = 0

    @SuppressLint("NotifyDataSetChanged")
    fun updateResults(categories: List<Category>?) {
        adapterCategories = MutableList(adapterSize) { null }
        if (categories != null) {
            val sortedCategories = categories.sortedByDescending { it.score() }
            val min = min(sortedCategories.size, adapterCategories.size)
            for (i in 0 until min) {
                adapterCategories[i] = sortedCategories[i]
            }
            adapterCategories = adapterCategories.sortedBy { it?.index() }.toMutableList()

            // 更新最後一次的 "none" 手勢次數
            lastNoneGestureCount = categories.count { it.categoryName()?.toLowerCase() == "none" }
            notifyDataSetChanged()
        }
    }

    // 新增一個方法用於取得最後一次偵測到的 "none" 手勢次數
    fun getLastNoneGestureCount(): Int {
        return lastNoneGestureCount
    }

    fun updateAdapterSize(size: Int) {
        adapterSize = size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemGestureRecognizerResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        adapterCategories[position].let { category ->
            holder.bind(category?.categoryName(), category?.score())
        }
    }

    override fun getItemCount(): Int = adapterCategories.size

    inner class ViewHolder(private val binding: ItemGestureRecognizerResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var mediaPlayer: MediaPlayer?

        init {
            mediaPlayer = MediaPlayer.create(binding.root.context, R.raw.warning_sound)
        }

        fun bind(label: String?, score: Float?) {
            with(binding) {
                tvLabel.text = label ?: NO_VALUE
                tvScore.text = if (score != null) String.format(
                    Locale.US,
                    "%.2f",
                    score
                ) else NO_VALUE
            }
            if (label?.toLowerCase() == "none") {
                playWarningSound()
                // 當手勢為 "none" 時，增加 "none" 手勢的次數
                noneGestureCount++
                playWarningSound()

                // 調用新添加的方法將最後一次 "none" 手勢次數發送到資料庫
                sendNoneGestureCountToDatabase(binding.root.context, noneGestureCount)
            } else {
                stopWarningSound()
            }
        }

        // 新增一個方法用於獲取手勢為 "none" 的次數
        fun getNoneGestureCount(): Int {
            return noneGestureCount
        }

        // 新增一個方法用於轉換統計信息為 JSON 格式
        fun getStatisticsAsJson(): String {
            val statistics = mapOf("noneGestureCount" to noneGestureCount)
            return Gson().toJson(statistics)
        }


        private fun playWarningSound() {
            if (mediaPlayer != null) {
                if (!mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.start()
                }
            }
        }

        private fun stopWarningSound() {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
            }
        }
    }
}