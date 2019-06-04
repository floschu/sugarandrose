package org.sugarandrose.app.ui.launcher

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.widget.ImageView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject
import kotlin.Int
import org.sugarandrose.app.R
import org.sugarandrose.app.data.local.PrefRepo
import org.sugarandrose.app.databinding.ActivityLauncherBinding
import org.sugarandrose.app.injection.qualifier.ActivityDisposable
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.main.MainActivity
import org.sugarandrose.app.ui.onboarding.OnboardingActivity
import java.util.concurrent.TimeUnit

private const val layout: Int = R.layout.activity_launcher

interface LauncherMvvm {
    interface View : MvvmView {
        fun startTransition(onboadingDone: Boolean)
    }

    interface ViewModel : MvvmViewModel<View> {
        fun init()
    }
}

class LauncherActivity : BaseActivity<ActivityLauncherBinding, LauncherMvvm.ViewModel>(), LauncherMvvm.View {
    companion object {
        const val logoTransitionName = "logo_transition_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndBindContentView(savedInstanceState, layout)

        binding.ivLogo.transitionName = logoTransitionName
        viewModel.init()
    }

    override fun startTransition(onboadingDone: Boolean) {
        val image = findViewById<ImageView>(R.id.iv_logo)
        val transition = ActivityOptionsCompat.makeSceneTransitionAnimation(this, image, logoTransitionName)

        val intent =
                if (onboadingDone) Intent(this, MainActivity::class.java)
                else Intent(this, OnboardingActivity::class.java)

        startActivity(intent, transition.toBundle())
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}

@PerActivity
class LauncherViewModel @Inject
constructor(@ActivityDisposable private val disposable: CompositeDisposable,
            private val prefRepo: PrefRepo
) : BaseViewModel<LauncherMvvm.View>(), LauncherMvvm.ViewModel {

    override fun init() {
        view?.let {
            Single.timer(1, TimeUnit.SECONDS)
                    .map { prefRepo.onboardingDone }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(it::startTransition)
                    .addTo(disposable)
        }
    }
}
