package org.sugarandrose.app.injection.components

import dagger.Component
import org.sugarandrose.app.injection.modules.ViewHolderModule
import org.sugarandrose.app.injection.modules.ViewModelModule
import org.sugarandrose.app.injection.scopes.PerViewHolder
import org.sugarandrose.app.ui.more.recyclerview.MoreHeaderItemViewHolder
import org.sugarandrose.app.ui.more.recyclerview.MoreItemViewHolder
import org.sugarandrose.app.ui.more.recyclerview.MorePageItemViewHolder

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
@Component(dependencies = arrayOf(FragmentComponent::class), modules = arrayOf(ViewHolderModule::class, ViewModelModule::class))
interface FragmentViewHolderComponent {
    fun inject(vh: MoreItemViewHolder)
    fun inject(vh: MoreHeaderItemViewHolder)
    fun inject(vh: MorePageItemViewHolder)
}