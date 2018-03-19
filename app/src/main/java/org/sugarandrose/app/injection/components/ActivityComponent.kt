package org.sugarandrose.app.injection.components

import org.sugarandrose.app.ui.onboarding.OnboardingActivity
import android.content.Context
import android.support.v4.app.FragmentManager
import dagger.Component
import io.reactivex.disposables.CompositeDisposable
import org.sugarandrose.app.injection.modules.ActivityModule
import org.sugarandrose.app.injection.modules.ViewModelModule
import org.sugarandrose.app.injection.qualifier.ActivityContext
import org.sugarandrose.app.injection.qualifier.ActivityDisposable
import org.sugarandrose.app.injection.qualifier.ActivityFragmentManager
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.feedback.Snacker
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.categories.detail.CategoryDetailActivity
import org.sugarandrose.app.ui.main.MainActivity
import org.sugarandrose.app.ui.photo.PhotoDetailActivity
import org.sugarandrose.app.ui.post.PostActivity
import org.sugarandrose.app.util.manager.ShareManager
import org.sugarandrose.app.util.manager.SocialMediaManager
import org.sugarandrose.app.util.manager.WebManager

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
@Component(dependencies = [(AppComponent::class)], modules = [(ActivityModule::class), (ViewModelModule::class)])
interface ActivityComponent : ActivityComponentProvides {
    // create inject methods for your Activities here
    fun inject(activity: OnboardingActivity)

    fun inject(activity: MainActivity)
    fun inject(activity: PostActivity)
    fun inject(activity: PhotoDetailActivity)
    fun inject(activity: CategoryDetailActivity)

}

interface ActivityComponentProvides : AppComponentProvides {
    @ActivityContext
    fun activityContext(): Context

    @ActivityDisposable
    fun activityDisposable(): CompositeDisposable

    @ActivityFragmentManager
    fun defaultFragmentManager(): FragmentManager

    fun navigator(): Navigator
    fun snacker(): Snacker

    fun webManager(): WebManager
    fun shareManager(): ShareManager
    fun socialMediaManager(): SocialMediaManager
}