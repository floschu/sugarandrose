package org.sugarandrose.app.ui.main

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import androidx.annotation.IdRes
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.ActivityMainBinding
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.home.HomeFragment
import org.sugarandrose.app.ui.post.PostActivity
import org.sugarandrose.app.ui.roses.RosesCacheManager
import org.sugarandrose.app.util.manager.WebManager

const val PUSH_POST_ID_INTENT = "org.sugarandrose.app.ui.main.MainActivity.post_id_intent"

interface MainMvvm {

    interface View : MvvmView {
        fun setSelectedBnvTab(@IdRes menuId: Int, viewpagerPage: Int = 0)
    }

    interface ViewModel : MvvmViewModel<View> {
        fun parseIntentUri(uri: Uri)
        fun openPost(postId: Long)
    }
}

class MainActivity : BaseActivity<ActivityMainBinding, MainMvvm.ViewModel>(), MainMvvm.View {
    private lateinit var adapter: MainAdapter
    @Inject
    lateinit var rosesCacheManager: RosesCacheManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportPostponeEnterTransition()
        setAndBindContentView(savedInstanceState, R.layout.activity_main)

        adapter = MainAdapter(supportFragmentManager, R.id.container, R.id.bnv_new, savedInstanceState).apply {
            setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            attachTo(binding.bottomNavigationView)
        }
        intent?.let { handleIntent(it) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(newIntent: Intent) {
        val pushId = newIntent.getStringExtra(PUSH_POST_ID_INTENT)
        val uri = newIntent.data
        if (pushId != null) viewModel.openPost(pushId.toLong())
        else if (uri != null) viewModel.parseIntentUri(uri)
    }

    override fun onResume() {
        super.onResume()
        rosesCacheManager.checkReloadData().addTo(disposable)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        adapter.onSaveInstanceState(outState)
    }

    override fun setSelectedBnvTab(@IdRes menuId: Int, viewpagerPage: Int) {
        binding.bottomNavigationView.selectedItemId = menuId
        (supportFragmentManager.fragments[0] as? HomeFragment)?.setSelectedPage(viewpagerPage)
    }

    override fun onBackPressed() {
        var handled = false
        for (f in supportFragmentManager.fragments) {
            if (f is BaseFragment<*, *>) {
                handled = f.onBackPressed()
                if (handled) break
            }
        }
        if (!handled) finish()
    }
}

@PerActivity
class MainViewModel @Inject
constructor(
    private val navigator: Navigator,
    private val resources: Resources,
    private val webManager: WebManager
) : BaseViewModel<MainMvvm.View>(), MainMvvm.ViewModel {
    private val postRegex = Regex("""2[0-9]{3}/[0-9]{2}/[0-9]{2}/*/""")

    override fun parseIntentUri(uri: Uri) {
        when {
            uri.lastPathSegment != null && uri.lastPathSegment == resources.getString(R.string.deeplink_roses) -> view?.setSelectedBnvTab(R.id.bnv_new, 1)
            uri.query != null && uri.query!!.contains(resources.getString(R.string.deeplink_post_query)) -> openPost(uri.query!!.removePrefix("${resources.getString(R.string.deeplink_post_query)}=").toLong())
//           uri.path != null && uri.path.contains(postRegex) -> navigator.startActivity(PostActivity::class.java, { putExtra(Navigator.EXTRA_ARG, uri.path) }) todo get id from url
            else -> webManager.open(uri)
        }
    }

    override fun openPost(postId: Long) = navigator.startActivity(PostActivity::class.java, { putExtra(Navigator.EXTRA_ARG, postId) })
}
