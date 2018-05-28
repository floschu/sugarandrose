package org.sugarandrose.app.ui.news

import android.databinding.Bindable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.FragmentNewBinding
import org.sugarandrose.app.injection.qualifier.FragmentDisposable
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.displayitems.PagedPostLoadingManager
import org.sugarandrose.app.ui.displayitems.recyclerview.DisplayItemAdapter
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import org.sugarandrose.app.util.pagination.PaginationScrollListener
import org.sugarandrose.app.util.manager.ErrorManager
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface NewMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        fun onRefresh()
        fun loadNextPage()

        @get:Bindable
        var refreshing: Boolean

        val adapter: DisplayItemAdapter
    }
}


class NewFragment : BaseFragment<FragmentNewBinding, NewMvvm.ViewModel>(), NewMvvm.View {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_new)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.addOnScrollListener(object : PaginationScrollListener(7) {
            override fun loadMoreItems() = viewModel.loadNextPage()
            override fun isLoading() = viewModel.adapter.loading
        })
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.adapter.isEmpty) viewModel.onRefresh()
    }
}


@PerFragment
class NewViewModel @Inject
constructor(@FragmentDisposable private val disposable: CompositeDisposable,
            private val errorManager: ErrorManager
) : BaseViewModel<NewMvvm.View>(), NewMvvm.ViewModel {
    override var refreshing: Boolean by NotifyPropertyChangedDelegate(false, BR.refreshing)

    override val adapter: DisplayItemAdapter = DisplayItemAdapter()

    private val pagedPostLoadingManager = PagedPostLoadingManager()

    override fun onRefresh() {
        adapter.clear()
        pagedPostLoadingManager.resetPages()
        refreshing = true
        loadNextPage()
    }

    override fun loadNextPage() {
        pagedPostLoadingManager.loadPostsPage()
                .doOnSubscribe { adapter.loading = true }
                .doOnEvent { _, _ ->
                    refreshing = false
                    adapter.loading = false
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { adapter.endOfPages = it.isEmpty() }
                .subscribe(adapter::add, { errorManager.handleWithToast(it, this::onRefresh) })
                .addTo(disposable)
    }
}