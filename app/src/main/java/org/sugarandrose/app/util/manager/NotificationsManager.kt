package org.sugarandrose.app.util.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import org.sugarandrose.app.R
import org.sugarandrose.app.injection.qualifier.AppContext
import org.sugarandrose.app.injection.scopes.PerApplication
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerApplication
class NotificationsManager @Inject
constructor(@AppContext private val context: Context) {

    companion object {
        const val REMOTE_PUSH_ID = 381636
        const val REMOTE_PUSH_CHANNEL_ID = "org.sugarandrose.app.util.channel.firebase_push"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) initChannels()
    }

    fun pushRemote(intent: Intent, title: String, message: String, subText: String? = null) {
        pushSimpleMessage(REMOTE_PUSH_CHANNEL_ID, intent, title, message, subText)
    }

    private fun pushSimpleMessage(channelId: String, intent: Intent, title: String, message: String, subText: String? = null) {
        val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentTitle(title)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setShowWhen(true)
                .setAutoCancel(true)
        if (subText != null) notification.setSubText(subText)
        NotificationManagerCompat.from(context).notify(REMOTE_PUSH_ID, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initChannels() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pushChannel = NotificationChannel(
                REMOTE_PUSH_CHANNEL_ID,
                context.getString(R.string.notification_channel_fb_name),
                NotificationManager.IMPORTANCE_DEFAULT
        )
        pushChannel.apply {
            lightColor = Color.MAGENTA
            enableVibration(true)
            enableLights(true)
        }
        notificationManager.createNotificationChannel(pushChannel)
    }
}