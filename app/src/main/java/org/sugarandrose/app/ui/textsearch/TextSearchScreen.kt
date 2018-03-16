package org.sugarandrose.app.ui.textsearch

import android.databinding.Bindable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
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
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.displayitems.PagedPostLoadingManager
import org.sugarandrose.app.ui.displayitems.recyclerview.DisplayItemAdapter
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import org.sugarandrose.app.util.PaginationScrollListener
import org.sugarandrose.app.util.extensions.hideKeyboard
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface TextSearchMvvm {
    interface View : MvvmView {
        fun hideKeyboard()
        fun toggleToolbarScrolling(enable: Boolean)
    }

    interface ViewModel : MvvmViewModel<View> {
        fun loadNextPage()

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

        binding.recyclerView.addOnScrollListener(object : PaginationScrollListener() {
            override fun loadMoreItems() = viewModel.loadNextPage()
            override fun isLoading() = viewModel.adapter.loading
        })
        binding.recyclerView.setOnTouchListener { _, _ -> hideKeyboard(); false }
//        toggleToolbarScrolling(false)
    }

    override fun hideKeyboard() {
        context?.hideKeyboard(binding.etSearch)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!isVisibleToUser) hideKeyboard()
    }

    override fun toggleToolbarScrolling(enable: Boolean) {
        val toolbarLayoutParams = binding.toolbar.layoutParams as AppBarLayout.LayoutParams
        binding.toolbar.layoutParams = toolbarLayoutParams.apply {
            scrollFlags = if (!enable) 0
            else AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
        }

        val appBarLayoutParams = binding.appbar.layoutParams as CoordinatorLayout.LayoutParams
        binding.appbar.layoutParams = appBarLayoutParams.apply {
            behavior = if (!enable) null
            else AppBarLayout.Behavior()
        }
    }
}


@PerFragment
class TextSearchViewModel @Inject
constructor(@FragmentDisposable private val disposable: CompositeDisposable) : BaseViewModel<TextSearchMvvm.View>(), TextSearchMvvm.ViewModel {
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
                .filter { it.isNotEmpty() }
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
            .doOnSubscribe { adapter.loading = true }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { adapter.endOfPages = it.isEmpty() }
            .doOnSuccess(adapter::add)
            .doOnError(Timber::e)
            .doOnEvent { _, _ ->
                refreshing = false
                adapter.loading = false
                hasMedia = !adapter.isEmpty
//                view?.toggleToolbarScrolling(hasMedia)
            }

    override fun onDeleteClick() {
        query = ""
        tryIt = !hasMedia
        view?.hideKeyboard()
    }
}