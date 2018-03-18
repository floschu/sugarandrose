package org.sugarandrose.app.service


import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.sugarandrose.app.R
import org.sugarandrose.app.SugarAndRoseApp
import org.sugarandrose.app.ui.main.MainActivity
import timber.log.Timber


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class SARFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("Firebase Message received: ${remoteMessage.data}")

        val intent = Intent(SugarAndRoseApp.instance, MainActivity::class.java)
        val title = remoteMessage.notification?.title ?: getString(R.string.name)
        val body = remoteMessage.notification?.body ?: getString(R.string.notification_new_post)

        SugarAndRoseApp.appComponent.notificationManager().pushRemote(intent, title, body)
    }
}
