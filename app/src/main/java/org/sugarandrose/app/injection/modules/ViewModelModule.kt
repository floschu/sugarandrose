package org.sugarandrose.app.injection.modules


import org.sugarandrose.app.ui.main.MainMvvm
import org.sugarandrose.app.ui.main.MainViewModel

import dagger.Binds
import dagger.Module
import org.sugarandrose.app.ui.categories.detail.CategoryDetailMvvm
import org.sugarandrose.app.ui.categories.detail.CategoryDetailViewModel
import org.sugarandrose.app.ui.favorited.FavoritedMvvm
import org.sugarandrose.app.ui.favorited.FavoritedViewModel
import org.sugarandrose.app.ui.more.MoreMvvm
import org.sugarandrose.app.ui.more.MoreViewModel
import org.sugarandrose.app.ui.more.recyclerview.MoreItemMvvm
import org.sugarandrose.app.ui.more.recyclerview.MoreItemViewModel
import org.sugarandrose.app.ui.categories.overview.CategoriesMvvm
import org.sugarandrose.app.ui.categories.overview.CategoriesViewModel
import org.sugarandrose.app.ui.categories.overview.recyclerview.CategoryItemMvvm
import org.sugarandrose.app.ui.categories.overview.recyclerview.CategoryItemViewModel
import org.sugarandrose.app.ui.home.HomeMvvm
import org.sugarandrose.app.ui.home.HomeViewModel
import org.sugarandrose.app.ui.displayitems.PostItemMvvm
import org.sugarandrose.app.ui.displayitems.PostItemViewModel
import org.sugarandrose.app.ui.news.NewMvvm
import org.sugarandrose.app.ui.news.NewViewModel
import org.sugarandrose.app.ui.displayitems.MediaItemMvvm
import org.sugarandrose.app.ui.displayitems.MediaItemViewModel
import org.sugarandrose.app.ui.post.PostMvvm
import org.sugarandrose.app.ui.post.PostViewModel
import org.sugarandrose.app.ui.roses.RosesMvvm
import org.sugarandrose.app.ui.roses.RosesViewModel
import org.sugarandrose.app.ui.displayitems.RoseItemMvvm
import org.sugarandrose.app.ui.displayitems.RoseItemViewModel
import org.sugarandrose.app.ui.more.recyclerview.MorePageItemMvvm
import org.sugarandrose.app.ui.more.recyclerview.MorePageItemViewModel
import org.sugarandrose.app.ui.search.SearchMvvm
import org.sugarandrose.app.ui.search.SearchViewModel
import org.sugarandrose.app.ui.textsearch.TextSearchMvvm
import org.sugarandrose.app.ui.textsearch.TextSearchViewModel

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
    internal abstract fun bindTestViewModel(viewModel: MoreViewModel): MoreMvvm.ViewModel

    @Binds
    internal abstract fun bindCategoriesViewModel(viewModel: CategoriesViewModel): CategoriesMvvm.ViewModel

    @Binds
    internal abstract fun bindCategoryItemViewModel(viewModel: CategoryItemViewModel): CategoryItemMvvm.ViewModel

    @Binds
    internal abstract fun bindMoreItemViewModel(viewModel: MoreItemViewModel): MoreItemMvvm.ViewModel

    @Binds
    internal abstract fun bindFavoritedViewModel(viewModel: FavoritedViewModel): FavoritedMvvm.ViewModel

    @Binds
    internal abstract fun bindMediaItemViewModel(viewModel: MediaItemViewModel): MediaItemMvvm.ViewModel

    @Binds
    internal abstract fun bindSearchViewModel(viewModel: SearchViewModel): SearchMvvm.ViewModel

    @Binds
    internal abstract fun bindTextSearchViewModel(viewModel: TextSearchViewModel): TextSearchMvvm.ViewModel

    @Binds
    internal abstract fun bindCategoryDetailViewModel(viewModel: CategoryDetailViewModel): CategoryDetailMvvm.ViewModel

    @Binds
    internal abstract fun bindHomeViewModel(viewModel: HomeViewModel): HomeMvvm.ViewModel

    @Binds
    internal abstract fun bindRosesViewModel(viewModel: RosesViewModel): RosesMvvm.ViewModel

    @Binds
    internal abstract fun bindRoseItemViewModel(viewModel: RoseItemViewModel): RoseItemMvvm.ViewModel

    @Binds
    internal abstract fun bindMorePageItemViewModel(viewModel: MorePageItemViewModel): MorePageItemMvvm.ViewModel

}
