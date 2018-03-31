package org.sugarandrose.app.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.widget.ImageView
import javax.inject.Inject
import org.sugarandrose.app.R
import org.sugarandrose.app.data.local.PrefRepo
import org.sugarandrose.app.databinding.ActivityOnboardingBinding
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.main.MainActivity

private const val layout: Int = R.layout.activity_onboarding

interface OnboardingMvvm {
    interface View : MvvmView {
        fun startTransition()
    }

    interface ViewModel : MvvmViewModel<View> {
        fun onDoneClick()
    }
}

class OnboardingActivity : BaseActivity<ActivityOnboardingBinding, OnboardingMvvm.ViewModel>(), OnboardingMvvm.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndBindContentView(savedInstanceState, layout)
    }

    override fun startTransition() {
        val image = findViewById<ImageView>(R.id.iv_logo)
        val transition = ActivityOptionsCompat.makeSceneTransitionAnimation(this, image, ViewCompat.getTransitionName(image))

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent, transition.toBundle())
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}

@PerActivity
class OnboardingViewModel @Inject
constructor(private val prefRepo: PrefRepo) : BaseViewModel<OnboardingMvvm.View>(), OnboardingMvvm.ViewModel {

    override fun onDoneClick() {
        prefRepo.onboardingDone = true
        view?.startTransition()
    }
}
