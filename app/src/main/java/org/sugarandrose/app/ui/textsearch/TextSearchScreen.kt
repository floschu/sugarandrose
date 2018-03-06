package org.sugarandrose.app.ui.textsearch

import android.app.Activity
import android.content.Context
import android.databinding.Bindable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.local.LocalMedia
import org.sugarandrose.app.data.model.local.LocalPost
import org.sugarandrose.app.data.model.local.TextSearchFilter
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.data.remote.TOTAL_PAGES_HEADER
import org.sugarandrose.app.data.remote.parseMaxPages
import org.sugarandrose.app.databinding.FragmentTextsearchBinding
import org.sugarandrose.app.injection.qualifier.ActivityContext
import org.sugarandrose.app.injection.qualifier.FragmentDisposable
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.displayitems.DisplayItemAdapter
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import org.sugarandrose.app.util.PaginationScrollListener
import org.sugarandrose.app.util.extensions.rxSingleChoiceDialog
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
        fun onChangeFilterClick()

        val adapter: DisplayItemAdapter
        @get:Bindable
        var loading: Boolean
        @get:Bindable
        var query: String
        @get:Bindable
        var hasMedia: Boolean
        @get:Bindable
        var tryIt: Boolean
        @get:Bindable
        var currentFilter: TextSearchFilter
    }
}


class TextSearchFragment : BaseFragment<FragmentTextsearchBinding, TextSearchMvvm.ViewModel>(), TextSearchMvvm.View {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_textsearch)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.itemAnimator = SlideInUpAnimator()
        binding.recyclerView.addOnScrollListener(object : PaginationScrollListener() {
            override fun loadMoreItems() = viewModel.loadNextPage()
            override fun isLoading() = viewModel.loading
        })
        binding.recyclerView.setOnTouchListener { _, _ -> hideKeyboard(); false }
        toggleToolbarScrolling(false)
    }

    override fun hideKeyboard() {
        val inputMethodManager = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) inputMethodManager.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
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
constructor(@ActivityContext private val context: Context,
            @FragmentDisposable private val disposable: CompositeDisposable,
            private val api: SugarAndRoseApi
) : BaseViewModel<TextSearchMvvm.View>(), TextSearchMvvm.ViewModel {
    override var loading: Boolean by NotifyPropertyChangedDelegate(false, BR.loading)
    override var hasMedia: Boolean by NotifyPropertyChangedDelegate(false, BR.hasMedia)
    override var tryIt: Boolean by NotifyPropertyChangedDelegate(true, BR.tryIt)
    override var currentFilter: TextSearchFilter by NotifyPropertyChangedDelegate(TextSearchFilter.POSTS, BR.currentFilter)
    override var query: String = ""
        set(value) {
            field = value
            querySubject.onNext(value)
            notifyPropertyChanged(BR.query)
        }

    override val adapter: DisplayItemAdapter = DisplayItemAdapter()

    private val querySubject = PublishSubject.create<String>()
    private var currentPage = 0
    private var maximumNumberOfPages = 10

    override fun attachView(view: TextSearchMvvm.View, savedInstanceState: Bundle?) {
        super.attachView(view, savedInstanceState)
        querySubject.distinctUntilChanged()
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter { it.isNotEmpty() }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    tryIt = false
                    adapter.clear()
                    currentPage = 1
                }
                .flatMapSingle(this::loadPosts)
                .subscribe().let { disposable.add(it) }
    }

    override fun loadNextPage() {
        //todo load media/roses
        if (currentPage >= maximumNumberOfPages) return
        currentPage++
        loadPosts(query).subscribe().let { disposable.add(it) }
    }

    private fun loadPosts(searchQuery: String) = api.getPostsForQuery(searchQuery, currentPage)
            .doOnSuccess { it.response()?.headers()?.values(TOTAL_PAGES_HEADER)?.firstOrNull()?.toInt()?.let { maximumNumberOfPages = it } }
            .map { it.response()?.body() }
            .flattenAsFlowable { it }
            .flatMapSingle { post ->
                if (post.featured_media != 0L) api.getMedia(post.featured_media).map { LocalPost(post, it) }
                else Single.just(LocalPost(post))
            }
            .toList()
            .map { it.sortedBy { it.date } }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { loading = true }
            .doOnSuccess(adapter::add)
            .doOnSuccess { loading = false }
            .doOnError(Timber::e)
            .doOnEvent { _, _ ->
                hasMedia = !adapter.isEmpty
                view?.toggleToolbarScrolling(hasMedia)
            }

    //todo
    private fun loadMedia(searchQuery: String) = Single.just(currentPage >= maximumNumberOfPages)
            .flatMap {
                if (it) Single.never()
                else api.getMediaPage(currentPage).doOnSubscribe { maximumNumberOfPages++ }
            }
            .doOnSuccess { maximumNumberOfPages = parseMaxPages(it) }
            .map { it.response()?.body() }
            .flattenAsFlowable { it }
            .map { LocalMedia(it) }
            .toList()
            .map { it.sortedBy { it.date } }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess(adapter::add)
            .subscribeOn(AndroidSchedulers.mainThread())

    //todo
    private fun loadRoses(searchQuery: String) = Single.just(currentPage >= maximumNumberOfPages)

    override fun onDeleteClick() {
        query = ""
        view?.hideKeyboard()
    }

    override fun onChangeFilterClick() {
        context.rxSingleChoiceDialog(R.string.search_types_title, R.array.search_types, TextSearchFilter.valueOf(currentFilter.toString()).ordinal)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onChangeFilter).addTo(disposable)
    }

    private fun onChangeFilter(index: Int) {
        tryIt = true
        adapter.clear()
        currentPage = 1
        currentFilter = TextSearchFilter.values()[index]
    }
}