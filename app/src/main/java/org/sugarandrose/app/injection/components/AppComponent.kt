package org.sugarandrose.app.injection.components

import android.content.Context
import android.content.res.Resources
import com.squareup.leakcanary.RefWatcher
import org.sugarandrose.app.data.local.FavoritedRepo
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.injection.modules.AppModule
import org.sugarandrose.app.injection.modules.DataModule
import org.sugarandrose.app.injection.modules.NetModule
import org.sugarandrose.app.injection.qualifier.AppContext
import org.sugarandrose.app.injection.scopes.PerApplication
import org.sugarandrose.app.ui.base.feedback.Toaster
import dagger.Component
import io.realm.Realm
import okhttp3.OkHttpClient
import org.sugarandrose.app.ui.categories.CategoriesCacheManager
import org.sugarandrose.app.ui.more.MoreCacheManager
import org.sugarandrose.app.ui.roses.RosesCacheManager
import org.sugarandrose.app.util.manager.*

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
@PerApplication
@Component(modules = arrayOf(AppModule::class, NetModule::class, DataModule::class))
interface AppComponent : AppComponentProvides {

}

interface AppComponentProvides {
    @AppContext
    fun appContext(): Context

    fun resources(): Resources
    fun refWatcher(): RefWatcher

    fun encryptionKeyManager(): org.sugarandrose.app.data.local.encryption.EncryptionKeyManager

    fun realm(): Realm
    fun myRepo(): FavoritedRepo
    fun sugarAndRoseApi(): SugarAndRoseApi

    fun okHttpClient(): OkHttpClient

    fun toaster(): Toaster

    fun notificationManager(): NotificationsManager
    fun eventLogManager(): EventLogManager
    fun webManager(): WebManager

    fun rosesCacheManager(): RosesCacheManager
    fun moreCacheManager(): MoreCacheManager
    fun categoriesCacheManager(): CategoriesCacheManager
    fun errorManager(): ErrorManager
}