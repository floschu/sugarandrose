package org.sugarandrose.app.ui.home

import android.databinding.Bindable
import android.os.Bundle
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.FragmentHomeBinding
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.home.viewpager.HomePagerAdapter
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import javax.inject.Inject


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