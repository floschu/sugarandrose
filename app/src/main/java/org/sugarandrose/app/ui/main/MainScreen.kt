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
import android.content.res.Resources
import android.databinding.Bindable
import android.net.Uri
import android.os.Bundle
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.remote.Post
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.databinding.ActivityMainBinding
import org.sugarandrose.app.injection.qualifier.ActivityDisposable
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.post.PostActivity
import org.sugarandrose.app.ui.roses.RosesCacheManager
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import timber.log.Timber
import javax.inject.Inject


interface MainMvvm {

    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        fun parseIntentUri(uri: Uri)
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
            //setCustomAnimations(R.anim.fade_in, R.anim.fade_out) todo enable animations when lib is fixed
            attachTo(binding.bottomNavigationView)
        }

        intent.data?.let { viewModel.parseIntentUri(it) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let { viewModel.parseIntentUri(it) }
    }

    override fun onResume() {
        super.onResume()
        rosesCacheManager.checkReloadData().addTo(disposable)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        adapter.onSaveInstanceState(outState)
    }
}


@PerActivity
class MainViewModel @Inject
constructor(private val navigator: Navigator, private val resources: Resources) : BaseViewModel<MainMvvm.View>(), MainMvvm.ViewModel {

    override fun parseIntentUri(uri: Uri) {
        if (uri.lastPathSegment == resources.getString(R.string.deeplink_roses).replace("/", ""))
        //todo switch to roses fragment
        else if (uri.path.contains(Regex("""2[0-9]{3}/[0-9]{2}/[0-9]{2}/*/""")))
            navigator.startActivity(PostActivity::class.java, { putExtra(Navigator.EXTRA_ARG, uri.path) })
    }
}