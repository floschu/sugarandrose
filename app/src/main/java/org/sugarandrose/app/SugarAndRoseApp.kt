package org.sugarandrose.app

import android.app.Application
import android.content.res.Resources
import com.google.firebase.messaging.FirebaseMessaging
import com.jakewharton.picasso.OkHttp3Downloader
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import com.squareup.picasso.Picasso
import io.reactivex.plugins.RxJavaPlugins
import io.realm.Realm
import okhttp3.Cache
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
        setupPicasso()
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/all")
    }

    private fun setupPicasso() {
        Picasso.setSingletonInstance(Picasso.Builder(instance)
            .downloader(OkHttp3Downloader(appComponent.okHttpClient()
                .newBuilder()
                .cache(Cache(instance.cacheDir, CACHE_SIZE))
                .build()
            ))
            .loggingEnabled(BuildConfig.DEBUG)
            .build()
        )
    }

    companion object {
        private const val CACHE_SIZE = (50 * 1024 * 1024).toLong() // 50 MB

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
