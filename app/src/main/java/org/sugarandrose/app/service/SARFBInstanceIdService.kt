package org.sugarandrose.app.service

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import timber.log.Timber


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class SARFBInstanceIdService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        Timber.i("Refreshed token: ${FirebaseInstanceId.getInstance().token}")
    }
}
