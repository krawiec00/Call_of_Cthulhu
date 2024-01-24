package com.app.callofcthulhu.services

import com.app.callofcthulhu.model.data.Notification
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("send-notification")
    fun sendNotification(@Body notificationData: Notification): Call<ResponseBody>
}