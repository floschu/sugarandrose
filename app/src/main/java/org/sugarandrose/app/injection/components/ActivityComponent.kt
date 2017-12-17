package org.sugarandrose.app.injection.components

import android.content.Context
import android.support.v4.app.FragmentManager
import org.sugarandrose.app.injection.modules.ActivityModule
import org.sugarandrose.app.injection.modules.ViewModelModule
import org.sugarandrose.app.injection.qualifier.ActivityContext
import org.sugarandrose.app.injection.qualifier.ActivityFragmentManager
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.feedback.Snacker
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.main.MainActivity
import dagger.Component
import org.sugarandrose.app.ui.post.PostActivity
import org.sugarandrose.app.util.WebManager

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
@PerActivity
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ActivityModule::class, ViewModelModule::class))
interface ActivityComponent : ActivityComponentProvides {
    // create inject methods for your Activities here

    fun inject(activity: MainActivity)
    fun inject(activity: PostActivity)

}

interface ActivityComponentProvides : AppComponentProvides {
    @ActivityContext fun activityContext(): Context
    @ActivityFragmentManager fun defaultFragmentManager(): FragmentManager
    fun navigator(): Navigator
    fun snacker(): Snacker
    fun webManager():WebManager
}