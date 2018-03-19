package org.sugarandrose.app.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import javax.inject.Inject
import kotlin.Int
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.FragmentOnboardingItemBinding
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel

private const val layout: Int = R.layout.fragment_onboarding_item

interface OnboardingItemMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View>
}

class OnboardingItemFragment : BaseFragment<FragmentOnboardingItemBinding, OnboardingItemMvvm.ViewModel>(), OnboardingItemMvvm.View {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            setAndBindContentView(inflater, container, savedInstanceState, layout)
}

@PerFragment
class OnboardingItemViewModel @Inject
constructor() : BaseViewModel<OnboardingItemMvvm.View>(), OnboardingItemMvvm.ViewModel {

}
