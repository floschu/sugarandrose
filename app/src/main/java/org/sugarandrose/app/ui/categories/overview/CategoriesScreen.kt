package org.sugarandrose.app.ui.categories.overview

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.FragmentCategoriesBinding
import org.sugarandrose.app.injection.qualifier.ChildFragmentManager
import org.sugarandrose.app.injection.qualifier.FragmentDisposable
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.navigator.ChildFragmentNavigator
import org.sugarandrose.app.ui.base.navigator.FragmentNavigator
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.categories.CategoriesCacheManager
import org.sugarandrose.app.ui.categories.recyclerview.CategoriesAdapter
import org.sugarandrose.app.ui.search.SearchFragment
import org.sugarandrose.app.ui.search.SearchMvvm
import org.sugarandrose.app.ui.textsearch.TextSearchFragment
import org.sugarandrose.app.util.extensions.castWithUnwrap
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface CategoriesMvvm {
    interface View : MvvmView {
        fun openTextSearch()
    }

    interface ViewModel : MvvmViewModel<View> {
        val adapter: CategoriesAdapter

        fun onSearchClick()
    }
}


class CategoriesFragment : BaseFragment<FragmentCategoriesBinding, CategoriesMvvm.ViewModel>(), CategoriesMvvm.View {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_categories)
    }

    override fun openTextSearch() {
        if (parentFragment != null && parentFragment is SearchFragment)
            (parentFragment as SearchFragment).goToTextSearch()
    }
}


@PerFragment
class CategoriesViewModel @Inject
constructor(@FragmentDisposable private val disposable: CompositeDisposable,
            private val categoriesCacheManager: CategoriesCacheManager
) : BaseViewModel<CategoriesMvvm.View>(), CategoriesMvvm.ViewModel {

    override val adapter: CategoriesAdapter = CategoriesAdapter()

    override fun attachView(view: CategoriesMvvm.View, savedInstanceState: Bundle?) {
        super.attachView(view, savedInstanceState)
        disposable.addAll(
                categoriesCacheManager.dataSubject.subscribe({ adapter.data = it }, Timber::e),
                categoriesCacheManager.reloadData()
        )
    }

    override fun onSearchClick() {
        view?.openTextSearch()
    }
}