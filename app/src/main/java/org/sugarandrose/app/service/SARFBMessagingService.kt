package org.sugarandrose.app.service


import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.sugarandrose.app.SugarAndRoseApp
import timber.log.Timber


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class SARFBMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("Firebase Message received: ${remoteMessage.data}")

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body
        if (title == null || body == null) return

        SugarAndRoseApp.appComponent.notificationManager().let {
            when {
                isNewPostPush(title, body) -> it.pushNewPost(body)
                else -> it.pushRemoteMessage(title, body)
            }
        }
    }

    private fun isNewPostPush(title: String, body: String) =
            title == "new_post" && body.matches(Regex("""[0-9]+"""))
}

