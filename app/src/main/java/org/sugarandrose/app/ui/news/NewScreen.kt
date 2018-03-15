package org.sugarandrose.app.ui.news

import android.databinding.Bindable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
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
            private val pagedPostLoadingManager: PagedPostLoadingManager
) : BaseViewModel<NewMvvm.View>(), NewMvvm.ViewModel {
    override var refreshing: Boolean by NotifyPropertyChangedDelegate(false, BR.refreshing)

    override val adapter: DisplayItemAdapter = DisplayItemAdapter()

    override fun onRefresh() {
        adapter.clear()
        pagedPostLoadingManager.resetPages()
        loadNextPage()
    }

    override fun loadNextPage() {
        pagedPostLoadingManager.loadPostsPage()
                .doOnSubscribe { refreshing = true }
                .doOnEvent { _, _ -> refreshing = false }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::add, Timber::e)
                .addTo(disposable)
    }
}