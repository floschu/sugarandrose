package org.sugarandrose.app.ui.onboarding

import android.databinding.Bindable
import android.os.Bundle
import javax.inject.Inject
import kotlin.Int
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.local.PrefRepo
import org.sugarandrose.app.databinding.ActivityOnboardingBinding
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.main.MainActivity
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate

private const val layout: Int = R.layout.activity_onboarding

interface OnboardingMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        @get:Bindable
        var selectedPagePosition: Int
        val adapter: OnboardingAdapter
        val pageTransformer: OnboardingPageTransformer

        fun onSkipClick()
        fun onNextClick()
    }
}

class OnboardingActivity : BaseActivity<ActivityOnboardingBinding, OnboardingMvvm.ViewModel>(), OnboardingMvvm.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndBindContentView(savedInstanceState, layout)
    }
}

@PerActivity
class OnboardingViewModel @Inject
constructor(override val adapter: OnboardingAdapter,
            override val pageTransformer: OnboardingPageTransformer,
            private val navigator: Navigator,
            private val prefRepo: PrefRepo
) : BaseViewModel<OnboardingMvvm.View>(), OnboardingMvvm.ViewModel {
    override var selectedPagePosition = 0
        set(value) {
            if (value >= OnboardingAdapter.PAGES - 1) onBoardingFinished()
            else {
                field = value
                notifyPropertyChanged(BR.selectedPagePosition)
            }
        }

    override fun onSkipClick() = onBoardingFinished()

    override fun onNextClick() {
        selectedPagePosition++
    }

    private fun onBoardingFinished() {
        prefRepo.onboardingComplete = true
        navigator.finishActivity()
        navigator.startActivity(MainActivity::class.java)
    }
}
