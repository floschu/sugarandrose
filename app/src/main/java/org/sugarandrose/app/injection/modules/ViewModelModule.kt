package org.sugarandrose.app.injection.modules


import org.sugarandrose.app.ui.main.MainMvvm
import org.sugarandrose.app.ui.main.MainViewModel

import dagger.Binds
import dagger.Module
import org.sugarandrose.app.ui.calendar.CalendarMvvm
import org.sugarandrose.app.ui.calendar.CalendarViewModel
import org.sugarandrose.app.ui.categories.CategoriesMvvm
import org.sugarandrose.app.ui.categories.CategoriesViewModel
import org.sugarandrose.app.ui.categories.recyclerview.CategoryItemMvvm
import org.sugarandrose.app.ui.categories.recyclerview.CategoryItemViewModel
import org.sugarandrose.app.ui.news.recyclerview.PostItemMvvm
import org.sugarandrose.app.ui.news.recyclerview.PostItemViewModel
import org.sugarandrose.app.ui.news.NewMvvm
import org.sugarandrose.app.ui.news.NewViewModel
import org.sugarandrose.app.ui.post.PostMvvm
import org.sugarandrose.app.ui.post.PostViewModel
import org.sugarandrose.app.ui.test.TestMvvm
import org.sugarandrose.app.ui.test.TestViewModel

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
@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindMainViewModel(viewModel: MainViewModel): MainMvvm.ViewModel

    @Binds
    internal abstract fun bindPostViewModel(viewModel: PostViewModel): PostMvvm.ViewModel

    @Binds
    internal abstract fun bindNewViewModel(viewModel: NewViewModel): NewMvvm.ViewModel

    @Binds
    internal abstract fun bindPostItemViewModel(viewModel: PostItemViewModel): PostItemMvvm.ViewModel

    @Binds
    internal abstract fun bindCalendarViewModel(viewModel: CalendarViewModel): CalendarMvvm.ViewModel

    @Binds
    internal abstract fun bindTestViewModel(viewModel: TestViewModel): TestMvvm.ViewModel

    @Binds
    internal abstract fun bindCategoriesViewModel(viewModel: CategoriesViewModel): CategoriesMvvm.ViewModel

    @Binds
    internal abstract fun bindCategoryItemViewModel(viewModel: CategoryItemViewModel): CategoryItemMvvm.ViewModel

}
