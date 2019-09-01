package org.sugarandrose.app

import android.app.Application
import android.content.res.Resources
import com.google.firebase.messaging.FirebaseMessaging
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import io.reactivex.plugins.RxJavaPlugins
import io.realm.Realm
import org.sugarandrose.app.injection.components.AppComponent
import org.sugarandrose.app.injection.components.DaggerAppComponent
import org.sugarandrose.app.injection.modules.AppModule
import timber.log.Timber

class SugarAndRoseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) return

        instance = this

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

        Timber.plant(Timber.DebugTree())
        Realm.init(this)
        RxJavaPlugins.setErrorHandler(Timber::e)
        AndroidThreeTen.init(this)
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/all")
    }

    companion object {
        lateinit var instance: SugarAndRoseApp
            private set

        lateinit var appComponent: AppComponent
            private set

        val realm: Realm
            get() = appComponent.realm()

        val res: Resources
            get() = instance.resources
    }
}
