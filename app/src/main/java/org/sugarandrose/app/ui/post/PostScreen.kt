package org.sugarandrose.app.ui.post

import android.annotation.SuppressLint
import android.databinding.Bindable
import android.net.Uri
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
import android.webkit.WebView
import android.content.Intent
import android.webkit.WebResourceRequest
import android.os.Build
import android.annotation.TargetApi
import android.content.res.Resources
import android.net.http.SslError
import android.view.View
import android.webkit.WebViewClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.util.extensions.getColorHex
import org.sugarandrose.app.util.manager.WebManager
import timber.log.Timber


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface PostMvvm {

    interface View : MvvmView {
        fun loadContent(content: String)
    }

    interface ViewModel : MvvmViewModel<View> {
        fun init(id: Long)

        @get:Bindable
        var loading: Boolean
        @get:Bindable
        var post: LocalPost

        fun onMoreClick()

        fun onWebViewError()
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
        binding.webview.webChromeClient = WebChromeClient()
        binding.webview.webViewClient = AppWebViewClient()
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        binding.webview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        binding.webview.settings.defaultFontSize = resources.getDimension(R.dimen.text_size_regular).toInt()
    }

    override fun loadContent(content: String) = binding.webview.loadData(content, "text/html; charset=UTF-8", null)

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
            viewModel.loading = false
            super.onPageFinished(view, url)
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            Timber.e(error.toString())
            viewModel.onWebViewError()
        }

        override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
            Timber.e(errorResponse.toString())
            viewModel.onWebViewError()
        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            Timber.e(error.toString())
            viewModel.onWebViewError()
        }
    }
}


@PerActivity
class PostViewModel @Inject
constructor(private val api: SugarAndRoseApi,
            private val webManager: WebManager,
            private val navigator: Navigator,
            resources: Resources
) : BaseViewModel<PostMvvm.View>(), PostMvvm.ViewModel {
    override var post: LocalPost by NotifyPropertyChangedDelegate(LocalPost(), BR.post)
    override var loading: Boolean by NotifyPropertyChangedDelegate(false, BR.loading)

    private var header =
//            "<link rel=\"stylesheet\" type=\"text/header\" href=\"//fonts.googleapis.com/header?family=Oswald\" />" +
                    "<style type=\"text/css\">" +
                    "@font-face {" +
                    "font-family: Oswald;" +
                    "src: url(\"file:///android_asset/fonts/oswald.ttf\")" +
                    "}" +
                    "body {" +
                    "padding-bottom: 50px;" +
                    "}" +
                    "h1 {" +
                    "font-family: Oswald;" +
                    "}" +
                    "h2 {" +
                    "color: ${resources.getColorHex(R.color.textBlackSecondary)};" +
                    "font-family: Oswald;" +
                    "}" +
                    "h3 {" +
                    "color: ${resources.getColorHex(R.color.textBlackSecondary)};" +
                    "font-family: Oswald;" +
                    "}" +
                    "p {" +
                    "color: ${resources.getColorHex(R.color.textBlackPrimary)};" +
                    "}" +
                    "a {" +
                    "color: ${resources.getColorHex(R.color.colorAccent)};" +
                    "font-weight: bold;" +
                    "text-decoration: none;" +
                    "}" +
                    "img {" +
                    "display: inline;" +
                    "width: auto;" +
                    "height: auto;" +
                    "max-width: 100%;" +
                    "border-radius: 5px 5px 5px 5px;" +
                    "margin-bottom: 5px;" +
                    "}" +
                    "</style>"

    override fun init(id: Long) {
        api.getPost(id)
                .doOnSubscribe { loading = true }
                .map(::LocalPost)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPostSuccess, Timber::e)
                .addTo(disposable)
    }

    private fun onPostSuccess(post: LocalPost) {
        this.post = post.apply {
            content = "<html><head>$header</head><body>$content</body></html>"
        }
        this.post.content?.let { view?.loadContent(it) }
    }

    override fun onMoreClick() = webManager.open(post.url)

    override fun onWebViewError() {
        webManager.open(post.url)
        navigator.finishActivity()
    }
}