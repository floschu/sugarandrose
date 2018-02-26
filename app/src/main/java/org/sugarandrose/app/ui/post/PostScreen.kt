package org.sugarandrose.app.ui.post

import android.annotation.SuppressLint
import android.databinding.Bindable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
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
import android.webkit.WebView
import android.content.Intent
import android.webkit.WebResourceRequest
import android.os.Build
import android.annotation.TargetApi
import android.webkit.WebViewClient
import io.reactivex.rxkotlin.addTo
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.util.WebManager
import timber.log.Timber


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface PostMvvm {

    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        fun init(id: Long)

        var css: String

        @get:Bindable
        var refreshing: Boolean
        @get:Bindable
        var post: LocalPost

        fun onRefresh()
        fun onMoreClick()
    }
}


class PostActivity : BaseActivity<ActivityPostBinding, PostMvvm.ViewModel>(), PostMvvm.View {
    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var webManager: WebManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityComponent.inject(this)
        WebView.enableSlowWholeDocumentDraw()
        setAndBindContentView(savedInstanceState, R.layout.activity_post)

        setSupportActionBar(binding.includeToolbar?.toolbar)
        supportActionBar?.let {
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        initWebView()

        val id = intent.getLongExtra(Navigator.EXTRA_ARG, -1)
        if (id < 0) finish()
        else viewModel.init(id)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.webview.canGoBack()) binding.webview.goBack()
        else super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        binding.webview.onResume()
    }

    override fun onPause() {
        binding.webview.onPause()
        super.onPause()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        binding.webview.webViewClient = AppWebViewClient()
        binding.webview.settings.javaScriptEnabled = true
    }

    private inner class AppWebViewClient : WebViewClient() {

        @Suppress("OverridingDeprecatedMember")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            handleUri(Uri.parse(url))
            return true
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            handleUri(request.url)
            return true
        }

        private fun handleUri(uri: Uri) = when (uri.scheme) {
            "mailto" -> navigator.startActivity(Intent.ACTION_SENDTO, uri)
            "tel" -> navigator.startActivity(Intent.ACTION_DIAL, uri)
            else -> webManager.open(uri)

        }

        override fun onPageFinished(view: WebView, url: String) {
            viewModel.refreshing = false
            super.onPageFinished(view, url)
        }
    }
}


@PerActivity
class PostViewModel @Inject
constructor(private val api: SugarAndRoseApi, private val webManager: WebManager) : BaseViewModel<PostMvvm.View>(), PostMvvm.ViewModel {
    override var post: LocalPost by NotifyPropertyChangedDelegate(LocalPost(), BR.post)
    override var refreshing: Boolean by NotifyPropertyChangedDelegate(false, BR.refreshing)

    override var css = "<style>" +
            "img {" +
            "display: inline; " +
            "height: auto;max-width: 100%; " +
            "}" +
            "</style>"

    override fun init(id: Long) {
        if (refreshing) return
        api.getPost(id)
                .doOnSubscribe { refreshing = true }
                .map { LocalPost(it) }
                .doOnSuccess { it.content += "<br><br>" }
                .subscribe({ post = it }, Timber::e)
                .addTo(disposable)
    }

    override fun onRefresh() = init(post.id)

    override fun onMoreClick() = webManager.open(post.url)
}