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
import timber.log.Timber


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface PostMvvm {

    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        @get:Bindable
        var refreshing: Boolean

        fun onRefresh()

        @get:Bindable
        var title: String?
        @get:Bindable
        var url: String?
    }
}


class PostActivity : BaseActivity<ActivityPostBinding, PostMvvm.ViewModel>(), PostMvvm.View {
    @Inject
    lateinit var navigator: Navigator

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

        viewModel.url = intent.getParcelableExtra<LocalPost>(Navigator.EXTRA_ARG).url

        initWebView()
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
        binding.webview.webChromeClient = AppWebChromeClient()
        binding.webview.settings.javaScriptEnabled = true
    }

    private inner class AppWebViewClient : WebViewClient() {

        @Suppress("OverridingDeprecatedMember")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            val uri = Uri.parse(url)
            val handled = handleUri(uri)
            if (!handled) view.loadUrl(url)
            return true
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val uri = request.url
            val handled = handleUri(uri)
            if (!handled) view.loadUrl(uri.toString())
            return true
        }

        private fun handleUri(uri: Uri): Boolean = when (uri.scheme) {
            "mailto" -> {
                navigator.startActivity(Intent.ACTION_SENDTO, uri)
                true
            }
            "tel" -> {
                navigator.startActivity(Intent.ACTION_DIAL, uri)
                true
            }
            else -> false
        }

        override fun onPageFinished(view: WebView, url: String) {
            viewModel.refreshing = false
            super.onPageFinished(view, url)
        }
    }

    private inner class AppWebChromeClient : WebChromeClient() {

        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            viewModel.title = title
        }
    }
}


@PerActivity
class PostViewModel @Inject
constructor() : BaseViewModel<PostMvvm.View>(), PostMvvm.ViewModel {
    override var refreshing: Boolean by NotifyPropertyChangedDelegate(true, BR.refreshing)
    override var title: String? by NotifyPropertyChangedDelegate(null, BR.title)
    override var url: String? by NotifyPropertyChangedDelegate(null, BR.url)

    override fun onRefresh() {
        refreshing = true
        notifyPropertyChanged(BR.url)
    }
}