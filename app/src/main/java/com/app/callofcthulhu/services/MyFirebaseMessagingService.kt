package com.app.callofcthulhu.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.callofcthulhu.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Logika obsługi przychodzącego powiadomienia
        if (remoteMessage.notification != null) {
            // Wyświetl powiadomienie
            showNotification(remoteMessage.notification!!.title, remoteMessage.notification!!.body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Logika obsługi nowego tokena, np. wyślij go na serwer
        Log.d("FCM", "Nowy token: $token")
    }

    private fun showNotification(title: String?, body: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelId = "fcm_channel_id"

        // Tworzenie kanału powiadomień dla Android 8.0 (Oreo) i nowszych
        val notificationChannel = NotificationChannel(notificationChannelId, "Powiadomienia FCM", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(notificationChannel)

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.baseline_add_24) // Ustaw ikonę powiadomienia
            .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }

}
