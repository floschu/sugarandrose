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
        Timber.d("Firebase Message received: " + remoteMessage.data)
        remoteMessage.notification?.body?.let {
            SugarAndRoseApp.appComponent.notificationManager().pushRemote(
                    Intent(SugarAndRoseApp.instance, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP },
                    remoteMessage.notification?.title ?: getString(R.string.app_name),
                    it

            )
        }
    }
}
