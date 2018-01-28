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

        val body = remoteMessage.notification?.body ?: return

        SugarAndRoseApp.appComponent.notificationManager().pushRemote(
                Intent(SugarAndRoseApp.instance, MainActivity::class.java),
                remoteMessage.notification?.title ?: getString(R.string.app_name),
                body
        )
    }
}
