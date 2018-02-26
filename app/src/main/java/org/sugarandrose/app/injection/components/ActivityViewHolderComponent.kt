package org.sugarandrose.app.injection.components

import org.sugarandrose.app.injection.modules.ViewHolderModule
import org.sugarandrose.app.injection.modules.ViewModelModule
import org.sugarandrose.app.injection.scopes.PerViewHolder

import dagger.Component
import org.sugarandrose.app.ui.categories.recyclerview.CategoryItemViewHolder
import org.sugarandrose.app.ui.displayitems.LocalDisplayHeaderViewHolder
import org.sugarandrose.app.ui.displayitems.MediaItemViewHolder
import org.sugarandrose.app.ui.displayitems.PostItemViewHolder
import org.sugarandrose.app.ui.displayitems.RoseItemViewHolder

/* Copyright 2017 Tailored Media GmbH
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
@PerViewHolder
@Component(dependencies = arrayOf(ActivityComponent::class), modules = arrayOf(ViewHolderModule::class, ViewModelModule::class))
interface ActivityViewHolderComponent {
    fun inject(vh: PostItemViewHolder)
    fun inject(vh: MediaItemViewHolder)
    fun inject(vh: RoseItemViewHolder)
    fun inject(vh: LocalDisplayHeaderViewHolder)
    fun inject(vh: CategoryItemViewHolder)
}
