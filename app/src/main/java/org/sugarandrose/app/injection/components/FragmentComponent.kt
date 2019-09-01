package org.sugarandrose.app.injection.components

import dagger.Component
import io.reactivex.disposables.CompositeDisposable
import org.sugarandrose.app.injection.modules.FragmentModule
import org.sugarandrose.app.injection.modules.ViewModelModule
import org.sugarandrose.app.injection.qualifier.FragmentDisposable
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.categories.overview.CategoriesFragment
import org.sugarandrose.app.ui.favorited.FavoritedFragment
import org.sugarandrose.app.ui.home.HomeFragment
import org.sugarandrose.app.ui.more.MoreFragment
import org.sugarandrose.app.ui.news.NewFragment
import org.sugarandrose.app.ui.roses.RosesFragment
import org.sugarandrose.app.ui.search.SearchFragment
import org.sugarandrose.app.ui.textsearch.TextSearchFragment

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
@PerFragment
@Component(dependencies = [(ActivityComponent::class)], modules = [(FragmentModule::class), (ViewModelModule::class)])
interface FragmentComponent : FragmentComponentProvides {
    // create inject methods for your Fragments here

    fun inject(fragment: NewFragment)
    fun inject(fragment: MoreFragment)
    fun inject(fragment: FavoritedFragment)
    fun inject(fragment: SearchFragment)
    fun inject(fragment: TextSearchFragment)
    fun inject(fragment: CategoriesFragment)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: RosesFragment)
}

interface FragmentComponentProvides : ActivityComponentProvides {
    @FragmentDisposable
    fun fragmentDisposable(): CompositeDisposable
}
