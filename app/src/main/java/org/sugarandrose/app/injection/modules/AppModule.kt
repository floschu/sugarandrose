package org.sugarandrose.app.injection.modules

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration
import org.sugarandrose.app.injection.qualifier.AppContext
import org.sugarandrose.app.injection.scopes.PerApplication
import org.sugarandrose.app.ui.base.feedback.ApplicationToaster
import org.sugarandrose.app.ui.base.feedback.Toaster

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
 * limitations under the License.
 *
 * ------
 *
 * FILE MODIFIED 2017 Tailored Media GmbH */
@Module
class AppModule(private val app: Application) {
    private val realmFileName = "sugarandrose"
    private val realmVersion: Long = 1

    @Provides
    @PerApplication
    @AppContext
    internal fun provideAppContext(): Context = app

    @Provides
    @PerApplication
    internal fun provideResources(): Resources = app.resources

    @Provides
    @PerApplication
    internal fun provideRefWatcher(): RefWatcher = LeakCanary.install(app)

    @Provides
    internal fun provideRealm(): Realm {
        val config = RealmConfiguration.Builder().apply {
            name(realmFileName)
            schemaVersion(realmVersion)
            deleteRealmIfMigrationNeeded()
        }.build()
        return Realm.getInstance(config)
    }

    @Provides
    @PerApplication
    internal fun provideToaster(): Toaster = ApplicationToaster(app)
}
