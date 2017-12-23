package org.sugarandrose.app.ui.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.sugarandrose.app.R
import org.sugarandrose.app.SugarAndRoseApp
import org.sugarandrose.app.databinding.FragmentTestBinding
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.main.MainActivity
import org.sugarandrose.app.util.NotificationsManager
import javax.inject.Inject
import android.net.Uri
import org.sugarandrose.app.BuildConfig
import org.sugarandrose.app.injection.qualifier.ActivityContext
import android.support.v4.content.ContextCompat.startActivity
import android.content.ActivityNotFoundException
import org.sugarandrose.app.ui.main.IntentForwardingActivity


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface TestMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        fun pushNotification()
        fun onFacebookClick()
        fun onInstagramClick()
    }
}


class TestFragment : BaseFragment<FragmentTestBinding, TestMvvm.ViewModel>(), TestMvvm.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_test)
    }

}


@PerFragment
class TestViewModel @Inject
constructor(@ActivityContext private val context: Context, private val notificationsManager: NotificationsManager, private val navigator: Navigator) : BaseViewModel<TestMvvm.View>(), TestMvvm.ViewModel {

    override fun pushNotification() {
        notificationsManager.pushRemote(Intent(SugarAndRoseApp.instance, IntentForwardingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data = Uri.parse("https://sugarandrose.org/2017/12/24/adventskalender-tuer-24/")
            action = Intent.ACTION_VIEW
        }, "Adventskalender Tür 24", "vom 24.12.2017", "Neuer Post")
    }

    override fun onFacebookClick() {
        val facebookUrl = "https://www.facebook.com/${BuildConfig.FB_NAME}"
        val uri = try {
            val applicationInfo = context.applicationContext.packageManager.getApplicationInfo("com.facebook.katana", 0)
            if (applicationInfo.enabled) Uri.parse("fb://page/${BuildConfig.FB_ID}")
            else Uri.parse(facebookUrl)
        } catch (e: Exception) {
            Uri.parse(facebookUrl)
        }
        navigator.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    override fun onInstagramClick() {
        val uri = Uri.parse("http://instagram.com/_u/${BuildConfig.INSTA_NAME}")
        try {
            navigator.startActivity(Intent(Intent.ACTION_VIEW, uri).apply { `package` = "com.instagram.android" })
        } catch (e: Exception) {
            navigator.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }
}