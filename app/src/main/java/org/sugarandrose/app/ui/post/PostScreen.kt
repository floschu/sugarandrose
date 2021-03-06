package org.sugarandrose.app.ui.post

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.Bindable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.databinding.ActivityPostBinding
import org.sugarandrose.app.injection.qualifier.ActivityContext
import org.sugarandrose.app.injection.qualifier.ActivityDisposable
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.feedback.Toaster
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import org.sugarandrose.app.util.extensions.getColorHex
import org.sugarandrose.app.util.extensions.hideStatusBar
import org.sugarandrose.app.util.extensions.showSystemUi
import org.sugarandrose.app.util.extensions.slideIn
import org.sugarandrose.app.util.extensions.slideOut
import org.sugarandrose.app.util.manager.ErrorManager
import org.sugarandrose.app.util.manager.WebManager
import org.sugarandrose.app.util.videowebview.VideoEnabledWebChromeClient
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
        fun init(path: String)

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

    private lateinit var webChromeClient: VideoEnabledWebChromeClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WebView.enableSlowWholeDocumentDraw()
        setAndBindContentView(savedInstanceState, R.layout.activity_post)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        initWebView()

        val id = intent.getLongExtra(Navigator.EXTRA_ARG, -1)
        val path = intent.getStringExtra(Navigator.EXTRA_ARG)
        when {
            id > 0 -> viewModel.init(id)
            path != null -> viewModel.init(path)
            else -> finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        binding.webview.onResume()
    }

    override fun onPause() {
        binding.webview.onPause()
        super.onPause()
    }

    override fun onBackPressed() {
        if (!webChromeClient.onBackPressed()) super.onBackPressed()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webChromeClient = VideoEnabledWebChromeClient(binding.videoHide, binding.videoFullscreen, null, binding.webview).apply {
            setOnToggledFullscreen {
                if (it) {
                    binding.cardMore.slideOut(250).subscribe().addTo(disposable)
                    hideStatusBar()
                    supportActionBar?.hide()
                } else {
                    binding.cardMore.slideIn(250).subscribe().addTo(disposable)
                    supportActionBar?.show()
                    showSystemUi()
                }
            }
        }
        binding.webview.webChromeClient = webChromeClient
        binding.webview.webViewClient = AppWebViewClient()
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
    }

    override fun loadContent(content: String) = binding.webview.loadDataWithBaseURL("file:///android_asset", content, "text/html; charset=UTF-8", "UTF-8", "")

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
            Timber.e("onReceivedError: $error")
            viewModel.onWebViewError()
        }

        override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
            Timber.e("onReceivedHttpError: $errorResponse")
            viewModel.onWebViewError()
        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            Timber.e("onReceivedSslError: $error")
            viewModel.onWebViewError()
        }
    }
}

@PerActivity
class PostViewModel @Inject
constructor(
    @ActivityDisposable private val disposable: CompositeDisposable,
    private val api: SugarAndRoseApi,
    private val webManager: WebManager,
    private val navigator: Navigator,
    private val errorManager: ErrorManager,
    private val toaster: Toaster,
    @ActivityContext context: Context
) : BaseViewModel<PostMvvm.View>(), PostMvvm.ViewModel {
    override var post: LocalPost by NotifyPropertyChangedDelegate(LocalPost(), BR.post)
    override var loading: Boolean by NotifyPropertyChangedDelegate(false, BR.loading)

    private var header =
//            "<link rel=\"stylesheet\" type=\"text/header\" href=\"//fonts.googleapis.com/header?family=Oswald\" />" +
        "<style type=\"text/css\">" +
            "@font-face {" +
            "font-family: Oswald;" +
            "src: url(\"file:///android_asset/oswald.ttf\")" +
            "}" +
            "body {" +
            "padding-bottom: 50px;" +
            "}" +
            "h1 {" +
            "font-family: Oswald;" +
            "}" +
            "h2 {" +
            "color: ${context.getColorHex(R.color.textBlackSecondary)};" +
            "font-family: Oswald;" +
            "}" +
            "h3 {" +
            "color: ${context.getColorHex(R.color.textBlackSecondary)};" +
            "font-family: Oswald;" +
            "}" +
            "p {" +
            "color: ${context.getColorHex(R.color.textBlackPrimary)};" +
            "}" +
            "a {" +
            "color: ${context.getColorHex(R.color.colorAccent)};" +
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

    override fun init(path: String) {
        api.getPostsFromDeepLink(path)
            .doOnSubscribe { loading = true }
            .doOnEvent { _, _ -> loading = false }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isNotEmpty()) init(it.first().id)
                else {
                    toaster.show(R.string.post_uri_error)
                    navigator.finishActivity()
                }
            }, Timber::e)
            .addTo(disposable)
    }

    override fun init(id: Long) {
        api.getPost(id)
            .doOnSubscribe { loading = true }
            .map(::LocalPost)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onPostSuccess, { errorManager.handleWithRetrySnack(it) { init(id) } })
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
