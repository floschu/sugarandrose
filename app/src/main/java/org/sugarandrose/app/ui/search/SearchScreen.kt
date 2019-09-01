package org.sugarandrose.app.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import javax.inject.Inject
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.FragmentSearchBinding
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.categories.overview.CategoriesFragment
import org.sugarandrose.app.ui.textsearch.TextSearchFragment

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface SearchMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View>
}

class SearchFragment : BaseFragment<FragmentSearchBinding, SearchMvvm.ViewModel>(), SearchMvvm.View {
    private var isInTextSearch = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_search)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction().replace(R.id.container, CategoriesFragment(), null).commitNow()
    }

    fun goBackToOverview() {
        childFragmentManager.popBackStackImmediate()
        isInTextSearch = false
    }

    fun goToTextSearch() {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.container, TextSearchFragment(), null).addToBackStack(null).commit()
        childFragmentManager.executePendingTransactions()
        isInTextSearch = true
    }

    override fun onBackPressed(): Boolean = if (isInTextSearch) {
        goBackToOverview()
        true
    } else false
}

@PerFragment
class SearchViewModel @Inject
constructor() : BaseViewModel<SearchMvvm.View>(), SearchMvvm.ViewModel
