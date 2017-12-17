package org.sugarandrose.app.ui.post

import android.databinding.Bindable
import android.os.Bundle
import android.view.MenuItem
import android.webkit.*
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.databinding.ActivityPostBinding
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import javax.inject.Inject


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface PostMvvm {

    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {

        @get:Bindable
        var post: LocalPost?
        val client: WebChromeClient
    }
}


class PostActivity : BaseActivity<ActivityPostBinding, PostMvvm.ViewModel>(), PostMvvm.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityComponent.inject(this)
        setAndBindContentView(savedInstanceState, R.layout.activity_post)

        setSupportActionBar(binding.includeToolbar?.toolbar)
        supportActionBar?.let {
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        viewModel.post = intent.getParcelableExtra(Navigator.EXTRA_ARG)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.webview.canGoBack()) binding.webview.goBack()
        else super.onBackPressed()
    }
}


@PerActivity
class PostViewModel @Inject
constructor() : BaseViewModel<PostMvvm.View>(), PostMvvm.ViewModel {

    override var post: LocalPost? by NotifyPropertyChangedDelegate(null, BR.post)
    override val client = object : WebChromeClient() {}
}