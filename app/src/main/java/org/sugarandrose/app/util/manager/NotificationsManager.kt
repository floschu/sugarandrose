package org.sugarandrose.app.util.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import org.sugarandrose.app.R
import org.sugarandrose.app.SugarAndRoseApp
import org.sugarandrose.app.injection.qualifier.AppContext
import org.sugarandrose.app.injection.scopes.PerApplication
import org.sugarandrose.app.ui.main.MainActivity
import org.sugarandrose.app.ui.main.PUSH_POST_ID_INTENT
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerApplication
class NotificationsManager @Inject
constructor(@AppContext private val context: Context, private val resources: Resources) {

    companion object {
        const val REMOTE_MESSAGE_PUSH_CHANNEL_ID = "org.sugarandrose.app.util.channel.firebase_push_remote"
        const val REMOTE_MESSAGE_PUSH_ID = 381636

        const val NEW_POST_PUSH_CHANNEL_ID = "org.sugarandrose.app.util.channel.firebase_push_post"
        const val NEW_POST_PUSH_ID = 381637
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) initChannels()
    }

    fun pushRemoteMessage(title: String, message: String) {
        val intent = Intent(SugarAndRoseApp.instance, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        pushSimpleMessage(
                NEW_POST_PUSH_CHANNEL_ID,
                NEW_POST_PUSH_ID,
                intent,
                title,
                message
        )
    }

    fun pushNewPost(postId: String) {
        val intent = Intent(SugarAndRoseApp.instance, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(PUSH_POST_ID_INTENT, postId)
        }

        pushSimpleMessage(
                REMOTE_MESSAGE_PUSH_CHANNEL_ID,
                REMOTE_MESSAGE_PUSH_ID,
                intent,
                resources.getString(R.string.notification_new_post_title),
                resources.getString(R.string.notification_new_post_message)
        )
    }

    private fun pushSimpleMessage(channelId: String, pushId: Int, intent: Intent, title: String, message: String) {
        val notification = NotificationCompat.Builder(context, channelId).apply {
            color = ContextCompat.getColor(context, R.color.colorAccent)
            setSmallIcon(R.drawable.ic_rose)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setContentIntent(PendingIntent.getActivity(context, pushId, intent, PendingIntent.FLAG_UPDATE_CURRENT))
            setContentTitle(title)
            setContentText(message)
            setShowWhen(true)
            setAutoCancel(true)
        }.build()
        NotificationManagerCompat.from(context).notify(pushId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initChannels() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelRemote = NotificationChannel(
                REMOTE_MESSAGE_PUSH_CHANNEL_ID,
                context.getString(R.string.fb_channel_remote),
                NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            lightColor = Color.MAGENTA
            enableVibration(true)
            enableLights(true)
        }

        val channelPosts = NotificationChannel(
                NEW_POST_PUSH_CHANNEL_ID,
                context.getString(R.string.fb_channel_posts),
                NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            lightColor = Color.MAGENTA
            enableVibration(true)
            enableLights(true)
        }

        notificationManager.createNotificationChannel(channelRemote)
        notificationManager.createNotificationChannel(channelPosts)
    }
}