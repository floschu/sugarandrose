package org.sugarandrose.app.ui.more

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.FragmentMoreBinding
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.more.recyclerview.MoreAdapter
import javax.inject.Inject


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface MoreMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        val adapter: MoreAdapter
    }
}


class MoreFragment : BaseFragment<FragmentMoreBinding, MoreMvvm.ViewModel>(), MoreMvvm.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_more)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.setHasFixedSize(true)
        binding.appbar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener { //todo: animate toolbartitle out left and in right or something
            internal var isShowing = false
            internal var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.toolbarTitle.text = string(R.string.name)
                    isShowing = true
                } else if (isShowing) {
                    binding.toolbarTitle.text = ""
                    isShowing = false
                }
            }
        })
    }

}


@PerFragment
class MoreViewModel @Inject
constructor(override val adapter: MoreAdapter) : BaseViewModel<MoreMvvm.View>(), MoreMvvm.ViewModel