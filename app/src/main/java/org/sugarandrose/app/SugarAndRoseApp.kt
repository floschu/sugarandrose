package org.sugarandrose.app

import android.content.res.Resources
import android.support.multidex.MultiDexApplication
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


/* Copyright 2016 Patrick Löwenstein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */

class SugarAndRoseApp : MultiDexApplication() {
    private val CACHE_SIZE = (50 * 1024 * 1024).toLong() // 50 MB

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) return

        Timber.plant(Timber.DebugTree())

        SugarAndRoseApp.instance = this
        SugarAndRoseApp.appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        Realm.init(this)
        RxJavaPlugins.setErrorHandler({ Timber.e(it) })
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

        lateinit var instance: SugarAndRoseApp
            private set

        lateinit var appComponent: AppComponent
            private set

        val realm: Realm
            get() = SugarAndRoseApp.appComponent.realm()

        val res: Resources
            get() = SugarAndRoseApp.instance.resources
    }
}
