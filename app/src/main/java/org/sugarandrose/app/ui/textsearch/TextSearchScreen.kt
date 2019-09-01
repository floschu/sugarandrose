package org.sugarandrose.app.ui.textsearch

import androidx.databinding.Bindable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.FragmentTextsearchBinding
import org.sugarandrose.app.injection.qualifier.FragmentDisposable
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.feedback.Snacker
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.displayitems.PagedPostLoadingManager
import org.sugarandrose.app.ui.displayitems.recyclerview.DisplayItemAdapter
import org.sugarandrose.app.ui.search.SearchFragment
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import org.sugarandrose.app.util.pagination.PaginationScrollListener
import org.sugarandrose.app.util.extensions.hideKeyboard
import org.sugarandrose.app.util.extensions.showKeyboard
import org.sugarandrose.app.util.manager.ErrorManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface TextSearchMvvm {
    interface View : MvvmView {
        fun hideKeyboard()

        fun goBackToOverview()
    }

    interface ViewModel : MvvmViewModel<View> {
        fun loadNextPage()

        fun onBackClick()
        fun onDeleteClick()

        val adapter: DisplayItemAdapter
        @get:Bindable
        var refreshing: Boolean
        @get:Bindable
        var query: String
        @get:Bindable
        var hasMedia: Boolean
        @get:Bindable
        var tryIt: Boolean
    }
}


class TextSearchFragment : BaseFragment<FragmentTextsearchBinding, TextSearchMvvm.ViewModel>(), TextSearchMvvm.View {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_textsearch)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.addOnScrollListener(object : PaginationScrollListener(7) {
            override fun loadMoreItems() = viewModel.loadNextPage()
            override fun isLoading() = viewModel.adapter.loading
        })
        binding.recyclerView.setOnTouchListener { _, _ -> hideKeyboard(); false }

        binding.etSearch.requestFocus()
        context?.showKeyboard(binding.etSearch)
    }

    override fun hideKeyboard() {
        context?.hideKeyboard(binding.etSearch)
    }

    override fun goBackToOverview() {
        if (parentFragment != null && parentFragment is SearchFragment)
            (parentFragment as SearchFragment).goBackToOverview()
    }
}


@PerFragment
class TextSearchViewModel @Inject
constructor(@FragmentDisposable private val disposable: CompositeDisposable,
            private val errorManager: ErrorManager
) : BaseViewModel<TextSearchMvvm.View>(), TextSearchMvvm.ViewModel {

    override var refreshing: Boolean by NotifyPropertyChangedDelegate(false, BR.refreshing)
    override var hasMedia: Boolean by NotifyPropertyChangedDelegate(false, BR.hasMedia)
    override var tryIt: Boolean by NotifyPropertyChangedDelegate(true, BR.tryIt)
    override var query: String = ""
        set(value) {
            field = value
            querySubject.onNext(value)
            notifyPropertyChanged(BR.query)
        }

    override val adapter: DisplayItemAdapter = DisplayItemAdapter()

    private val pagedPostLoadingManager = PagedPostLoadingManager()
    private val querySubject = PublishSubject.create<String>()

    override fun attachView(view: TextSearchMvvm.View, savedInstanceState: Bundle?) {
        super.attachView(view, savedInstanceState)
        querySubject.distinctUntilChanged()
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter(String::isNotEmpty)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    tryIt = false
                    adapter.clear()
                    pagedPostLoadingManager.resetPages()
                    refreshing = true
                }
                .flatMapSingle(this::loadPage)
                .subscribe().addTo(disposable)
    }

    override fun loadNextPage() {
        loadPage(query).subscribe().addTo(disposable)
    }

    private fun loadPage(query: String) = pagedPostLoadingManager.loadQueryPage(query)
            .onErrorReturnItem(emptyList())
            .doOnSubscribe { adapter.loading = true }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { adapter.endOfPages = it.isEmpty() }
            .doOnSuccess(adapter::add)
            .doOnError { errorManager.handleWithRetrySnack(it, this::loadNextPage) }
            .doOnEvent { _, _ ->
                refreshing = false
                adapter.loading = false
                hasMedia = !adapter.isEmpty
            }

    override fun onDeleteClick() {
        query = ""
        tryIt = !hasMedia
        view?.hideKeyboard()
    }

    override fun onBackClick() {
        view?.hideKeyboard()
        view?.goBackToOverview()
    }
}