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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import io.reactivex.rxkotlin.addTo
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.ActivityMainBinding
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import javax.inject.Inject
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.post.PostActivity
import org.sugarandrose.app.ui.roses.RosesCacheManager


interface MainMvvm {

    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        fun openArticle(uri: Uri)
    }
}


class MainActivity : BaseActivity<ActivityMainBinding, MainMvvm.ViewModel>(), MainMvvm.View {
    private lateinit var adapter: MainAdapter
    @Inject
    lateinit var rosesCacheManager: RosesCacheManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndBindContentView(savedInstanceState, R.layout.activity_main)

        MainAdapter.disableShiftMode(binding.bottomNavigationView)
        adapter = MainAdapter(supportFragmentManager, R.id.container, R.id.bnv_new, savedInstanceState).apply {
            setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            attachTo(binding.bottomNavigationView)
        }

        intent.data?.let { viewModel.openArticle(it) }
    }


    override fun onResume() {
        super.onResume()
        rosesCacheManager.checkReloadData().addTo(disposable)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let { viewModel.openArticle(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        adapter.onSaveInstanceState(outState)
    }
}


@PerActivity
class MainViewModel @Inject
constructor(private val navigator: Navigator) : BaseViewModel<MainMvvm.View>(), MainMvvm.ViewModel {

    override fun openArticle(uri: Uri) = navigator.startActivity(PostActivity::class.java, { putExtra(Navigator.EXTRA_ARG, -1L) })
}