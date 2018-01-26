package org.sugarandrose.app.ui.textsearch

import android.annotation.SuppressLint
import android.app.Activity
import android.databinding.Bindable
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.data.remote.TOTAL_PAGES_HEADER
import org.sugarandrose.app.databinding.FragmentTextsearchBinding
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.news.recyclerview.PostAdapter
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import org.sugarandrose.app.util.PaginationScrollListener
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
    }

    interface ViewModel : MvvmViewModel<View> {
        fun loadNextPage()

        val adapter: PostAdapter
        @get:Bindable
        var loading: Boolean
        @get:Bindable
        var query: String
        @get:Bindable
        var hasMedia: Boolean
    }
}


class TextSearchFragment : BaseFragment<FragmentTextsearchBinding, TextSearchMvvm.ViewModel>(), TextSearchMvvm.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_textsearch)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.itemAnimator = SlideInUpAnimator()
        binding.recyclerView.addOnScrollListener(object : PaginationScrollListener() {
            override fun loadMoreItems() = viewModel.loadNextPage()
            override fun isLoading() = viewModel.loading
        })
        binding.content.setOnTouchListener { _, _ -> hideKeyboard(); false }
    }

    override fun hideKeyboard() {
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) inputMethodManager.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }
}


@PerFragment
class TextSearchViewModel @Inject
constructor(private val api: SugarAndRoseApi) : BaseViewModel<TextSearchMvvm.View>(), TextSearchMvvm.ViewModel {
    override var loading: Boolean by NotifyPropertyChangedDelegate(false, BR.loading)
    override var hasMedia: Boolean by NotifyPropertyChangedDelegate(false, BR.hasMedia)
    override var query: String = ""
        set(value) {
            field = value
            querySubject.onNext(value)
            notifyPropertyChanged(BR.query)
        }

    override val adapter = PostAdapter()
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
                    adapter.clear()
                    currentPage = 1
                }
                .flatMapSingle(this::loadPage)
                .subscribe().let { disposable.add(it) }
    }

    override fun loadNextPage() {
        if (currentPage >= maximumNumberOfPages) return
        currentPage++
        loadPage(query).subscribe().let { disposable.add(it) }
    }

    private fun loadPage(searchQuery: String) = api.getPostsForQuery(searchQuery, currentPage)
            .doOnSuccess { it.response()?.headers()?.values(TOTAL_PAGES_HEADER)?.firstOrNull()?.toInt()?.let { maximumNumberOfPages = it } }
            .map { it.response()?.body() }
            .flattenAsFlowable { it }
            .flatMapSingle { post ->
                if (post.featured_media != 0L) api.getMedia(post.featured_media).map { LocalPost(post, it) }
                else Single.just(LocalPost(post))
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { loading = true }
            .doOnSuccess { adapter.add(it) }
            .doOnSuccess { loading = false }
            .doOnError(Timber::e)
            .doOnEvent { _, _ -> hasMedia = !adapter.isEmpty }

}