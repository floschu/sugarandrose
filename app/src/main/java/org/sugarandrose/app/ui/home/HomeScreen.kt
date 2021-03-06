package org.sugarandrose.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Bindable
import androidx.fragment.app.FragmentPagerAdapter
import javax.inject.Inject
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.FragmentHomeBinding
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.home.viewpager.HomePagerAdapter
import org.sugarandrose.app.ui.launcher.LauncherActivity
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface HomeMvvm {
    interface View : MvvmView {
        fun setSelectedPage(page: Int)
    }

    interface ViewModel : MvvmViewModel<View> {

        @get:Bindable
        var selectedPagePosition: Int
        val adapter: FragmentPagerAdapter
    }
}

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeMvvm.ViewModel>(), HomeMvvm.View {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_home)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.includeToolbar.ivLogo.transitionName = LauncherActivity.logoTransitionName
        activity?.supportStartPostponedEnterTransition()
    }

    override fun setSelectedPage(page: Int) {
        viewModel.selectedPagePosition = page
    }
}

@PerFragment
class HomeViewModel @Inject
constructor(override val adapter: HomePagerAdapter) : BaseViewModel<HomeMvvm.View>(), HomeMvvm.ViewModel {
    override var selectedPagePosition: Int by NotifyPropertyChangedDelegate(0, BR.selectedPagePosition)
}
