package com.app.callofcthulhu.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.callofcthulhu.R
import com.app.callofcthulhu.view.share.ShareNotificationActivity
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

        // Utwórz Intencję, która otworzy odpowiednią aktywność po kliknięciu powiadomienia
        val intent = Intent(this, ShareNotificationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.cthluhlu_logo) // Ustaw ikonę powiadomienia
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // Ustaw Intencję na powiadomienie

        notificationManager.notify(0, notificationBuilder.build())
    }
}


