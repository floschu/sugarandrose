package org.sugarandrose.app.ui.news

import android.databinding.Bindable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.data.remote.TOTAL_PAGES_HEADER
import org.sugarandrose.app.databinding.FragmentNewBinding
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.news.recyclerview.PostAdapter
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import org.sugarandrose.app.util.PaginationScrollListener
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface NewMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        fun onResume()

        @get:Bindable
        var refreshing: Boolean

        fun onRefresh()

        val adapter: PostAdapter
        fun loadNextPage()
    }
}


class NewFragment : BaseFragment<FragmentNewBinding, NewMvvm.ViewModel>(), NewMvvm.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_new)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.itemAnimator = SlideInUpAnimator()
        binding.recyclerView.addOnScrollListener(object : PaginationScrollListener() {
            override fun loadMoreItems() = viewModel.loadNextPage()
            override fun isLoading() = viewModel.refreshing
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }
}


@PerFragment
class NewViewModel @Inject
constructor(private val api: SugarAndRoseApi, override val adapter: PostAdapter) : BaseViewModel<NewMvvm.View>(), NewMvvm.ViewModel {
    override var refreshing: Boolean by NotifyPropertyChangedDelegate(false, BR.refreshing)

    private var currentPage = 1
    private var maximumNumberOfPages = 100

    override fun onResume() {
        if (adapter.isEmpty) onRefresh()
    }

    override fun onRefresh() {
        adapter.clear()
        currentPage = 1
        api.getNumberOfPages()
                .doOnSuccess { it.response()?.headers()?.values(TOTAL_PAGES_HEADER)?.firstOrNull()?.toInt()?.let { maximumNumberOfPages = it } }
                .toCompletable()
                .andThen(loadPage())
                .subscribe().let { disposable.add(it) }
    }

    override fun loadNextPage() {
        if (currentPage >= maximumNumberOfPages) return
        currentPage++
        loadPage().subscribe().let { disposable.add(it) }
    }

    private fun loadPage() = api.getPosts(currentPage)
            .flattenAsFlowable { it }
            .flatMapSingle { post ->
                if (post.featured_media != 0) api.getMedia(post.featured_media).map { LocalPost(post, it) }
                else Single.just(LocalPost(post))
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { refreshing = true }
            .doOnSuccess { adapter.add(it.sortedByDescending { it.date }) }
            .doOnError(Timber::e)
            .doOnEvent { _, _ -> refreshing = false }
}