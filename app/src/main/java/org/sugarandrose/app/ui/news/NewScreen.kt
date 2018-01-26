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
import org.sugarandrose.app.data.model.LocalMedia
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.data.remote.TOTAL_PAGES_DEFAULT
import org.sugarandrose.app.data.remote.TOTAL_PAGES_HEADER
import org.sugarandrose.app.data.remote.parseMaxPages
import org.sugarandrose.app.databinding.FragmentNewBinding
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.news.recyclerview.PostAdapter
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import org.sugarandrose.app.util.PaginationScrollListener
import retrofit2.adapter.rxjava2.Result
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
        fun onRefresh()
        fun loadNextPage()

        @get:Bindable
        var refreshing: Boolean

        val adapter: PostAdapter
    }
}


class NewFragment : BaseFragment<FragmentNewBinding, NewMvvm.ViewModel>(), NewMvvm.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_new)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
constructor(private val api: SugarAndRoseApi) : BaseViewModel<NewMvvm.View>(), NewMvvm.ViewModel {
    override var refreshing: Boolean by NotifyPropertyChangedDelegate(false, BR.refreshing)

    override val adapter = PostAdapter()
    private var currentPostsPage = 1
    private var maximumNumberOfPostPages = TOTAL_PAGES_DEFAULT
    private var currentMediaPage = 1
    private var maximumNumberOfMediaPages = TOTAL_PAGES_DEFAULT

    override fun onResume() {
        if (adapter.isEmpty) onRefresh()
    }

    override fun onRefresh() {
        adapter.clear()
        currentPostsPage = 1
        currentMediaPage = 1
        loadNextPage()
    }

    override fun loadNextPage() {
//        Single.merge(loadPosts(), loadMedia())
        loadPosts()
                .doOnSubscribe { refreshing = true }
                .doOnError(Timber::e)
                .doOnEvent { _, _ -> refreshing = false }
                .subscribe().let { disposable.add(it) }
    }

    private fun loadPosts() = Single.just(currentPostsPage >= maximumNumberOfPostPages)
            .flatMap {
                if (it) Single.never()
                else api.getPostsPage(currentPostsPage).doOnSubscribe { currentPostsPage++ }
            }
            .doOnSuccess { maximumNumberOfPostPages = parseMaxPages(it) }
            .map { it.response()?.body() }
            .flattenAsFlowable { it }
            .flatMapSingle { post ->
                if (post.featured_media != 0L) api.getMedia(post.featured_media).map { LocalPost(post, it) }
                else Single.just(LocalPost(post))
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { adapter.add(it) }
            .subscribeOn(AndroidSchedulers.mainThread())

    private fun loadMedia() = Single.just(currentMediaPage >= maximumNumberOfMediaPages)
            .flatMap {
                if (it) Single.never()
                else api.getMediaPage(currentMediaPage).doOnSubscribe { maximumNumberOfMediaPages++ }
            }
            .doOnSuccess { maximumNumberOfMediaPages = parseMaxPages(it) }
            .map { it.response()?.body() }
            .flattenAsFlowable { it }
            .map { LocalMedia(it) }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { adapter.add(it) }
            .subscribeOn(AndroidSchedulers.mainThread())

}