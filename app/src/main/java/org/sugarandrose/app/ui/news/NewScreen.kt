package org.sugarandrose.app.ui.news

import android.databinding.Bindable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.remote.SugarAndRoseApi
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
        var currentPage: Int

        val adapter: PostAdapter
        @get:Bindable
        var refreshing: Boolean

        fun onRefresh()
        fun loadNextPage(doneCallback: (() -> Unit)? = null)
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
        binding.recyclerView.addOnScrollListener(object : PaginationScrollListener(binding.recyclerView.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                viewModel.adapter.isLoading = true
                viewModel.loadNextPage { viewModel.adapter.isLoading = false }
            }

            override fun isLoading() = viewModel.adapter.isLoading
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.onRefresh()
    }
}


@PerFragment
class NewViewModel @Inject
constructor(private val api: SugarAndRoseApi, override val adapter: PostAdapter) : BaseViewModel<NewMvvm.View>(), NewMvvm.ViewModel {
    override var refreshing: Boolean by NotifyPropertyChangedDelegate(false, BR.refreshing)

    override var currentPage = 1

    override fun onRefresh() {
        adapter.clear()
        currentPage = 1
        refreshing = true
        loadNextPage { refreshing = false }
    }

    override fun loadNextPage(doneCallback: (() -> Unit)?) {
        currentPage += 1
        api.getPosts(currentPage).flattenAsFlowable { it }
                .flatMapSingle { post -> api.getMedia(post.featured_media).map { LocalPost(post, it) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ adapter.add(it) }, { Timber.e(it); doneCallback?.invoke() }, { doneCallback?.invoke() }).let { disposable.add(it) }
    }
}