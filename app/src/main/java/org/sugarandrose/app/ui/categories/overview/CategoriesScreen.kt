package org.sugarandrose.app.ui.categories.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Bindable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalCategory
import org.sugarandrose.app.databinding.FragmentCategoriesBinding
import org.sugarandrose.app.injection.qualifier.FragmentDisposable
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.categories.CategoriesCacheManager
import org.sugarandrose.app.ui.categories.recyclerview.CategoriesAdapter
import org.sugarandrose.app.ui.search.SearchFragment
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import org.sugarandrose.app.util.manager.ErrorManager
import org.sugarandrose.app.util.manager.TutorialManager
import timber.log.Timber

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface CategoriesMvvm {
    interface View : MvvmView {
        val tutorialView: android.view.View
        fun openTextSearch()
    }

    interface ViewModel : MvvmViewModel<View> {
        fun init()
        fun onSearchClick()

        @get:Bindable
        var refreshing: Boolean
        val adapter: CategoriesAdapter
    }
}

class CategoriesFragment : BaseFragment<FragmentCategoriesBinding, CategoriesMvvm.ViewModel>(), CategoriesMvvm.View {
    override val tutorialView: View get() = binding.etSearch

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_categories)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init()
    }

    override fun openTextSearch() {
        if (parentFragment != null && parentFragment is SearchFragment)
            (parentFragment as SearchFragment).goToTextSearch()
    }
}

@PerFragment
class CategoriesViewModel @Inject
constructor(
    @FragmentDisposable private val disposable: CompositeDisposable,
    private val categoriesCacheManager: CategoriesCacheManager,
    private val errorManager: ErrorManager,
    private val tutorialManager: TutorialManager
) : BaseViewModel<CategoriesMvvm.View>(), CategoriesMvvm.ViewModel {
    override var refreshing: Boolean by NotifyPropertyChangedDelegate(true, BR.refreshing)

    override val adapter: CategoriesAdapter = CategoriesAdapter()

    override fun attachView(view: CategoriesMvvm.View, savedInstanceState: Bundle?) {
        super.attachView(view, savedInstanceState)
        refreshing = true
        disposable.addAll(categoriesCacheManager.dataSubject.subscribe(this::fillAdapter, Timber::e), reloadData())
    }

    override fun init() {
        view?.let { tutorialManager.search(it.tutorialView) }
    }

    private fun reloadData() = categoriesCacheManager.reloadData { errorManager.handleWithToast(it) }

    private fun fillAdapter(items: List<LocalCategory>) {
        if (items.isNotEmpty()) refreshing = false
        adapter.data = items
    }

    override fun onSearchClick() {
        view?.openTextSearch()
    }
}
