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

package org.sugarandrose.app.ui.main

import android.databinding.Bindable
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.databinding.ActivityMainBinding
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import timber.log.Timber
import javax.inject.Inject
import android.support.design.widget.AppBarLayout
import org.sugarandrose.app.ui.main.recyclerview.PostAdapter


interface MainMvvm {

    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        fun onRefresh()

        val adapter: PostAdapter

        @get:Bindable
        val refreshing: Boolean
    }
}


class MainActivity : BaseActivity<ActivityMainBinding, MainMvvm.ViewModel>(), MainMvvm.View {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityComponent.inject(this)
        setAndBindContentView(savedInstanceState, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)

        binding.appbar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            internal var isShow = false
            internal var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.toolbarTitle.text = string(R.string.name)
                    isShow = true
                } else if (isShow) {
                    binding.toolbarTitle.text = ""
                    isShow = false
                }
            }
        })
    }

}


@PerActivity
class MainViewModel @Inject
constructor(private val api: SugarAndRoseApi, override val adapter: PostAdapter) : BaseViewModel<MainMvvm.View>(), MainMvvm.ViewModel {

    override var refreshing: Boolean by NotifyPropertyChangedDelegate(false, BR.refreshing)

    override fun attachView(view: MainMvvm.View, savedInstanceState: Bundle?) {
        super.attachView(view, savedInstanceState)
        onRefresh()
    }

    override fun onRefresh() {
        refreshing = true
        api.getPosts().flattenAsFlowable { it }
                .flatMapSingle { post -> api.getMedia(post.featured_media).map { LocalPost(post, it) } }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ adapter.data = it.sortedByDescending { it.date }; refreshing = false }, Timber::e).let { disposable.add(it) }
    }
}