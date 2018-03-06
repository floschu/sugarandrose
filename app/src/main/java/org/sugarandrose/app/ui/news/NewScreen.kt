package org.sugarandrose.app.ui.news

import android.databinding.Bindable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalMedia
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.data.remote.TOTAL_PAGES_DEFAULT
import org.sugarandrose.app.data.remote.parseMaxPages
import org.sugarandrose.app.databinding.FragmentNewBinding
import org.sugarandrose.app.injection.qualifier.FragmentDisposable
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.displayitems.DisplayItemAdapter
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

        binding.recyclerView.itemAnimator = SlideInUpAnimator()
        binding.recyclerView.addOnScrollListener(object : PaginationScrollListener() {
            override fun loadMoreItems() = viewModel.loadNextPage()
            override fun isLoading() = viewModel.refreshing
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
            private val api: SugarAndRoseApi
) : BaseViewModel<NewMvvm.View>(), NewMvvm.ViewModel {
    override var refreshing: Boolean by NotifyPropertyChangedDelegate(false, BR.refreshing)

    override val adapter: DisplayItemAdapter = DisplayItemAdapter()

    private var currentPostsPage = 1
    private var maximumNumberOfPostPages = TOTAL_PAGES_DEFAULT
    private var currentMediaPage = 1
    private var maximumNumberOfMediaPages = TOTAL_PAGES_DEFAULT

    override fun onRefresh() {
        adapter.clear()
        currentPostsPage = 1
        currentMediaPage = 1
        loadNextPage()
    }

    override fun loadNextPage() {
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
            .map { it.sortedBy { it.date } }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess(adapter::add)
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
            .map { it.sortedBy { it.date } }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess(adapter::add)
            .subscribeOn(AndroidSchedulers.mainThread())

}