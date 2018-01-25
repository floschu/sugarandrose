package org.sugarandrose.app.ui.textsearch

import android.databinding.Bindable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.remote.SugarAndRoseApi
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
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        fun loadNextPage()

        val adapter: PostAdapter
        @get:Bindable
        var loading: Boolean
        @get:Bindable
        var query: String
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
    }

}


@PerFragment
class TextSearchViewModel @Inject
constructor(private val api: SugarAndRoseApi) : BaseViewModel<TextSearchMvvm.View>(), TextSearchMvvm.ViewModel {
    override var loading: Boolean by NotifyPropertyChangedDelegate(false, BR.loading)
    override var query: String = ""
        set(value) {
            field = value
            querySubject.onNext(value)
            notifyPropertyChanged(BR.query)
        }

    override val adapter = PostAdapter()
    private val querySubject = PublishSubject.create<String>()
    private var currentPage = 1

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
        if (currentPage >= 10) return
        currentPage++
        loadPage(query).subscribe().let { disposable.add(it) }
    }

    private fun loadPage(searchQuery: String) = api.getPostsForQuery(searchQuery, currentPage)
            .flattenAsFlowable { it }
            .flatMapSingle { post ->
                if (post.featured_media != 0) api.getMedia(post.featured_media).map { LocalPost(post, it) }
                else Single.just(LocalPost(post))
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { loading = true }
            .doOnSuccess { adapter.add(it.sortedByDescending { it.date }) }
            .doOnSuccess { loading = false }
            .doOnError(Timber::e)
            .onErrorReturn { emptyList() }
}