package org.sugarandrose.app.injection.modules

import org.sugarandrose.app.data.local.MyRepo
import org.sugarandrose.app.data.local.MyRepoImpl
import org.sugarandrose.app.data.local.PrefRepo
import org.sugarandrose.app.data.local.SharedPrefRepo

import dagger.Binds
import dagger.Module

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
abstract class DataModule {

    @Binds
    internal abstract fun bindMyRepo(repo: MyRepoImpl): MyRepo

    @Binds
    internal abstract fun bindPrefRepo(repo: SharedPrefRepo): PrefRepo

}